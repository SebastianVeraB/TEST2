
pipeline {
    agent any
    options {
    skipDefaultCheckout true
    }
    stages {
         stage ('Checkout') {
             steps {
                 publishChecks conclusion: 'NEUTRAL', name: 'Nebula', status: 'IN_PROGRESS', summary: 'Testing Summary', text: 'Testing text', title: 'Testing Title'
                  checkout scm
             } 
         }
    

        stage('build') {
            steps {
                echo 'hello!'
                publishChecks name: 'Nebula', summary: 'Testing Summary', text: 'Testing text', title: 'Testing Title'

            }
        }
    }
}
