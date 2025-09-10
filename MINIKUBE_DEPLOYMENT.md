# 🚀 ComplyVault Microservices - Minikube Deployment Guide

## ✅ **Deploying Completed Services First (Recommended Approach)**

You can absolutely deploy your completed microservices first and add the UI later! This is a best practice in microservices development.

### **📋 Completed Services Ready for Deployment:**

| Service | Port | Status | Purpose |
|---------|------|--------|---------|
| **Audit** | 8093 | ✅ Ready | Centralized audit logging |
| **IngestionAndValidation** | 8080 | ✅ Ready | Message ingestion & validation |
| **Normalizer** | 8081 | ✅ Ready | Message normalization |
| **Compliance** | 8082 | ✅ Ready | Policy evaluation |
| **Retention** | 8083 | ✅ Ready | Retention policies |
| **Review** | 8084 | ✅ Ready | Review workflow |
| **Search** | 8085 | ✅ Ready | Search functionality |

### **🎯 Deployment Steps:**

#### **Step 1: Prerequisites**
```bash
# Install Minikube (if not already installed)
# Windows: choco install minikube
# Mac: brew install minikube
# Linux: curl -LO https://storage.googleapis.com/minikube/releases/latest/minikube-linux-amd64

# Install kubectl
# Windows: choco install kubernetes-cli
# Mac: brew install kubectl
# Linux: curl -LO "https://dl.k8s.io/release/$(curl -L -s https://dl.k8s.io/release/stable.txt)/bin/linux/amd64/kubectl"
```

#### **Step 2: Start Minikube**
```bash
minikube start --driver=docker
kubectl get nodes
```

#### **Step 3: Build Docker Images**
```bash
# Enable Minikube Docker daemon
eval $(minikube -p minikube docker-env)  # Linux/Mac
# OR
minikube -p minikube docker-env | Invoke-Expression  # PowerShell

# Build images
docker build -t audit-service:1.0 ./Audit
docker build -t ingestion-service:1.0 ./IngestionAndValidation
docker build -t normalizer-service:1.0 ./normalizer
docker build -t compliance-service:1.0 ./Compliance

# Verify images
docker images
```

#### **Step 4: Deploy Infrastructure Services**
```bash
# Deploy in order (dependencies first)
kubectl apply -f k8s/zookeeper-deployment.yaml
kubectl apply -f k8s/zookeeper-service.yaml
kubectl apply -f k8s/kafka-deployment.yaml
kubectl apply -f k8s/kafka-service.yaml
kubectl apply -f k8s/mongodb-deployment.yaml
kubectl apply -f k8s/mongodb-service.yaml

# Wait for infrastructure to be ready
kubectl wait --for=condition=available --timeout=300s deployment/zookeeper-deployment
kubectl wait --for=condition=available --timeout=300s deployment/kafka-deployment
kubectl wait --for=condition=available --timeout=300s deployment/mongodb-deployment
```

#### **Step 5: Deploy Microservices**
```bash
# Deploy microservices
kubectl apply -f k8s/audit-deployment.yaml
kubectl apply -f k8s/audit-service.yaml
kubectl apply -f k8s/ingestion-deployment.yaml
kubectl apply -f k8s/ingestion-service.yaml
kubectl apply -f k8s/normalizer-deployment.yaml
kubectl apply -f k8s/normalizer-service.yaml
kubectl apply -f k8s/compliance-deployment.yaml
kubectl apply -f k8s/compliance-service.yaml

# Wait for services to be ready
kubectl wait --for=condition=available --timeout=300s deployment/audit-deployment
kubectl wait --for=condition=available --timeout=300s deployment/ingestion-deployment
kubectl wait --for=condition=available --timeout=300s deployment/normalizer-deployment
kubectl wait --for=condition=available --timeout=300s deployment/compliance-deployment
```

#### **Step 6: Access Services**
```bash
# Get service URLs
minikube service audit-service --url
minikube service ingestion-service --url
minikube service normalizer-service --url
minikube service compliance-service --url
```

### **🔍 Monitoring & Troubleshooting:**

```bash
# Check pod status
kubectl get pods

# Check service status
kubectl get svc

# Check logs
kubectl logs -f deployment/audit-deployment
kubectl logs -f deployment/ingestion-deployment

# Describe pods for detailed info
kubectl describe pod <pod-name>
```

### **🎯 Adding UI Service Later (No Problems!):**

When you're ready to add the UI service:

1. **Create UI Dockerfile** in the UI service directory
2. **Add UI deployment YAMLs** to the k8s folder
3. **Build and deploy** the UI service
4. **Update Ingress** (if using) to include UI routes

**No conflicts or problems** - the existing services will continue working perfectly!

### **🌐 Service Communication:**

- **Internal Communication**: Services communicate via service names (e.g., `audit-service:8093`)
- **External Access**: Use `minikube service <service-name> --url` for external access
- **Database Connections**: MongoDB and PostgreSQL are accessible via service names

### **📊 Architecture in Minikube:**

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   Ingestion     │───▶│   Normalizer    │───▶│   Compliance    │
│   Service       │    │   Service       │    │   Service       │
│   :8080         │    │   :8081         │    │   :8082         │
└─────────────────┘    └─────────────────┘    └─────────────────┘
         │                       │                       │
         ▼                       ▼                       ▼
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   Audit         │    │   MongoDB       │    │   PostgreSQL    │
│   Service       │    │   :27017        │    │   :5432         │
│   :8093         │    └─────────────────┘    └─────────────────┘
└─────────────────┘
         │
         ▼
┌─────────────────┐
│   Kafka         │
│   :9092         │
└─────────────────┘
```

### **✅ Benefits of This Approach:**

1. **Incremental Deployment**: Deploy what's ready, add more later
2. **No Dependencies**: UI can be added without affecting existing services
3. **Easy Testing**: Test each service independently
4. **Scalable**: Add more services as they're completed
5. **Production-Ready**: Same patterns used in real production environments

**You're all set to deploy your completed microservices! 🚀**
