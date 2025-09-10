# 📁 ComplyVault Microservices Project Structure

## 🗂️ **Complete Project Organization**

```
Team1_Big_Project_final/
├── 📁 k8s/                          # Kubernetes deployment files
│   ├── audit-deployment.yaml
│   ├── audit-service.yaml
│   ├── ingestion-deployment.yaml
│   ├── ingestion-service.yaml
│   ├── normalizer-deployment.yaml
│   ├── normalizer-service.yaml
│   ├── compliance-deployment.yaml
│   ├── compliance-service.yaml
│   ├── retention-deployment.yaml
│   ├── retention-service.yaml
│   ├── review-deployment.yaml
│   ├── review-service.yaml
│   ├── search-deployment.yaml
│   ├── search-service.yaml
│   ├── mongodb-deployment.yaml
│   ├── mongodb-service.yaml
│   ├── postgres-deployment.yaml
│   ├── postgres-service.yaml
│   ├── kafka-deployment.yaml
│   ├── kafka-service.yaml
│   ├── zookeeper-deployment.yaml
│   └── zookeeper-service.yaml
│
├── 📁 Audit/                        # Audit Microservice
│   ├── Dockerfile
│   ├── pom.xml
│   ├── mvnw
│   ├── mvnw.cmd
│   └── src/
│       ├── main/java/com/complyvault/audit/
│       │   ├── AuditServiceApplication.java
│       │   ├── config/MongoConfig.java
│       │   ├── controller/AuditController.java
│       │   ├── model/AuditEvent.java
│       │   ├── repository/AuditEventRepository.java
│       │   └── service/AuditService.java
│       ├── main/resources/application.properties
│       └── test/
│
├── 📁 IngestionAndValidation/       # Ingestion & Validation Microservice
│   ├── Dockerfile
│   ├── pom.xml
│   ├── mvnw
│   ├── mvnw.cmd
│   └── src/
│       ├── main/java/com/Project1/IngestionAndValidation/
│       │   ├── IngestionAndValidationApplication.java
│       │   ├── config/RestTemplateConfig.java
│       │   ├── config/MongoConfig.java
│       │   ├── Ingestioncontrollers/MessageController.java
│       │   ├── Models/
│       │   │   ├── UniqueId.java
│       │   │   └── ProcessedMessage.java
│       │   ├── repository/UniqueIdRepository.java
│       │   ├── services/
│       │   │   ├── MessageValidationService.java
│       │   │   ├── S3StorageService.java
│       │   │   ├── DuplicateCheckService.java
│       │   │   └── MessageProducerService.java
│       │   ├── utils/MessageIdGenerator.java
│       │   ├── Validation/
│       │   └── exception/
│       ├── main/resources/
│       │   ├── application.properties
│       │   └── schemas/
│       └── test/
│
├── 📁 normalizer/                   # Normalizer Microservice
│   ├── Dockerfile
│   ├── pom.xml
│   ├── mvnw
│   ├── mvnw.cmd
│   └── src/
│       ├── main/java/com/project_1/normalizer/
│       │   ├── NormalizerApplication.java
│       │   ├── config/RestTemplateConfig.java
│       │   ├── config/MongoConfig.java
│       │   ├── kafka/NormalizerConsumer.java
│       │   ├── model/CanonicalMessage.java
│       │   ├── repository/UniqueIdRepository.java
│       │   ├── service/
│       │   │   ├── MessageService.java
│       │   │   └── MongoStorageService.java
│       │   └── util/adapters/
│       │       ├── MessageAdapter.java
│       │       ├── EmailAdapter.java
│       │       └── SlackAdapter.java
│       ├── main/resources/application.properties
│       └── test/
│
├── 📁 Compliance/                   # Compliance Microservice
│   ├── Dockerfile
│   ├── pom.xml
│   ├── mvnw
│   ├── mvnw.cmd
│   └── src/
│       ├── main/java/com/smarsh/compliance/
│       │   ├── CompliancePolicyEngineApplication.java
│       │   ├── config/RestTemplateConfig.java
│       │   ├── controller/
│       │   ├── entity/
│       │   ├── evaluators/
│       │   │   ├── KeywordEvaluator.java
│       │   │   └── RegexEvaluator.java
│       │   ├── kafka/MessageConsumer.java
│       │   ├── models/
│       │   ├── repository/
│       │   └── service/
│       │       └── ComplianceService.java
│       ├── main/resources/application.properties
│       └── test/
│
├── 📁 Retention/                    # Retention Microservice
│   ├── Dockerfile
│   ├── pom.xml
│   ├── mvnw
│   ├── mvnw.cmd
│   └── src/
│       ├── main/java/com/complyvault/retention/
│       ├── main/resources/application.properties
│       └── test/
│
├── 📁 review/                       # Review Microservice
│   ├── Dockerfile
│   ├── pom.xml
│   ├── mvnw
│   ├── mvnw.cmd
│   └── src/
│       ├── main/java/com/Project_1/Review/
│       ├── main/resources/
│       │   ├── application.properties
│       │   └── data.sql
│       └── test/
│
├── 📁 Search/                       # Search Microservice
│   ├── Dockerfile
│   ├── pom.xml
│   ├── mvnw
│   ├── mvnw.cmd
│   └── src/
│       ├── main/java/com/Project1/Search/
│       ├── main/resources/application.properties
│       └── test/
│
├── 📁 shared-models/                # Shared Models Library
│   ├── pom.xml
│   └── src/main/java/com/complyvault/shared/
│       ├── client/AuditClient.java
│       └── dto/AuditEventDTO.java
│
├── 📄 .gitignore                    # Git ignore rules
├── 📄 deploy-to-minikube.bat        # Windows deployment script
├── 📄 COMPLETE_DEPLOYMENT_GUIDE.md  # Complete deployment guide
├── 📄 PROJECT_STRUCTURE.md          # This file
├── 📄 MINIKUBE_DEPLOYMENT.md        # Minikube deployment guide
├── 📄 README.md                     # Project README
├── 📄 docker-compose.yml            # Docker Compose (alternative)
├── 📄 start-microservices.bat       # Local development script
├── 📄 test-microservices.bat        # Testing script
└── 📄 full project documentaion.md  # Project documentation
```

