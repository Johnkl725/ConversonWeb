@echo off
REM ================================================================
REM  Script para crear regla de firewall
REM  DEBE EJECUTARSE COMO ADMINISTRADOR
REM ================================================================

echo.
echo ==============================================
echo  CREAR REGLA DE FIREWALL - Puerto 8080
echo ==============================================
echo.

REM Verificar si se ejecuta como administrador
net session >nul 2>&1
if %ERRORLEVEL% NEQ 0 (
    echo ERROR: Este script debe ejecutarse como ADMINISTRADOR
    echo.
    echo Click derecho en el archivo -^> Ejecutar como administrador
    echo.
    pause
    exit /b 1
)

echo Creando regla de firewall...
echo.

netsh advfirewall firewall add rule ^
    name="ConversorPDF-8080" ^
    dir=in ^
    action=allow ^
    protocol=TCP ^
    localport=8080 ^
    profile=private,public

if %ERRORLEVEL% EQU 0 (
    echo.
    echo ========================================
    echo  REGLA CREADA EXITOSAMENTE!
    echo ========================================
    echo.
    echo Puerto 8080 ahora esta abierto en el firewall
    echo Otras PCs en tu red podran acceder a la app
    echo.
) else (
    echo.
    echo ERROR: No se pudo crear la regla
    echo Verifica que tengas permisos de administrador
    echo.
)

pause
