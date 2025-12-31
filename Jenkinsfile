pipeline {
    agent any

    environment {
        // Set any needed env vars (ex: JAVA_HOME, MAVEN_HOME, etc.)
    }

    tools {
        maven 'Maven_3.9'
        jdk 'jdk17'
    }

    stages {
        stage('Checkout') {
            steps {
                git credentialsId: 'github-token', url: 'https://github.com/BensaltanaHoussam/VertDrop_V2.git', branch: 'main'
            }
        }
        stage('Build') {
            steps {
                sh './mvnw clean package -DskipTests'
            }
        }
        stage('Test') {
            steps {
                sh './mvnw test'
            }
        }
        stage('Build Docker Image') {
            when {
                branch 'main'
            }
            steps {
                sh 'docker build -t vertdrop-app:ci .'
            }
        }
    }

    post {
        failure {
            echo 'Build failed!'
            mail to: 'bensaltanahoussam7@gmail.com',
                 subject: "Jenkins Build Failed: ${env.JOB_NAME} ${env.BUILD_NUMBER}",
                 body: "Check Jenkins for details: ${env.BUILD_URL}"
        }
    }
}