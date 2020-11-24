pipeline {
    triggers {
      pullRequestReview(reviewStates: ['approved'])
    }
    agent any
    options {
    skipDefaultCheckout true
    }
    stages {
         stage ('Checkout') {
             steps {
                 publishChecks conclusion: 'NEUTRAL', name: 'Deployment check', status: 'IN_PROGRESS', title: 'Running...'
                  checkout scm
             } 
         }
    

        stage('SFDX Check Deploy') {
            steps {
                publishChecks name: 'Deployment check', summary: 'This Pull Request is deployable', text: 'Reported Apex code coverage: ', title: 'Sucessful'
            }
        }
        
        stage('Deploy') {
            steps {
                script {
                    pullRequest.merge(commitTitle: 'Make it so..', commitMessage: 'TO BOLDLY GO WHERE NO MAN HAS GONE BEFORE...', mergeMethod: 'squash')
                }
            }
            when { 
                environment name: 'GITHUB_REVIEW_STATE', value: 'approved' 
            }
        }
        
    }
}
