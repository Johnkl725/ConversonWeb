@echo off
REM ================================================================
REM  Script para ejecutar con Gradle
REM ================================================================

echo.
echo ========================================
echo  Conversor de Archivos a PDF - Web
echo  Ejecutando con Gradle
echo ========================================
echo.

echo Iniciando aplicacion Spring Boot...
echo.
echo La aplicacion estara disponible en: http://localhost:8080
echo Presiona Ctrl+C para detener
echo.

gradlew.bat bootRun

pause
