# CineVerse Android 2.0 Preview

Proyecto Android Java que muestra el visor CineVerse existente en una WebView con reproducción a pantalla completa. Se conecta al servidor público en el puerto 8001; **no** al panel administrador 8000.

## Compilar con GitHub (sin Android Studio)
1. Crear un repositorio **privado** vacío en GitHub.
2. Subir **el contenido** de esta carpeta a la raíz del repositorio, incluida `.github/workflows/android.yml` (es una carpeta oculta).
3. En GitHub, ir a **Actions > Compilar CineVerse APK > Run workflow** (o subir a `main` para activar el flujo).
4. Esperar a que termine; entrar a la ejecución y descargar el artefacto `CineVerse-2.0-preview-APK`.
5. Descomprimir el artefacto y transferir `app-debug.apk` al teléfono.

## Configuración
Al abrir la app por primera vez, escribir la URL accesible del servidor público, p.ej. `http://192.168.1.50:8001` si esa es la IP real de la PC y ambos están en la misma Wi-Fi. `127.0.0.1` en el móvil **no** apunta a la PC. Fuera de la red local se necesita publicar el visor con HTTPS y controles de acceso; nunca exponer el puerto 8000 de administración.

## Limitaciones actuales
- **No incorpora Google Cast ni DLNA**; esto requiere integrar receptores, descubrimiento y reproducción remota.
- Muestra la interfaz HTML del servidor; la apariencia estilo Netflix depende de que esté instalado el visor CineVerse 1.0 en el puerto 8001.
- No convierte formatos incompatibles con Android; la reproducción depende del soporte del dispositivo/WebView.
- Es un APK **debug** firmado automáticamente para pruebas, no un paquete de publicación.
- Para instalaciones nuevas, desinstalar versiones anteriores si tienen una firma distinta; se perderán los datos locales de esa app.
- La compatibilidad del build se verifica al ejecutar GitHub Actions, no se ha compilado localmente.
