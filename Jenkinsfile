pipeline {
    agent any



    tools {
        maven 'Maven_3.9'
        jdk 'jdk17'
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

        stage('Build & Push Docker Image') {
           steps {
               script {
                   withCredentials([usernamePassword(credentialsId: 'dockerhub-id', usernameVariable: 'DOCKER_USER', passwordVariable: 'DOCKER_PASS')]) {
                       sh 'echo $DOCKER_PASS | docker login -u $DOCKER_USER --password-stdin'
                       sh "docker build -t ${DOCKER_USER}/vertdrop-app:${BUILD_NUMBER} ."
                       sh "docker build -t ${DOCKER_USER}/vertdrop-app:latest ."
                       sh "docker push ${DOCKER_USER}/vertdrop-app:${BUILD_NUMBER}"
                       sh "docker push ${DOCKER_USER}/vertdrop-app:latest"
                   }
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