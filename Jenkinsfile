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
        stage('SFDX Check Deploy') {
            when { expression { return ! pullRequest.labels.contains(env.Deployable)}}
            steps {
                withCredentials([file(credentialsId: 'QA_KEY', variable: 'QaKey')]) {
                    
                    
                    script {
                        publishChecks conclusion: 'NONE', name: 'Deploy check', title: 'In Progress'
                        CURRENT_USER = QA_USER
                        echo "Authenticating into Org"
                        
                        def deployCheckSuccess = true
                        def result = null
                        def toolbelt = tool 'toolbelt' 
                        
                        authorized = sh (script: "${toolbelt}/sfdx force:auth:jwt:grant --clientid ${QA_CONSUMER_KEY} -u ${CURRENT_USER} -f ${QaKey} -r https://login.salesforce.com --setdefaultusername",  returnStatus: true) == 0
                        
                        if(authorized) {
                            echo "Starting Deploy Check"
                            
                            deployCheckSuccess = sh (script: "${toolbelt}/sfdx force:source:deploy --checkonly -l RunLocalTests -p force-app/main/default/ --json > output.txt",  returnStatus: true) == 0
                                
                            
                            echo "end sfdx command"
                             
                            if(deployCheckSuccess) {
                                echo "got success"
                                //pullRequest.addLabel(env.Deployable)
                              
                                if (pullRequest.labels.contains(env.NotDeployable)) {
                                    pullRequest.removeLabel(env.NotDeployable)
                                }
                             } else {
                                echo "fail deploy check"
                                def output = readFile('output.txt').trim()
                                def outputObj = readJSON text: output
                                def summary = '<h3 id="summary-">Summary:</h3>' +
                                                '<h5 id="metadata">Metadata</h5>' +
                                                '<ul>' +
                                                '<li>Components with errors: ' + outputObj.result.numberComponentErrors + '</li>' +
                                                '<li>Components total: ' + outputObj.result.numberComponentsTotal + '</li>' +
                                                '</ul>' +
                                                '<h5 id="apex-run-test">Apex run test</h5>' +
                                                '<ul>' +
                                                '<li>Failed test: ' + outputObj.result.numberTestErrors + '</li>' +
                                                '<li>Test total: ' + outputObj.result.numberTestsTotal + '</li>' +
                                                '</ul>' +
                                                '<h5 id="code-coverage-warnings">Code coverage warnings</h5>'+
                                                '<ul>'
                                                
                                                outputObj.result.details.runTestResult.codeCoverageWarnings.each { warning ->
                                                println warning
                                                    if(warning.name in String) {
                                                          summary += '<li>'+ warning.name + ': ' + warning.message + '</li>'
                                                    }
                                                    else {
                                                          summary += '<li>' + warning.message + '</li>'
                                                    }
                                                  
                                                }
                                                
                                                
                                publishChecks conclusion: 'FAILURE', name: 'Deploy check', summary: summary, text: '## Text', title: 'Fail'
                                pullRequest.addLabel(env.NotDeployable)
                                
                                if (pullRequest.labels.contains(env.Deployable)) {
                                    pullRequest.removeLabel(env.Deployable)
                                }
                                sh 'rm output.txt'
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
        stage('logout') {
            steps{
                script{
                    def toolbelt = tool 'toolbelt' 
                    sh (script: "${toolbelt}/sfdx force:auth:logout --targetusername ${CURRENT_USER} -p")
                }
            }
        }
        
    }
}
