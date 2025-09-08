@echo off
echo Testing ComplyVault Microservices...
echo.

echo Testing Retention Service...
echo Creating retention policy...
curl -X POST http://localhost:8085/api/retention-policies ^
  -H "Content-Type: application/json" ^
  -d "{\"tenantId\":\"test-tenant\",\"channel\":\"email\",\"retentionPeriodDays\":30}"

echo.
echo Testing Audit Service...
echo Logging audit event...
curl -X POST http://localhost:8086/api/audit/log ^
  -H "Content-Type: application/json" ^
  -d "{\"tenantId\":\"test-tenant\",\"messageId\":\"test-msg-123\",\"network\":\"email\",\"eventType\":\"TEST\",\"serviceName\":\"test-service\",\"details\":{\"status\":\"success\"}}"

echo.
echo Checking audit events for test tenant...
curl "http://localhost:8086/api/audit/tenant/test-tenant"

echo.
echo Testing completed!
echo Check the responses above for any errors.
echo.
pause
