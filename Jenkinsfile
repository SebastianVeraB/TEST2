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
            toolbelt =  tool 'toolbelt' 
            bot = load "JenkinsHelper.groovy"
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
                                def output = bot.getSFDXOutcome()
                                println output
                                def outputObj = readJSON text: output
                                def summary = '<h3 id="summary-">Summary:</h3><hr>' +
                                                '<h4 id="metadata">Metadata</h4>' +
                                                '<ul>' +
                                                '<li>Components with errors: ' + outputObj.result.numberComponentErrors + '</li>' +
                                                '<li>Components total: ' + outputObj.result.numberComponentsTotal + '</li>' +
                                                '</ul>' +
                                                '<h4 id="apex-run-test">Apex run test</h4>' +
                                                '<ul>' +
                                                '<li>Failed test: ' + outputObj.result.numberTestErrors + '</li>' +
                                                '<li>Test total: ' + outputObj.result.numberTestsTotal + '</li>' +
                                                '</ul>' 
                                                if(outputObj.result.details.runTestResult.containsKey('codeCoverageWarnings')){
                                                if(outputObj.result.details.runTestResult.codeCoverageWarnings instanceof List){
                                                  summary+= '<h4 id="code-coverage-warnings">Code coverage warnings</h4>'+
                                                '<ul>'  
                                                  outputObj.result.details.runTestResult.codeCoverageWarnings.each { warning ->
                                               
                                                        if(warning.name in String) {
                                                              summary += '<li>'+ warning.name + ': ' + warning.message + '</li>'
                                                        }
                                                        else {
                                                              summary += '<li>' + warning.message + '</li>'
                                                        }
                                                  
                                                    }
                                                  summary+= '</ul>'
                                                
                                                }else {
                                                    summary+= '<h4 id="code-coverage-warnings">Code coverage warnings</h4>'+
                                                '<ul>'  + '<li>' +  outputObj.result.details.runTestResult.codeCoverageWarnings.message + '</li></ul>'
                                                }
                                                }
                              def details = ''
                              
                              def apexFailures = ''
                              if(outputObj.result.details.runTestResult.numFailures > 0) {
                                apexFailures = '<h4 id="apex-test-failures">Apex test failures</h4><ul>'
                                    outputObj.result.details.runTestResult.failures.each { failure ->
                                      if(failure != null) {
                                    apexFailures += '<li>Class: ' + failure.name + '</li>' +
                                                    '<li>Method: ' + failure.methodName + '</li>' +
                                                    '<li>Error message: ' + failure.message + '</li>' +
                                                    '<li>Stacktrace: ' + failure.stackTrace + '</li>'
                                      }
                                    }
                                apexFailures += '</ul>'
                              }  
                                  
                                details += apexFailures
                                publishChecks conclusion: 'FAILURE', name: 'Deploy check', summary: summary, title: 'Fail', text: details
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
                    def toolbelt = tool 'toolbelt' 
                    sh (script: "${toolbelt}/sfdx force:auth:logout --targetusername ${CURRENT_USER} -p")
                }
            }
        }
        
    }
}
