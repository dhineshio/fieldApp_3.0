plugins {
  alias(libs.plugins.android.application)
  alias(libs.plugins.jetbrains.kotlin.android)
}

android {
  namespace = "com.thaagam.field_app"
  compileSdk = 34

  viewBinding{
    enable = true
  }

  defaultConfig {
    applicationId = "com.thaagam.field_app"
    minSdk = 26
    targetSdk = 34
    versionCode = 1
    versionName = "1.0"

    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
  }

  buildTypes {
    release {
      isMinifyEnabled = false
      proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
    }
  }
  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
  }
  kotlinOptions {
    jvmTarget = "1.8"
  }
}

dependencies {

  implementation(libs.vision.common)
  implementation(libs.barcode.scanning.common)
  implementation(libs.play.services.mlkit.barcode.scanning)
  implementation(libs.androidx.work.runtime.ktx)
  // CameraX core library using the camera2 implementation
  val camerax_version = "1.4.0-alpha05"
  // The following line is optional, as the core library is included indirectly by camera-camera2
  implementation("androidx.camera:camera-core:${camerax_version}")
  implementation("androidx.camera:camera-camera2:${camerax_version}")
  // If you want to additionally use the CameraX Lifecycle library
  implementation("androidx.camera:camera-lifecycle:${camerax_version}")
  // If you want to additionally use the CameraX VideoCapture library
  implementation("androidx.camera:camera-video:${camerax_version}")
  // If you want to additionally use the CameraX View class
  implementation("androidx.camera:camera-view:${camerax_version}")
  // If you want to additionally add CameraX ML Kit Vision Integration
  implementation("androidx.camera:camera-mlkit-vision:${camerax_version}")
  // If you want to additionally use the CameraX Extensions library
  implementation("androidx.camera:camera-extensions:${camerax_version}")

  implementation(libs.androidx.core.ktx)
  implementation(libs.androidx.appcompat)
  implementation(libs.material)
  implementation(libs.androidx.activity)
  implementation(libs.androidx.constraintlayout)
  implementation(libs.androidx.swiperefreshlayout)
  implementation(libs.androidx.camera.view)
  testImplementation(libs.junit)
  androidTestImplementation(libs.androidx.junit)
  androidTestImplementation(libs.androidx.espresso.core)
}