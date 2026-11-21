@echo off
setlocal
cd /d "%~dp0"
if not exist gradlew.bat (
 echo No se encontro Gradle Wrapper. Abrir esta carpeta en Android Studio,
 echo esperar la sincronizacion y usar Build ^> Build APK(s).
 pause
 exit /b 1
)
call gradlew.bat assembleDebug
if errorlevel 1 (echo Error de compilacion & pause & exit /b 1)
echo APK: app\build\outputs\apk\debug\app-debug.apk
pause
