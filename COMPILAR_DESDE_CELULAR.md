# Compilar CineVerse desde tu celular usando GitHub Actions

No necesitás Android Studio ni una computadora encendida.

1. Desde el navegador del celular, iniciá sesión en https://github.com y creá un repositorio **privado** llamado `CineVerse-Android`.
2. Descomprimí este ZIP. Subí **el contenido de la carpeta `CineVerse_Android_0.5`**, no la carpeta contenedora, a la raíz del repositorio. Deben quedar `app/`, `build.gradle.kts`, `settings.gradle.kts` y `.github/workflows/android-apk.yml` en la raíz. Para subir carpetas desde el teléfono puede resultar más fácil usar una app Git o activar «Sitio para computadoras» en el navegador.
3. Entrá a **Actions → Compilar CineVerse APK → Run workflow** y ejecutalo en la rama principal.
4. Esperá el resultado. Abrí la ejecución terminada y descargá el archivo de la sección **Artifacts** llamado `CineVerse-Android-0.6-debug-APK`.
5. Extraé el ZIP descargado e instalá `app-debug.apk` en tu Android. Es una compilación de prueba, no una versión de Play Store.

## Importante
- La compilación se hará en GitHub, no fue ejecutada ni verificada en este entorno.
- El APK solo funciona cuando el servidor de Windows es accesible desde el teléfono. La dirección inicial es `http://192.168.0.113:8000`; si la IP cambia, usá la opción de cambiar servidor.
- No subas películas, contraseñas, tokens ni archivos `.cineverse_admin_token.txt` a GitHub.
- Este proyecto 0.6 todavía no incorpora Chromecast ni LibVLC nativo; el soporte de formatos depende de WebView y del servidor.
- Para actualizar una APK existente firmada con otra clave, puede ser necesario desinstalarla primero. Eso puede borrar datos locales de la aplicación.
