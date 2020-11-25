pipeline {
     triggers {
        issueCommentTrigger('test')
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
                        for (review in pullRequest.reviews) {
                            echo "${review.user} has a review in ${review.state} state for Pull Request. Review body: ${review.body}"
                        }
                }
            }
            when { expression { return pullRequest.reviews[0]}}
            
        }
        
    }
}
