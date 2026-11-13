plugins {
 id("com.android.application")
 id("org.jetbrains.kotlin.android")
 id("org.jetbrains.kotlin.plugin.compose")
}
android {
 namespace = "com.zenvora.learningstudio"
 compileSdk = 36
 defaultConfig {
  applicationId = "com.zenvora.learningstudio"
  minSdk = 24
  targetSdk = 36
  versionCode = 2
  versionName = "1.0.1"
 }
 signingConfigs {
  create("release") {
   storeFile = file("zenvora-upload.jks")
   storePassword = System.getenv("ZENVORA_KEYSTORE_PASSWORD")
   keyAlias = System.getenv("ZENVORA_KEY_ALIAS")
   keyPassword = System.getenv("ZENVORA_KEY_PASSWORD")
  }
 }
 buildTypes {
  getByName("release") {
   signingConfig = signingConfigs.getByName("release")
   isMinifyEnabled = false
  }
 }
 compileOptions {
  sourceCompatibility = JavaVersion.VERSION_17
  targetCompatibility = JavaVersion.VERSION_17
 }
 kotlinOptions { jvmTarget = "17" }
 buildFeatures { compose = true }
}
dependencies {
 implementation(platform("androidx.compose:compose-bom:2024.12.01"))
 implementation("androidx.activity:activity-compose:1.10.0")
 implementation("androidx.compose.ui:ui")
 implementation("androidx.compose.ui:ui-tooling-preview")
 implementation("androidx.compose.material3:material3")
 implementation("androidx.compose.material:material-icons-extended")
 implementation("androidx.navigation:navigation-compose:2.8.5")
 implementation("com.squareup.okhttp3:okhttp:4.12.0")
 implementation("com.google.android.gms:play-services-ads:23.6.0")
 debugImplementation("androidx.compose.ui:ui-tooling")
}
