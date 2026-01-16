@echo off
REM ================================================================
REM  Script para ejecutar la aplicación Spring Boot
REM ================================================================

echo.
echo ========================================
echo  Conversor de Archivos a PDF - Web
echo ========================================
echo.

echo [1/2] Compilando con Maven...
call mvn clean package -DskipTests

if %ERRORLEVEL% NEQ 0 (
    echo.
    echo ERROR: Fallo la compilacion
    pause
    exit /b 1
)

echo.
echo [2/2] Iniciando aplicacion...
echo.
echo La aplicacion estara disponible en: http://localhost:8080
echo Presiona Ctrl+C para detener
echo.

java -jar target\converson-web-1.0.0.jar

pause
