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
    

        stage('build') {
            steps {
                echo 'hello!'
                publishChecks name: 'Deployment check', summary: 'This Pull Request is deployable', text: 'Reported Apex code coverage: ', title: 'Sucessful'

            }
        }
    }
}
