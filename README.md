# CineVerse Android 0.5 — Proyecto reconstruido

Proyecto Android Studio (Kotlin). Se conecta directamente al servidor CineVerse que ya está instalado en Windows.

## Compilar
1. Instalar Android Studio reciente y abrir esta carpeta.
2. Esperar la sincronización de Gradle (requiere internet y SDK Android 35).
3. Menú Build > Build APK(s).
4. APK de prueba: app/build/outputs/apk/debug/app-debug.apk.

## Uso
- PC: iniciar CineVerse en el puerto 8000 con su token configurado.
- Android y PC: misma red Wi-Fi.
- Dirección predeterminada: http://192.168.0.113:8000.
- Si cambia la IP: presionar Atrás en la pantalla principal y seleccionar Cambiar servidor, o usar la opción al fallar la conexión.
- La app carga la interfaz del servidor en WebView, y usa pantalla completa horizontal cuando el reproductor web lo solicita.

## Limitaciones importantes
- Es una primera integración funcional WebView, no un catálogo nativo ni una implementación de LibVLC.
- La compatibilidad con MKV y códecs depende de Android System WebView y del servidor. No se garantiza reproducir todos los formatos.
- No implementa Chromecast, descubrimiento automático de IP ni acceso remoto.
- HTTP sin cifrado es solo para red local confiable. No exponer el puerto 8000 a Internet.
- La pantalla del servidor puede incluir controles administrativos; no introducir ni guardar tokens en dispositivos ajenos.
- El ID de paquete puede ser distinto al de la APK anterior; se puede instalar como otra app.
