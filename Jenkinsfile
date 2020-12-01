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
        QA_USER = credentials('Jenkins_QA_User')
        QA_CONSUMER_KEY = credentials('QA_CONSUMER_KEY')
        
    }
    stages {
        stage('SFDX Check Deploy') {
            when { expression { return ! pullRequest.labels.contains(env.Deployable)}}
            steps {
                withCredentials([file(credentialsId: 'QA_KEY', variable: 'QaKey')]) {
                    
                    
                    script {
                        
                        echo "Authenticating into Org"
                    
                        def toolbelt = tool 'toolbelt' 
                        result = sh (script: "${toolbelt}/sfdx force:auth:jwt:grant --clientid ${QA_CONSUMER_KEY} -u ${QA_USER} -f ${QaKey} -r https://login.salesforce.com -a QAMerge",  returnStatus: true)
                        
                        if(result == 0) {
                            echo "Starting Deploy Check"
                             result = sh (script: "${toolbelt}/sfdx force:source:deploy --checkonly -u QAMerge -l RunAllTestsInOrg -p force-app/main/default/",  returnStatus: true)
                             if(result == 0) {
                                 
                                pullRequest.addLabel(env.Deployable)
                              
                                if (pullRequest.labels.contains(env.NotDeployable)) {
                                    pullRequest.removeLabel(env.NotDeployable)
                                }
                             } else {
                                 
                                pullRequest.addLabel(env.NotDeployable)
                                
                                if (pullRequest.labels.contains(env.Deployable)) {
                                    pullRequest.removeLabel(env.Deployable)
                                }
                             }
                        }
                        else {
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
        
    }
}
