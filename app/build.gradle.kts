plugins { id("com.android.application") }
android {
    namespace = "ar.cineverse.app"
    compileSdk = 35
    defaultConfig { applicationId = "ar.cineverse.app"; minSdk = 24; targetSdk = 28; versionCode = 20; versionName = "2.0-preview" }
}

 dependencies { implementation("com.google.android.gms:play-services-cast-framework:22.2.0") }
