# 🚀 Complete ComplyVault Microservices Deployment Guide

## 📋 **ALL Services Ready for Deployment**

| Service | Port | Status | Purpose | Database |
|---------|------|--------|---------|----------|
| **Audit** | 8093 | ✅ Ready | Centralized audit logging | MongoDB |
| **IngestionAndValidation** | 8080 | ✅ Ready | Message ingestion & validation | MongoDB + S3 |
| **Normalizer** | 8081 | ✅ Ready | Message normalization | MongoDB |
| **Compliance** | 8082 | ✅ Ready | Policy evaluation | PostgreSQL |
| **Retention** | 8083 | ✅ Ready | Retention policies | PostgreSQL |
| **Review** | 8084 | ✅ Ready | Review workflow | H2 (in-memory) |
| **Search** | 8085 | ✅ Ready | Search functionality | MongoDB |

## 🎯 **Step-by-Step Deployment Process**

### **Step 1: Prerequisites Setup**

```bash
# 1. Install Minikube
# Windows: choco install minikube
# Mac: brew install minikube
# Linux: Download from https://minikube.sigs.k8s.io/docs/start/

# 2. Install kubectl
# Windows: choco install kubernetes-cli
# Mac: brew install kubectl
# Linux: Download from https://kubernetes.io/docs/tasks/tools/

# 3. Install Docker Desktop
# Download from https://www.docker.com/products/docker-desktop

# 4. Verify installations
minikube version
kubectl version --client
docker --version
```

### **Step 2: Start Minikube**

```bash
# Start Minikube with Docker driver
minikube start --driver=docker

# Verify Minikube is running
kubectl get nodes
# Should show 1 node in Ready state
```

### **Step 3: Build All Docker Images**

```bash
# Enable Minikube Docker daemon
minikube -p minikube docker-env | Invoke-Expression  # PowerShell
# OR
eval $(minikube -p minikube docker-env)  # Linux/Mac

# Build all service images
docker build -t audit-service:1.0 ./Audit
docker build -t ingestion-service:1.0 ./IngestionAndValidation
docker build -t normalizer-service:1.0 ./normalizer
docker build -t compliance-service:1.0 ./Compliance
docker build -t retention-service:1.0 ./Retention
docker build -t review-service:1.0 ./review
docker build -t search-service:1.0 ./Search

# Verify all images are built
docker images | grep -E "(audit|ingestion|normalizer|compliance|retention|review|search)-service"
```

### **Step 4: Deploy Infrastructure Services**

```bash
# Deploy in dependency order
kubectl apply -f k8s/zookeeper-deployment.yaml
kubectl apply -f k8s/zookeeper-service.yaml

kubectl apply -f k8s/kafka-deployment.yaml
kubectl apply -f k8s/kafka-service.yaml

kubectl apply -f k8s/mongodb-deployment.yaml
kubectl apply -f k8s/mongodb-service.yaml

kubectl apply -f k8s/postgres-deployment.yaml
kubectl apply -f k8s/postgres-service.yaml

# Wait for infrastructure to be ready
kubectl wait --for=condition=available --timeout=300s deployment/zookeeper-deployment
kubectl wait --for=condition=available --timeout=300s deployment/kafka-deployment
kubectl wait --for=condition=available --timeout=300s deployment/mongodb-deployment
kubectl wait --for=condition=available --timeout=300s deployment/postgres-deployment
```

### **Step 5: Deploy All Microservices**

```bash
# Deploy all microservices
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

# Wait for all services to be ready
kubectl wait --for=condition=available --timeout=300s deployment/audit-deployment
kubectl wait --for=condition=available --timeout=300s deployment/ingestion-deployment
kubectl wait --for=condition=available --timeout=300s deployment/normalizer-deployment
kubectl wait --for=condition=available --timeout=300s deployment/compliance-deployment
kubectl wait --for=condition=available --timeout=300s deployment/retention-deployment
kubectl wait --for=condition=available --timeout=300s deployment/review-deployment
kubectl wait --for=condition=available --timeout=300s deployment/search-deployment
```

