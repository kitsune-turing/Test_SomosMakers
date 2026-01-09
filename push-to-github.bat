@echo off
echo ================================
echo Inicializando repositorio Git
echo ================================

cd /d "%~dp0"

echo.
echo [1/6] Inicializando Git...
git init

echo.
echo [2/6] Agregando archivos al staging...
git add .

echo.
echo [3/6] Creando primer commit...
git commit -m "Initial commit: BankLoad - Sistema de Gestion de Prestamos Bancarios con Redis Cache"

echo.
echo [4/6] Cambiando rama a main...
git branch -M main

echo.
echo [5/6] Agregando repositorio remoto...
git remote add origin https://github.com/kitsune-turing/Test_SomosMakers.git

echo.
echo [6/6] Subiendo codigo a GitHub...
git push -u origin main

echo.
echo ================================
echo Proceso completado exitosamente
echo ================================
echo.
echo El proyecto ha sido subido a:
echo https://github.com/kitsune-turing/Test_SomosMakers
echo.
pause

