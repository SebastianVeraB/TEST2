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
      toolbelt = tool name: 'toolbelt', type:     'com.cloudbees.jenkins.plugins.customtools.CustomTool'
  }
    stages {
        stage('SFDX Check Deploy') {
            steps {
                publishChecks name: 'Deployment check', summary: 'This Pull Request is deployable', text: 'Reported Apex code coverage: ', title: 'Sucessful'
              script {
                rc = command "${toolbelt}/sfdx --version"
                pullRequest.addLabel('Deployable')
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
