@echo off
setlocal
set MAVEN_WRAPPER_DISABLE_SELF_UPDATE=true
set SCRIPT_DIR=%~dp0
set MVNW_CMD=%SCRIPT_DIR%..\Audit\mvnw.cmd
if exist "%MVNW_CMD%" (
  call "%MVNW_CMD%" %*
) else (
  echo Maven wrapper not found!
  exit /b 1
)
endlocal

