pipeline {
    agent any

    environment {
        DOCKERHUB_REPO_PREFIX = 'poonamcloud'
        BACKEND_IMAGE = "${DOCKERHUB_REPO_PREFIX}/three-tier-backend"
        FRONTEND_IMAGE = "${DOCKERHUB_REPO_PREFIX}/three-tier-frontend"
        K8S_MASTER_IP = '13.233.125.143'
        K8S_MASTER_USER = 'ubuntu'
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build & Push Backend Image') {
            steps {
                dir('app/backend') {
                    script {
                        docker.withRegistry('https://index.docker.io/v1/', 'dockerhub-credentials') {
                            def backendImg = docker.build("${BACKEND_IMAGE}:${BUILD_NUMBER}")
                            backendImg.push()
                            backendImg.push('latest')
                        }
                    }
                }
            }
        }

        stage('Build & Push Frontend Image') {
            steps {
                dir('app/frontend') {
                    script {
                        docker.withRegistry('https://index.docker.io/v1/', 'dockerhub-credentials') {
                            def frontendImg = docker.build("${FRONTEND_IMAGE}:${BUILD_NUMBER}")
                            frontendImg.push()
                            frontendImg.push('latest')
                        }
                    }
                }
            }
        }

        stage('Deploy to Kubernetes') {
            steps {
                sshagent(credentials: ['k8s-master-ssh-key']) {
                    sh """
                        ssh -o StrictHostKeyChecking=no ${K8S_MASTER_USER}@${K8S_MASTER_IP} 'mkdir -p ~/three-tier-k8s'
                        scp -o StrictHostKeyChecking=no -r k8s/* ${K8S_MASTER_USER}@${K8S_MASTER_IP}:~/three-tier-k8s/
                        ssh -o StrictHostKeyChecking=no ${K8S_MASTER_USER}@${K8S_MASTER_IP} '
                            kubectl apply -f ~/three-tier-k8s/01-mysql.yaml
                            kubectl apply -f ~/three-tier-k8s/02-backend.yaml
                            kubectl apply -f ~/three-tier-k8s/03-frontend.yaml
                            kubectl apply -f ~/three-tier-k8s/04-hpa.yaml
                            kubectl apply -f ~/three-tier-k8s/05-ingress-443.yaml
                            kubectl rollout restart deployment/backend deployment/frontend
                            kubectl get pods -o wide
                        '
                    """
                }
            }
        }
    }

    post {
        success {
            echo "Deployment to Kubernetes (${K8S_MASTER_IP}) succeeded for build #${BUILD_NUMBER}."
        }
        failure {
            echo "Pipeline failed for build #${BUILD_NUMBER}. Check console log for details."
        }
    }
}