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