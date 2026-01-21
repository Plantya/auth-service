pipeline {
  agent any

  options {
    timestamps()
    ansiColor('xterm')
    disableConcurrentBuilds()
  }

  environment {
    DOCKERFILE = 'Dockerfile'
    CONFIGMAP_YAML = 'k8s/dev/config-map.yaml'
    DEPLOYMENT_YAML = 'k8s/dev/deployment.yaml'

    DO_REGISTRY = 'registry.digitalocean.com/plantya-registry'
    IMAGE_NAME = 'auth-service'

    K8S_NAMESPACE = 'plantya-dev'
    K8S_DEPLOYMENT_NAME = 'auth-service'
    K8S_CONTAINER_NAME = 'auth-service'
  }

  tools {
    jdk 'Java25'
    maven 'Maven3'
  }

  stages {

    stage('Checkout') {
      steps {
        checkout scm
        sh 'git rev-parse HEAD'
      }
    }

    stage('Compute Image Tag') {
      steps {
        script {
          def shortSha = sh(script: "git rev-parse --short=8 HEAD", returnStdout: true).trim()
          env.IMAGE_TAG = "git-${shortSha}"
          env.FULL_IMAGE = "${env.DO_REGISTRY}/${env.IMAGE_NAME}:${env.IMAGE_TAG}"
          env.FULL_IMAGE_DEV_LATEST = "${env.DO_REGISTRY}/${env.IMAGE_NAME}:dev-latest"
        }

        sh '''
          echo "IMAGE_TAG=${IMAGE_TAG}"
          echo "FULL_IMAGE=${FULL_IMAGE}"
        '''
      }
    }

    stage('Test') {
      steps {
        sh 'mvn -B -DskipTests=false test'
      }
      post {
        always {
          junit allowEmptyResults: true, testResults: "/target/surefire-reports/*.xml"
        }
      }
    }

    stage('Build') {
      steps {
        sh 'mvn -B -DskipTests=true clean package'
      }
    }

    stage('Debug Workspace') {
      steps {
        sh '''
          pwd
          ls -la
          ls -la auth-service || true
        '''
      }
    }
    
    stage('Docker Build') {
      steps {
        sh '''
          docker build -f "${DOCKERFILE}" -t "${FULL_IMAGE}" "."
          docker tag "${FULL_IMAGE}" "${FULL_IMAGE_DEV_LATEST}"
        '''
      }
    }

    stage('Login DO Registry') {
      environment {
        DO_TOKEN = credentials('DO_TOKEN')
      }
      steps {
        sh '''
          doctl auth init --access-token "${DO_TOKEN}"
          doctl registry login --expiry-seconds 600
        '''
      }
    }

    stage('Push Image') {
      steps {
        sh '''
          docker push "${FULL_IMAGE}"
          docker push "${FULL_IMAGE_DEV_LATEST}"
        '''
      }
    }

    stage('Deploy ConfigMap + Deployment') {
      steps {
        withCredentials([file(credentialsId: 'KUBECONFIG_FILE', variable: 'KUBECONFIG')]) {
          sh '''
            export KUBECONFIG="${KUBECONFIG}"

            kubectl get ns "${K8S_NAMESPACE}" >/dev/null 2>&1 || \
              kubectl create ns "${K8S_NAMESPACE}"

            echo "Apply ConfigMap..."
            kubectl -n "${K8S_NAMESPACE}" apply -f "${CONFIGMAP_YAML}"

            echo "Apply base Deployment..."
            kubectl -n "${K8S_NAMESPACE}" apply -f "${DEPLOYMENT_YAML}"

            echo "Update image..."
            kubectl -n "${K8S_NAMESPACE}" set image deployment/"${K8S_DEPLOYMENT_NAME}" \
              "${K8S_CONTAINER_NAME}"="${FULL_IMAGE}" --record
          '''
        }
      }
    }

    stage('Verify Rollout') {
      steps {
        withCredentials([file(credentialsId: 'KUBECONFIG_FILE', variable: 'KUBECONFIG')]) {
          sh '''
            export KUBECONFIG="${KUBECONFIG}"

            kubectl -n "${K8S_NAMESPACE}" rollout status deployment/"${K8S_DEPLOYMENT_NAME}" --timeout=180s
            kubectl -n "${K8S_NAMESPACE}" get pods -o wide
          '''
        }
      }
    }
  }

  post {
    always {
      sh '''
        docker rmi "${FULL_IMAGE}" "${FULL_IMAGE_DEV_LATEST}" 2>/dev/null || true
      '''
    }
  }
}