## 🎯 **Why Infrastructure YAML Files Are Needed**

### **Dependency Chain:**
```
Your Microservices → Need → Infrastructure Services
     ↓                    ↓
┌─────────────┐    ┌─────────────┐
│ Audit       │───▶│ MongoDB     │
│ Ingestion   │───▶│ MongoDB     │
│ Normalizer  │───▶│ MongoDB     │
│ Compliance  │───▶│ PostgreSQL  │
│ Retention   │───▶│ PostgreSQL  │
│ Review      │───▶│ H2 (built-in)│
│ Search      │───▶│ MongoDB     │
└─────────────┘    └─────────────┘
     ↓                    ↓
┌─────────────┐    ┌─────────────┐
│ All Services│───▶│ Kafka       │
│ (except UI) │───▶│ Zookeeper   │
└─────────────┘    └─────────────┘
```

### **Without Infrastructure Services:**
- ❌ **MongoDB missing** → Audit, Ingestion, Normalizer, Search will crash
- ❌ **PostgreSQL missing** → Compliance, Retention will crash  
- ❌ **Kafka missing** → Message streaming between services fails
- ❌ **Zookeeper missing** → Kafka won't start

### **With Infrastructure Services:**
- ✅ **All databases available** → Services can store data
- ✅ **Message streaming works** → Services can communicate
- ✅ **Complete platform** → Full microservices architecture

## 🚀 **How to Deploy (CMD/Command Prompt)**

### **Step 1: Open Command Prompt**
```cmd
# Press Windows + R, type "cmd", press Enter
# OR
# Press Windows + X, select "Command Prompt"
```

### **Step 2: Navigate to Project**
```cmd
cd C:\Users\poojitha.gandla\Team1_Big_Project_final
```

### **Step 3: Run Deployment**
```cmd
deploy-to-minikube.bat
```

### **Step 4: Monitor Deployment**
```cmd
# In another CMD window:
kubectl get pods
kubectl get svc
```

## 📊 **Service Ports & Access**

| Service | Port | Access URL |
|---------|------|------------|
| Audit | 8093 | `minikube service audit-service --url` |
| Ingestion | 8080 | `minikube service ingestion-service --url` |
| Normalizer | 8081 | `minikube service normalizer-service --url` |
| Compliance | 8082 | `minikube service compliance-service --url` |
| Retention | 8083 | `minikube service retention-service --url` |
| Review | 8084 | `minikube service review-service --url` |
| Search | 8085 | `minikube service search-service --url` |

## ✅ **Ready to Deploy!**

Everything is organized and ready. Just run `deploy-to-minikube.bat` in Command Prompt and your complete microservices platform will be deployed to Minikube! 🚀
