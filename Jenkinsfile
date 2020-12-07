def bot
def toolbelt

pipeline {
  triggers {
    GenericTrigger(
     genericVariables: [
      [key: 'ref', value: '$.review.state']
     ],

     token: 'review',
    
    )
  }
    agent any
    environment {
        CURRENT_USER = null
        QA_USER = credentials('Jenkins_QA_User')
        QA_CONSUMER_KEY = credentials('QA_CONSUMER_KEY')
        
    }
    stages {
        stage('Init') {
            steps {
                script {
                    toolbelt =  tool 'toolbelt' 
                    bot = load "JenkinsHelper.groovy"
                }
            }
            
        }

        stage('SFDX Check Deploy') {
            when { expression { return ! pullRequest.labels.contains(env.Deployable)}}
            steps {
                withCredentials([file(credentialsId: 'QA_KEY', variable: 'QaKey')]) {
                    
                    script {
                        publishChecks name: 'Deploy check', title: 'Job will start shortly...', status: 'QUEUED', conclusion: 'NONE'
                        
                        CURRENT_USER = QA_USER
                        echo "Authenticating into Org"
                        
                        def deployCheckSuccess = true
                        def result = null
                       
                        authorized = sh (script: "${toolbelt}/sfdx force:auth:jwt:grant --clientid ${QA_CONSUMER_KEY} -u ${CURRENT_USER} -f ${QaKey} -r https://login.salesforce.com --setdefaultusername",  returnStatus: true) == 0
                        
                        if(authorized) {
                            echo "Starting Deploy Check"

                            publishChecks name: 'Deploy check', title: 'In Progress', status: 'IN_PROGRESS', conclusion: 'NONE'
                            deployCheckSuccess = sh (script: "${toolbelt}/sfdx force:source:deploy --checkonly -l RunLocalTests -p force-app/main/default/ --json > output.txt",  returnStatus: true) == 0
                            
                            if(deployCheckSuccess) {
                                echo "Deploy check OK"
                                publishChecks name: 'Deploy check', title: 'Success '
                                pullRequest.addLabel(env.Deployable)
                              
                                if (pullRequest.labels.contains(env.NotDeployable)) {
                                    pullRequest.removeLabel(env.NotDeployable)
                                }
                            } else {
                                echo "Fail deploy check"
                                def outcome = bot.getSFDXOutcome()
                                publishChecks conclusion: 'FAILURE', name: 'Deploy check', summary: outcome[0], title: 'Fail', text: outcome[1]
                                pullRequest.addLabel(env.NotDeployable)
                                
                                if (pullRequest.labels.contains(env.Deployable)) {
                                    pullRequest.removeLabel(env.Deployable)
                                }
                                sh 'rm output.txt'
                             
                             }
                        }else {
                             echo "Authentication failed"
                           
                        }
                        
                    }
                }
            }
        }
        
        stage('Deploy') {
            when { expression { return pullRequest.reviews[0]}}
            steps {
                script {
                        for (review in pullRequest.reviews) {
                            echo "${review.user} has a review in ${review.state} state for Pull Request. Review body: ${review.body}"
                        }
                       if (pullRequest.mergeable) {
                          pullRequest.merge(commitTitle: 'Make it so..', commitMessage: 'TO BOLDLY GO WHERE NO MAN HAS GONE BEFORE...', mergeMethod: 'squash')
                      }
                }
            }
            
            
        }
        stage('logout') {
            steps{
                script{
                    sh (script: "${toolbelt}/sfdx force:auth:logout --targetusername ${CURRENT_USER} -p")
                }
            }
        }
        
    }
}
