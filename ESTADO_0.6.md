# CineVerse Android 0.6 — proyecto fuente

## Implementado en el código
- Cliente WebView conectado al servidor configurable, con dirección inicial `http://192.168.0.113:8000`.
- Pantalla completa inmersiva horizontal cuando el reproductor HTML solicita fullscreen.
- Error de conexión con reintento y cambio manual de dirección.
- Conservación del estado WebView en recreación de Activity.
- Permisos INTERNET y red; HTTP local permitido para servidores LAN.

## No implementado / no verificado
- Chromecast/Cast SDK, VLC nativo, detección automática de IP, reproducción garantizada de MKV/AVI, acceso remoto seguro.
- No se ha modificado ni probado el backend Windows ni eliminado la sección TV del backend.
- No se ha compilado ni probado un APK en un teléfono en este entorno.

## Cómo compilar
Abrir esta carpeta en Android Studio (JDK 17, SDK Android 35). Esperar Gradle Sync y usar Build > Build APK(s). El resultado esperado está en `app/build/outputs/apk/debug/app-debug.apk`. La compilación requiere internet para descargar dependencias si no están en caché.

## Conexión
El servidor debe escuchar en `0.0.0.0:8000`, Windows Firewall debe permitir la conexión en red privada, y el teléfono debe usar la IP LAN real de la PC. Cambiarla desde el menú de la app.
