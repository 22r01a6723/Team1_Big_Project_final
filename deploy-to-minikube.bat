@echo off
echo 🚀 Deploying ComplyVault Microservices to Minikube

REM Step 1: Start Minikube
echo 📦 Starting Minikube...
minikube start --driver=docker

REM Step 2: Enable Minikube Docker daemon
echo 🐳 Configuring Docker environment...
minikube -p minikube docker-env | Invoke-Expression

REM Step 3: Build Docker images
echo 🔨 Building Docker images...
docker build -t audit-service:1.0 ./Audit
docker build -t ingestion-service:1.0 ./IngestionAndValidation
docker build -t normalizer-service:1.0 ./normalizer
docker build -t compliance-service:1.0 ./Compliance
docker build -t retention-service:1.0 ./Retention
docker build -t review-service:1.0 ./review
docker build -t search-service:1.0 ./Search

echo ✅ Docker images built successfully

REM Step 4: Deploy infrastructure services first
echo 🏗️ Deploying infrastructure services...
kubectl apply -f k8s/zookeeper-deployment.yaml
kubectl apply -f k8s/zookeeper-service.yaml
kubectl apply -f k8s/kafka-deployment.yaml
kubectl apply -f k8s/kafka-service.yaml
kubectl apply -f k8s/mongodb-deployment.yaml
kubectl apply -f k8s/mongodb-service.yaml
kubectl apply -f k8s/postgres-deployment.yaml
kubectl apply -f k8s/postgres-service.yaml

REM Wait for infrastructure to be ready
echo ⏳ Waiting for infrastructure services to be ready...
kubectl wait --for=condition=available --timeout=300s deployment/zookeeper-deployment
kubectl wait --for=condition=available --timeout=300s deployment/kafka-deployment
kubectl wait --for=condition=available --timeout=300s deployment/mongodb-deployment
kubectl wait --for=condition=available --timeout=300s deployment/postgres-deployment

REM Step 5: Deploy microservices
echo 🚀 Deploying microservices...
kubectl apply -f k8s/audit-deployment.yaml
kubectl apply -f k8s/audit-service.yaml
kubectl apply -f k8s/ingestion-deployment.yaml
kubectl apply -f k8s/ingestion-service.yaml
kubectl apply -f k8s/normalizer-deployment.yaml
kubectl apply -f k8s/normalizer-service.yaml
kubectl apply -f k8s/compliance-deployment.yaml
kubectl apply -f k8s/compliance-service.yaml
kubectl apply -f k8s/retention-deployment.yaml
kubectl apply -f k8s/retention-service.yaml
kubectl apply -f k8s/review-deployment.yaml
kubectl apply -f k8s/review-service.yaml
kubectl apply -f k8s/search-deployment.yaml
kubectl apply -f k8s/search-service.yaml

REM Wait for microservices to be ready
echo ⏳ Waiting for microservices to be ready...
kubectl wait --for=condition=available --timeout=300s deployment/audit-deployment
kubectl wait --for=condition=available --timeout=300s deployment/ingestion-deployment
kubectl wait --for=condition=available --timeout=300s deployment/normalizer-deployment
kubectl wait --for=condition=available --timeout=300s deployment/compliance-deployment
kubectl wait --for=condition=available --timeout=300s deployment/retention-deployment
kubectl wait --for=condition=available --timeout=300s deployment/review-deployment
kubectl wait --for=condition=available --timeout=300s deployment/search-deployment

REM Step 6: Show service URLs
echo 🌐 Service URLs:
echo Audit Service:
minikube service audit-service --url
echo Ingestion Service:
minikube service ingestion-service --url
echo Normalizer Service:
minikube service normalizer-service --url
echo Compliance Service:
minikube service compliance-service --url
echo Retention Service:
minikube service retention-service --url
echo Review Service:
minikube service review-service --url
echo Search Service:
minikube service search-service --url

echo ✅ Deployment completed successfully!
echo 📊 Check status with: kubectl get pods
echo 🔍 Check services with: kubectl get svc
pause