### **Step 6: Verify Deployment**

```bash
# Check all pods are running
kubectl get pods
# All pods should show STATUS: Running

# Check all services
kubectl get svc
# All services should show TYPE: NodePort

# Check logs if any issues
kubectl logs -f deployment/audit-deployment
kubectl logs -f deployment/ingestion-deployment
```

### **Step 7: Access Services**

```bash
# Get service URLs
minikube service audit-service --url
minikube service ingestion-service --url
minikube service normalizer-service --url
minikube service compliance-service --url
minikube service retention-service --url
minikube service review-service --url
minikube service search-service --url
```

## 🔧 **Quick Deployment (One Command)**

```bash
# Run the complete deployment script
deploy-to-minikube.bat
```

## 📊 **Service Architecture in Minikube**

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   Ingestion     │───▶│   Normalizer    │───▶│   Compliance    │
│   :8080         │    │   :8081         │    │   :8082         │
└─────────────────┘    └─────────────────┘    └─────────────────┘
         │                       │                       │
         ▼                       ▼                       ▼
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   Audit         │    │   MongoDB       │    │   PostgreSQL    │
│   :8093         │    │   :27017        │    │   :5432         │
└─────────────────┘    └─────────────────┘    └─────────────────┘
         │                       │                       │
         ▼                       ▼                       ▼
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   Retention     │    │   Review        │    │   Search        │
│   :8083         │    │   :8084         │    │   :8085         │
└─────────────────┘    └─────────────────┘    └─────────────────┘
         │                       │                       │
         ▼                       ▼                       ▼
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   Kafka         │    │   H2 Database   │    │   MongoDB       │
│   :9092         │    │   (in-memory)   │    │   :27017        │
└─────────────────┘    └─────────────────┘    └─────────────────┘
```

## 🎯 **What to Do After Deployment**

### **1. Test Each Service**

```bash
# Test Audit Service
curl http://$(minikube service audit-service --url | cut -d'/' -f3)/api/audit/log

# Test Ingestion Service
curl -X POST http://$(minikube service ingestion-service --url | cut -d'/' -f3)/api/messages \
  -H "Content-Type: application/json" \
  -d '{"tenantId":"test","network":"slack","user":"testuser","text":"Hello World"}'

# Test other services similarly...
```

### **2. Monitor Services**

```bash
# Watch all pods
kubectl get pods -w

# Check service logs
kubectl logs -f deployment/audit-deployment
kubectl logs -f deployment/ingestion-deployment

# Check resource usage
kubectl top pods
kubectl top nodes
```

### **3. Scale Services (Optional)**

```bash
# Scale a service to 3 replicas
kubectl scale deployment audit-deployment --replicas=3

# Check scaling
kubectl get pods -l app=audit
```

### **4. Access Service Dashboards**

```bash
# Access H2 Console (Review Service)
minikube service review-service --url
# Then go to /h2-console

# Access other service endpoints
minikube service audit-service --url
minikube service ingestion-service --url
```

## 🚨 **Troubleshooting**

### **Common Issues:**

1. **Pods not starting:**
   ```bash
   kubectl describe pod <pod-name>
   kubectl logs <pod-name>
   ```

2. **Services not accessible:**
   ```bash
   kubectl get svc
   kubectl describe svc <service-name>
   ```

3. **Database connection issues:**
   ```bash
   kubectl logs deployment/mongodb-deployment
   kubectl logs deployment/postgres-deployment
   ```

### **Reset Everything:**

```bash
# Delete all deployments
kubectl delete deployment --all
kubectl delete service --all

# Restart Minikube
minikube stop
minikube start --driver=docker
```

## ✅ **Success Indicators**

- All pods show `STATUS: Running`
- All services show `TYPE: NodePort`
- Service URLs are accessible
- No error logs in pod logs
- Services can communicate with each other

**Your complete microservices architecture is now running in Minikube! 🎉**
