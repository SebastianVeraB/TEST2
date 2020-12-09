def bot
def toolbelt
def check_runs


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
                    check_runs = load 'buildGithubCheckScript.groovy'
                }
            }
            
        }
         stage("Build") {
            steps {
                script {
                    withCredentials([sshUserPrivateKey(credentialsId: 'test', keyFileVariable: 'privateKey', passphraseVariable: '', usernameVariable: '')]) {
                        try {
                            echo "starting custom check"
                            check_runs.buildGithubCheck('TEST2', pullRequest.statuses.last().id, privateKey, 'success', "build")
                        } catch(Exception e) {
                           // check_runs.buildGithubCheck(<REPO_NAME>, <COMMIT_ID>, privateKey, 'failure', "build")
                            echo "Exception: ${e}"
                        }
                    }
                }
            }
        }/*
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
                        println pullRequest.mergeable
                        
                    }
                }
            }
        }
        
        stage('Deploy') {
            when { expression { return pullRequest.mergeable && pullRequest.labels.contains(env.Deployable)}}
            steps {
                script {
                        publishChecks name: 'Deploy Job', title: 'In Progress', status: 'IN_PROGRESS', conclusion: 'NONE'
                        deploySuccess = bot.deploy()
                        if(deploySuccess) {
                            pullRequest.merge(commitTitle: 'Commit Title', commitMessage: 'Commit Message.', mergeMethod: 'squash')
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
        }*/
        
    }
}
