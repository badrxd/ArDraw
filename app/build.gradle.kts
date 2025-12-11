plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("kotlin-kapt")
    id("com.google.gms.google-services") version "4.4.4" apply true
//    id("com.google.gms.google-services")


}

android {
    namespace = "com.example.ardraw"
    compileSdk {
        version = release(36)
    }

    defaultConfig {
        applicationId = "com.example.ardraw"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
}

dependencies {

implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.compose.foundation)
    //    implementation(libs.firebase.storage.ktx)
    val navVersion = "2.8.9"
    val composeVersion = "1.9.4"   // or whichever 1.9.x version is stable
    val roomVersion = "2.8.2"
    val cameraxVersion = "1.3.0"

    // Room
    implementation("androidx.room:room-runtime:$roomVersion")
    implementation("androidx.room:room-ktx:$roomVersion")
    kapt("androidx.room:room-compiler:$roomVersion")

    // Navigation Compose
    implementation("androidx.navigation:navigation-compose:$navVersion")

    // Core KTX
    implementation("androidx.core:core-ktx:1.7.0")  // you can try bumping, but verify compatibility

    // Jetpack Compose
    implementation("androidx.compose.ui:ui:$composeVersion")
    implementation("androidx.compose.material:material:$composeVersion")
    implementation("androidx.compose.ui:ui-tooling-preview:$composeVersion")

    implementation("com.google.code.gson:gson:2.13.1")

//    implementation("com.google.firebase:firebase-storage-ktx:21.0.1")


    implementation(platform("com.google.firebase:firebase-bom:34.6.0"))
    implementation("com.google.firebase:firebase-storage")

    implementation("androidx.datastore:datastore-preferences:1.1.0")
    implementation("io.coil-kt:coil-compose:2.6.0")


//    implementation ("androidx.camera:camera-core:$cameraxVersion")
//    implementation ("androidx.camera:camera-camera2:$cameraxVersion")
//    implementation ("androidx.camera:camera-lifecycle:$cameraxVersion")
//    implementation ("androidx.camera:camera-view:$cameraxVersion")
//    implementation ("androidx.camera:camera-extensions:$cameraxVersion")
    implementation ("com.google.accompanist:accompanist-permissions:0.37.3")

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}