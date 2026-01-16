@echo off
REM ================================================================
REM  Script de Verificacion Rapida de Red
REM  Para Conversor PDF Web
REM ================================================================

echo.
echo ==============================================
echo  VERIFICACION DE RED - Conversor PDF
echo ==============================================
echo.

echo [1] TU IP LOCAL:
echo ----------------------------------------
ipconfig | findstr /i "IPv4"
echo.

echo [2] PUERTOS ESCUCHANDO (debe aparecer 8080):
echo ----------------------------------------
netstat -an | findstr :8080
echo.

echo [3] REGLA DE FIREWALL:
echo ----------------------------------------
netsh advfirewall firewall show rule name="ConversorPDF-8080" 2>nul
if %ERRORLEVEL% NEQ 0 (
    echo WARNING: No se encontro regla de firewall
    echo Ejecuta: crear-regla-firewall.bat
)
echo.

echo ==============================================
echo  INSTRUCCIONES:
echo ==============================================
echo.
echo 1. Anota tu IP (ej: 192.168.1.100)
echo 2. En otra PC, abre navegador
echo 3. Ir a: http://TU-IP:8080
echo.
echo Ejemplo: http://192.168.1.100:8080
echo.

pause
