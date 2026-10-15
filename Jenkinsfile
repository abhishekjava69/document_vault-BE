
pipeline {
    agent any

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build and Test') {
            steps {
                sh '''
                    mvn -B -f api-gateway/pom.xml clean verify
                    mvn -B -f auth-service/pom.xml clean verify
                '''
            }
        }

        stage('Deploy with Docker') {
            steps {
                sh '''
                    docker compose \
                      --env-file /opt/document-vault/.env \
                      -f docker-compose.yml \
                      up -d --build
                '''
            }
        }
    }

    post {
        always {
            junit allowEmptyResults: true,
                  testResults: '**/target/surefire-reports/TEST-*.xml'
        }
    }
}
