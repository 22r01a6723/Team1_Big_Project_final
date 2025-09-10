# ✅ ComplyVault Deployment Checklist

## 🎯 **Quick Deployment Steps**

### **Step 1: Open Command Prompt (CMD)**
- Press `Windows + R`
- Type `cmd`
- Press `Enter`

### **Step 2: Navigate to Project**
```cmd
cd C:\Users\poojitha.gandla\Team1_Big_Project_final
```

### **Step 3: Run Deployment Script**
```cmd
deploy-to-minikube.bat
```

### **Step 4: Wait for Completion**
- Script will build all Docker images
- Deploy infrastructure services (MongoDB, PostgreSQL, Kafka, Zookeeper)
- Deploy all 7 microservices
- Show service URLs

### **Step 5: Verify Deployment**
```cmd
# Check all pods are running
kubectl get pods

# Check all services
kubectl get svc

# Get service URLs
minikube service audit-service --url
minikube service ingestion-service --url
```

## 🏗️ **Why We Need Infrastructure Services**

| Infrastructure | Used By | Purpose |
|----------------|---------|---------|
| **MongoDB** | Audit, Ingestion, Normalizer, Search | Data storage |
| **PostgreSQL** | Compliance, Retention | Relational data |
| **Kafka** | All services (except UI) | Message streaming |
| **Zookeeper** | Kafka | Coordination |

**Without these → Your microservices will crash on startup!**

## 📊 **What Gets Deployed**

### **Infrastructure Services:**
- ✅ MongoDB (port 27017)
- ✅ PostgreSQL (port 5432)  
- ✅ Kafka (port 9092)
- ✅ Zookeeper (port 2181)

### **Microservices:**
- ✅ Audit Service (port 8093)
- ✅ Ingestion Service (port 8080)
- ✅ Normalizer Service (port 8081)
- ✅ Compliance Service (port 8082)
- ✅ Retention Service (port 8083)
- ✅ Review Service (port 8084)
- ✅ Search Service (port 8085)

## 🎯 **Success Indicators**

- All pods show `STATUS: Running`
- All services show `TYPE: NodePort`
- Service URLs are accessible
- No error logs in pod logs

## 🚨 **If Something Goes Wrong**

```cmd
# Check pod logs
kubectl logs -f deployment/audit-deployment
kubectl logs -f deployment/ingestion-deployment

# Check pod status
kubectl describe pod <pod-name>

# Restart everything
kubectl delete deployment --all
kubectl delete service --all
minikube stop
minikube start --driver=docker
deploy-to-minikube.bat
```

## 🎉 **You're Ready!**

Just run `deploy-to-minikube.bat` in Command Prompt and your complete ComplyVault platform will be running in Minikube! 🚀
