@echo off
cls
echo ========================================
echo   Starting VertDrop SDMS with Docker
echo ========================================
echo.

echo [1/4] Cleaning old containers...
docker-compose down

echo.
echo [2/4] Checking ports...
netstat -ano | findstr :5432 >nul
if %errorlevel%==0 (
    echo WARNING: Port 5432 is in use!  Stop PostgreSQL service first.
    pause
    exit
)

netstat -ano | findstr :8080 >nul
if %errorlevel%==0 (
    echo WARNING: Port 8080 is in use!
    pause
    exit
)

echo.
echo [3/4] Building and starting containers...
echo This may take 3-5 minutes...
docker-compose up -d --build

echo.
echo [4/4] Waiting for services to be healthy...
timeout /t 30 /nobreak > nul

echo.
echo ========================================
echo   Application Status
echo ========================================
docker-compose ps

echo.
echo ========================================
echo   Next Steps
echo ========================================
echo   View logs:        docker-compose logs -f app
echo   Stop:            docker-compose down
echo   Health check:    curl http://localhost:8080/actuator/health
echo.
echo   API:  http://localhost:8080
echo ========================================
echo.
pause