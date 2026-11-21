plugins { id("com.android.application"); id("org.jetbrains.kotlin.android") }
android {
    namespace = "ar.cineverse.app"
    compileSdk = 35
    defaultConfig { applicationId = "ar.cineverse.app"; minSdk = 24; targetSdk = 28; versionCode = 6; versionName = "0.6.0" }
    compileOptions { sourceCompatibility = JavaVersion.VERSION_17; targetCompatibility = JavaVersion.VERSION_17 }
    kotlinOptions { jvmTarget = "17" }
}
