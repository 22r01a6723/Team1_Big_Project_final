@echo off
echo Starting ComplyVault Microservices...
echo.

echo Starting infrastructure services (Kafka, MongoDB, PostgreSQL, Elasticsearch)...
docker-compose up -d zookeeper kafka mongodb postgresql elasticsearch kibana

echo.
echo Waiting for infrastructure services to start...
timeout /t 30 /nobreak

echo.
echo Starting microservices...
echo.

echo Starting Ingestion Service (Port 8080)...
start "Ingestion Service" cmd /k "cd IngestionAndValidation && mvnw spring-boot:run"

echo Starting Normalizer Service (Port 8081)...
start "Normalizer Service" cmd /k "cd normalizer && mvnw spring-boot:run"

echo Starting Compliance Service (Port 8082)...
start "Compliance Service" cmd /k "cd Compliance && mvnw spring-boot:run"

echo Starting Search Service (Port 8083)...
start "Search Service" cmd /k "cd Search && mvnw spring-boot:run"

echo Starting Review Service (Port 8084)...
start "Review Service" cmd /k "cd review && mvnw spring-boot:run"

echo Starting Retention Service (Port 8085)...
start "Retention Service" cmd /k "cd Retention && mvnw spring-boot:run"

echo Starting Audit Service (Port 8086)...
start "Audit Service" cmd /k "cd Audit && mvnw spring-boot:run"

echo.
echo All services are starting...
echo Check individual terminal windows for service logs.
echo.
echo Service URLs:
echo - Ingestion Service: http://localhost:8080
echo - Normalizer Service: http://localhost:8081
echo - Compliance Service: http://localhost:8082
echo - Search Service: http://localhost:8083
echo - Review Service: http://localhost:8084
echo - Retention Service: http://localhost:8085
echo - Audit Service: http://localhost:8086
echo.
echo Press any key to exit...
pause > nul
