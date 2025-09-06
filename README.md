[README.md](https://github.com/user-attachments/files/21941589/README.md)
# ComplyVault – Multi-Tenant Archival & Compliance Platform (MVP)

## 📌 Project Overview
**ComplyVault** is a multi-tenant archival and compliance platform designed to securely ingest, process, and review enterprise communications.  

This MVP demonstrates ingestion of Email and Slack messages in JSON format, applies compliance policies, flags violations, and makes data searchable for audits.  

### ✅ Key Goals
- Ingest messages from multiple channels (Email, Slack)  
- Normalize into a canonical schema and deduplicate  
- Store immutably (raw + canonical form)  
- Enforce retention policies per channel  
- Apply compliance policies (regex-based rules)  
- Support flagging & review workflow  
- Provide searchable archives with Elasticsearch  
- Ensure multi-tenant isolation and audit logging

### ⚙️ Tech Stack
- Java 17 + Spring Boot  
- Apache Kafka → message ingestion  
- MongoDB → raw + canonical messages, audit logs  
- PostgreSQL → flagged messages, deduplication  
- Elasticsearch → search functionality  

---

## 🚀 Setup Instructions

### 1. Prerequisites
Install and configure:
- Java 17+  
- Apache Kafka  
- MongoDB  
- PostgreSQL  
- Elasticsearch  
- Maven  

### 2. Clone Repository
Clone the project from GitHub and navigate into the directory.  

### 3. Configure Databases
Update application configuration files with your PostgreSQL, MongoDB, Kafka, and Elasticsearch connection details.  

### 4. Run Application
Build and run the application with Maven.  

---

## 🏗️ Architecture Overview

**High-Level Flow:**  
1. Ingestion from REST APIs or Kafka topics  
2. Validation and normalization into a canonical schema  
3. Deduplication using PostgreSQL  
4. Immutable storage in MongoDB and disk  
5. Compliance checks with regex-based policy engine  
6. Flagging of violations stored in PostgreSQL  
7. Review workflow and audit logging  
8. Search functionality using Elasticsearch  

**Key Points:**  
- Tenant isolation: every record contains tenantId  
- Audit logs: all lifecycle events stored in MongoDB  
- Scalable ingestion with Kafka and fast retrieval via Elasticsearch  

---

## 📋 Functional Requirements

### 1. Ingestion & Validation
- Messages are ingested from Email and Slack in JSON format.  
- Schema validation ensures required fields are present.  
- Invalid messages are rejected.  

### 2. Normalization & Canonical Form
- All ingested data is converted into a unified canonical schema.  
- Canonical messages are stored in MongoDB.  

### 3. Immutable Storage
- Raw messages are stored in both disk and MongoDB in an append-only manner.  
- No updates or deletes allowed to maintain immutability.  
- All actions are logged in audit logs.  

### 4. Deduplication
- Mongo DB is used to track message IDs.  
- Duplicates are skipped but logged.  

### 5. Retention Policies
- Retention periods can be set per channel and tenant.  
- Policies are stored in Postgres.  
  

### 6. Compliance Check
- Compliance rules are regex-based and defined in policy files.  
- Messages matching rules are flagged.  

### 7. Storing Flagged Messages
- Flagged metadata is stored in PostgreSQL.  
- Each entry contains message ID, tenant ID, rule ID, reason, status, and timestamp.  

### 8. Review Workflow
- APIs allow reviewers to list flagged messages and update their status.  
- All review actions are logged in MongoDB for audit purposes.  

### 9. Search Functionality
- Elasticsearch powers full-text and keyword search across normalized data.  
- Queries can filter by tenant, sender, subject, or content.  

### 10.Kafka
 - Kafka acts as the central event backbone across all microservices.

 - Each service consumes from one Kafka topic and produces to the next, ensuring loose coupling.

 - Messages are durable and replayable, enabling independent scaling of services and complete audit trails.
---

## 🔍 Audit Logging
- Every step (ingestion, normalization, compliance check, review) generates audit logs.  
- Audit logs include action, timestamp, tenant ID, and details.  
- Logs are stored in MongoDB for compliance tracking.  

---


