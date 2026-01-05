pipeline {
    agent any



    tools {
        maven 'Maven_3.9'
        jdk 'jdk17'
    }
    environment {
            DOCKERHUB_USERNAME = 'bensaltanahoussam'
            IMAGE_NAME = 'vertdrop-v2'
    }
    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build') {
            steps {
                sh 'mvn clean package -DskipTests'
            }
        }

        stage('Test') {
            steps {
                sh 'mvn test'
            }
        }

        stage('Build Docker Image') {
            steps {
                sh 'docker build -t vertdrop-app:${BUILD_NUMBER} .'
                sh 'docker build -t vertdrop-app:latest .'
            }
        }

        stage('Push Docker Image') {
            steps {
                withCredentials([usernamePassword(credentialsId: 'dockerhub-credentials', usernameVariable: 'DOCKERHUB_USER', passwordVariable: 'DOCKERHUB_PASS')]) {
                    sh '''
                        echo $DOCKERHUB_PASS | docker login -u $DOCKERHUB_USER --password-stdin
                        docker tag vertdrop-app:${BUILD_NUMBER} $DOCKERHUB_USERNAME/$IMAGE_NAME:${BUILD_NUMBER}
                        docker tag vertdrop-app:latest $DOCKERHUB_USERNAME/$IMAGE_NAME:latest
                        docker push $DOCKERHUB_USERNAME/$IMAGE_NAME:${BUILD_NUMBER}
                        docker push $DOCKERHUB_USERNAME/$IMAGE_NAME:latest
                    '''
                }
            }
        }
    }

    post {
        success {
            echo '✅ Build successful!'
        }
        failure {
            echo '❌ Build failed!'
            mail to: 'bensaltanahoussam7@gmail.com',
                 subject: "Jenkins Build Failed: ${env.JOB_NAME} #${env.BUILD_NUMBER}",
                 body: "Check Jenkins for details: ${env.BUILD_URL}"
        }
    }
}