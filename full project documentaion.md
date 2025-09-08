# ComplyVault Microservices Architecture

This document explains the new microservices architecture with separated Retention and Audit services.

## 🏗️ Architecture Overview

The application has been refactored into the following microservices:

### Core Services
1. **Ingestion Service** (Port 8080) - Handles message ingestion and validation
2. **Normalizer Service** (Port 8081) - Normalizes messages into canonical format
3. **Compliance Service** (Port 8082) - Applies compliance policies
4. **Search Service** (Port 8083) - Provides search functionality
5. **Review Service** (Port 8084) - Manages flagged message reviews

### New Separated Services
6. **Retention Service** (Port 8085) - Manages retention policies and cleanup
7. **Audit Service** (Port 8086) - Centralized audit logging

## 🚀 Quick Start

### Option 1: Docker Compose (Recommended)
```bash
# Start all services with infrastructure
docker-compose up -d

# Check service status
docker-compose ps

# View logs
docker-compose logs -f [service-name]
```

### Option 2: Manual Startup
1. Start infrastructure services:
   ```bash
   # Start Kafka, MongoDB, PostgreSQL, Elasticsearch
   docker-compose up -d zookeeper kafka mongodb postgresql elasticsearch kibana
   ```

2. Start microservices individually:
   ```bash
   # Terminal 1 - Ingestion Service
   cd IngestionAndValidation && ./mvnw spring-boot:run

   # Terminal 2 - Normalizer Service  
   cd normalizer && ./mvnw spring-boot:run

   # Terminal 3 - Compliance Service
   cd Compliance && ./mvnw spring-boot:run

   # Terminal 4 - Search Service
   cd Search && ./mvnw spring-boot:run

   # Terminal 5 - Review Service
   cd review && ./mvnw spring-boot:run

   # Terminal 6 - Retention Service
   cd Retention && ./mvnw spring-boot:run

   # Terminal 7 - Audit Service
   cd Audit && ./mvnw spring-boot:run
   ```

## 🔧 Service Configuration

### Retention Service
- **Port**: 8085
- **Database**: MongoDB (complyVault database)
- **Kafka Topics**: Consumes from `messages` topic
- **Features**:
  - Retention policy management
  - Automated message cleanup
  - Scheduled processing (daily at 1 AM)
  - File system cleanup

### Audit Service
- **Port**: 8086
- **Database**: MongoDB (complyVault database)
- **Kafka Topics**: Consumes from `audit-events` topic
- **Features**:
  - Centralized audit logging
  - Event search and filtering
  - Duplicate detection
  - Multi-service audit aggregation

## 📡 Inter-Service Communication

### Kafka Topics
- `messages` - Canonical messages between services
- `audit-events` - Audit events from all services
- `compliance-events` - Compliance check results
- `search-events` - Search indexing events

### API Endpoints

#### Retention Service
```bash
# Create/Update retention policy
POST http://localhost:8085/api/retention-policies
{
  "tenantId": "bank-001",
  "channel": "email",
  "retentionPeriodDays": 90
}

# Get retention policy
GET http://localhost:8085/api/retention-policies/{tenantId}/{channel}

# Trigger manual processing
POST http://localhost:8085/api/retention-policies/process-expired
```

#### Audit Service
```bash
# Log audit event
POST http://localhost:8086/api/audit/log
{
  "tenantId": "bank-001",
  "messageId": "msg-123",
  "network": "email",
  "eventType": "INGESTED",
  "serviceName": "ingestion-service",
  "details": {"status": "success"}
}

# Search audit events
GET http://localhost:8086/api/audit/search?tenantId=bank-001&eventType=INGESTED&startTime=2024-01-01T00:00:00Z&endTime=2024-12-31T23:59:59Z

# Check for duplicates
GET http://localhost:8086/api/audit/duplicate/{messageId}
```

## 🔄 Migration from Monolithic Services

### Changes Made
1. **Retention Service**: Extracted from normalizer service
2. **Audit Service**: Centralized audit logging from all services
3. **Normalizer Service**: Removed retention and audit dependencies
4. **Shared Models**: Created common DTOs for inter-service communication

### Breaking Changes
- Retention policies are now managed by the Retention service
- Audit events are centralized in the Audit service
- Services now communicate via Kafka for audit events

## 🧪 Testing

### Test Retention Service
```bash
# Create a retention policy
curl -X POST http://localhost:8085/api/retention-policies \
  -H "Content-Type: application/json" \
  -d '{"tenantId":"test-tenant","channel":"email","retentionPeriodDays":30}'

# Trigger processing
curl -X POST http://localhost:8085/api/retention-policies/process-expired
```

### Test Audit Service
```bash
# Log an audit event
curl -X POST http://localhost:8086/api/audit/log \
  -H "Content-Type: application/json" \
  -d '{"tenantId":"test-tenant","messageId":"test-msg","network":"email","eventType":"TEST","serviceName":"test-service"}'

# Search events
curl "http://localhost:8086/api/audit/tenant/test-tenant"
```

## 📊 Monitoring

### Health Checks
- Retention Service: `http://localhost:8085/actuator/health`
- Audit Service: `http://localhost:8086/actuator/health`

### Logs
- Each service logs to its own console
- Docker logs: `docker-compose logs -f [service-name]`

## 🚨 Troubleshooting

### Common Issues
1. **Service won't start**: Check if required infrastructure services are running
2. **Database connection errors**: Verify MongoDB/PostgreSQL are accessible
3. **Kafka connection issues**: Ensure Kafka is running and accessible
4. **Port conflicts**: Check if ports 8080-8086 are available

### Debug Commands
```bash
# Check service status
docker-compose ps

# View service logs
docker-compose logs [service-name]

# Check database connections
docker-compose exec mongodb mongosh
docker-compose exec postgresql psql -U postgres

# Check Kafka topics
docker-compose exec kafka kafka-topics --bootstrap-server localhost:9092 --list
```

## 🔧 Development

### Adding New Services
1. Create service directory with Maven structure
2. Add to `docker-compose.yml`
3. Update shared models if needed
4. Configure Kafka topics
5. Add health checks and monitoring

### Modifying Existing Services
1. Update service code
2. Rebuild Docker image: `docker-compose build [service-name]`
3. Restart service: `docker-compose restart [service-name]`
