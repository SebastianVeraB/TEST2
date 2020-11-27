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
    stages {
        stage('SFDX Check Deploy') {
            steps {
              script {
                def toolbelt = tool 'toolbelt' 
                sh script: "${toolbelt}/sfdx --version"
                
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
