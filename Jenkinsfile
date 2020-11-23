pipeline {
    agent any
    stages {
        stage('build') {
            steps {
                echo 'hello!'
                publishChecks name: 'example', title: 'Pipeline Check', summary: 'check through pipeline', text: 'you can publish checks in pipeline script', detailsURL: 'https://github.com/jenkinsci/checks-api-plugin#pipeline-usage'

            }
        }
    }
}
