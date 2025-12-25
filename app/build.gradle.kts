import java.io.FileInputStream
import java.util.Properties

val signingPropertiesFile = rootProject.file("signing.properties")
val signingProperties = Properties()

if (signingPropertiesFile.exists()) {
    signingProperties.load(FileInputStream(signingPropertiesFile))
}

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("kotlin-kapt")
    id("com.google.gms.google-services") version "4.4.4" apply true
    id("com.google.dagger.hilt.android")
}

android {
    namespace = "com.badr1.ardraw"
    compileSdk = 36  // Fixed: removed the curly braces

    defaultConfig {
        applicationId = "com.badr1.ardraw"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0.0"  // ← Update version name

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    // Signing configs MUST come before buildTypes
    signingConfigs {
        create("release") {
            if (signingProperties.isNotEmpty()) {
                storeFile = file(signingProperties.getProperty("STORE_FILE"))
                storePassword = signingProperties.getProperty("STORE_PASSWORD")
                keyAlias = signingProperties.getProperty("KEY_ALIAS")
                keyPassword = signingProperties.getProperty("KEY_PASSWORD")
            }
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            ndk {
                debugSymbolLevel = "FULL"
            }
            // Apply signing config
            signingConfig = signingConfigs.getByName("release")
        }
        debug {
            isMinifyEnabled = false
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
    val navVersion = "2.8.9"
    val composeVersion = "1.9.4"
    val roomVersion = "2.8.2"
    val cameraxVersion = "1.5.1"

    // Room
    implementation("androidx.room:room-runtime:$roomVersion")
    implementation("androidx.room:room-ktx:$roomVersion")
    kapt("androidx.room:room-compiler:$roomVersion")

    // Navigation Compose
    implementation("androidx.navigation:navigation-compose:$navVersion")

    // Core KTX
    implementation("androidx.core:core-ktx:1.15.0")  // Updated version

    // Jetpack Compose
    implementation("androidx.compose.ui:ui:$composeVersion")
    implementation("androidx.compose.material:material:$composeVersion")
    implementation("androidx.compose.ui:ui-tooling-preview:$composeVersion")
    implementation("androidx.compose.foundation:foundation:$composeVersion")

    // Gson
    implementation("com.google.code.gson:gson:2.13.1")

    // Firebase
    implementation(platform("com.google.firebase:firebase-bom:34.6.0"))
    implementation("com.google.firebase:firebase-config")
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.firebase:firebase-database")
    implementation("com.google.firebase:firebase-storage")
    implementation("com.google.firebase:firebase-auth-ktx:22.3.1")

    // DataStore
    implementation("androidx.datastore:datastore-preferences-core:1.1.1")
    implementation("androidx.datastore:datastore-preferences:1.1.1")

    // Coil for image loading
    implementation("io.coil-kt:coil-compose:2.6.0")

    // CameraX
    implementation("androidx.camera:camera-core:$cameraxVersion")
    implementation("androidx.camera:camera-camera2:$cameraxVersion")
    implementation("androidx.camera:camera-lifecycle:$cameraxVersion")
    implementation("androidx.camera:camera-view:$cameraxVersion")
    implementation("androidx.camera:camera-extensions:$cameraxVersion")
    implementation("com.google.accompanist:accompanist-permissions:0.37.3")

    // Hilt
    implementation("androidx.hilt:hilt-navigation-compose:1.3.0")
    implementation("com.google.dagger:hilt-android:2.57.2")
    kapt("com.google.dagger:hilt-android-compiler:2.57.2")

    // ads
    implementation("com.google.android.gms:play-services-ads:23.5.0")

    // AndroidX & Material3
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.compose.foundation)
    implementation(libs.material)
    implementation(libs.androidx.compose.runtime)
    implementation(libs.androidx.compose.animation.core.lint)

    // Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}



//import java.io.FileInputStream
//import java.util.Properties
//
//val signingPropertiesFile = rootProject.file("signing.properties")
//val signingProperties = Properties()
//
//if (signingPropertiesFile.exists()) { // ⬅️ Change `exists()` to `exists`
//    signingProperties.load(FileInputStream(signingPropertiesFile))
//}
//
//plugins {
//    alias(libs.plugins.android.application)
//    alias(libs.plugins.kotlin.android)
//    alias(libs.plugins.kotlin.compose)
//    id("kotlin-kapt")
//    id("com.google.gms.google-services") version "4.4.4" apply true
//    id("com.google.dagger.hilt.android")
//
//
//}
//
//android {
//    namespace = "com.badr.ardraw"
//    compileSdk {
//        version = release(36)
//    }
//
//    defaultConfig {
//        applicationId = "com.badr.ardraw"
//        minSdk = 24
//        targetSdk = 36
//        versionCode = 3
//        versionName = "1.1.0"
//
//        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
//    }
//
//    buildTypes {
//        release {
//            isMinifyEnabled = true
//            isShrinkResources = true
//            proguardFiles(
//                getDefaultProguardFile("proguard-android-optimize.txt"),
//                "proguard-rules.pro"
//            )
//            ndk {
//                debugSymbolLevel = "FULL"
//            }
//        }
//        debug {
//            isMinifyEnabled = false
//        }
//    }
//    compileOptions {
//        sourceCompatibility = JavaVersion.VERSION_11
//        targetCompatibility = JavaVersion.VERSION_11
//    }
//    kotlinOptions {
//        jvmTarget = "11"
//    }
//    buildFeatures {
//        compose = true
//    }
//}
//
//dependencies {
//
//    implementation(libs.androidx.ui.graphics)
//    implementation(libs.androidx.compose.foundation)
//    implementation(libs.material)
//    implementation(libs.androidx.compose.runtime)
//    implementation(libs.androidx.compose.animation.core.lint)
//    val navVersion = "2.8.9"
//    val composeVersion = "1.9.4"   // or whichever 1.9.x version is stable
//    val roomVersion = "2.8.2"
//    val cameraxVersion = "1.5.1"
//
//    // Room
//    implementation("androidx.room:room-runtime:$roomVersion")
//    implementation("androidx.room:room-ktx:$roomVersion")
//    kapt("androidx.room:room-compiler:$roomVersion")
//
//    // Navigation Compose
//    implementation("androidx.navigation:navigation-compose:$navVersion")
//
//    // Core KTX
//    implementation("androidx.core:core-ktx:1.7.0")  // you can try bumping, but verify compatibility
//
//    // Jetpack Compose
//    implementation("androidx.compose.ui:ui:$composeVersion")
//    implementation("androidx.compose.material:material:$composeVersion")
//    implementation("androidx.compose.ui:ui-tooling-preview:$composeVersion")
//
//    implementation("com.google.code.gson:gson:2.13.1")
//
//
//
//    implementation(platform("com.google.firebase:firebase-bom:34.6.0"))
//    implementation("com.google.firebase:firebase-config")
//    implementation("com.google.firebase:firebase-analytics")
//    implementation("com.google.firebase:firebase-database")
//    implementation("com.google.firebase:firebase-storage")
//
//
//    implementation("androidx.datastore:datastore-preferences-core:1.1.1")
//    implementation("androidx.datastore:datastore-preferences:1.1.1")
//
//    implementation("io.coil-kt:coil-compose:2.6.0")
////    implementation("io.coil-kt:coil-network-okhttp:2.6.0")
//
//
//    implementation("androidx.camera:camera-core:$cameraxVersion")
//    implementation("androidx.camera:camera-camera2:$cameraxVersion")
//    implementation("androidx.camera:camera-lifecycle:$cameraxVersion")
//    implementation("androidx.camera:camera-view:$cameraxVersion")
//    implementation("androidx.camera:camera-extensions:$cameraxVersion")
//    implementation("com.google.accompanist:accompanist-permissions:0.37.3")
//
//    implementation("androidx.hilt:hilt-navigation-compose:1.3.0")
//    implementation("com.google.dagger:hilt-android:2.57.2")
//    kapt("com.google.dagger:hilt-android-compiler:2.57.2")
//
//
//    implementation(libs.androidx.core.ktx)
//    implementation(libs.androidx.lifecycle.runtime.ktx)
//    implementation(libs.androidx.activity.compose)
//    implementation(platform(libs.androidx.compose.bom))
//    implementation(libs.androidx.compose.ui)
//    implementation(libs.androidx.compose.ui.graphics)
//    implementation(libs.androidx.compose.ui.tooling.preview)
//    implementation(libs.androidx.compose.material3)
//    testImplementation(libs.junit)
//    androidTestImplementation(libs.androidx.junit)
//    androidTestImplementation(libs.androidx.espresso.core)
//    androidTestImplementation(platform(libs.androidx.compose.bom))
//    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
//    debugImplementation(libs.androidx.compose.ui.tooling)
//    debugImplementation(libs.androidx.compose.ui.test.manifest)
//}
//
//
//
//android {
//    // ... other android configurations
//
//    signingConfigs {
//        create("release") {
//            // 2. Assign the properties to the signing config
//            if (signingProperties.isNotEmpty()) {
//                storeFile = file(signingProperties.getProperty("STORE_FILE"))
//                storePassword = signingProperties.getProperty("STORE_PASSWORD")
//                keyAlias = signingProperties.getProperty("KEY_ALIAS")
//                keyPassword = signingProperties.getProperty("KEY_PASSWORD")
//            }
//        }
//    }
//
//    buildTypes {
//        release {
//            // 3. Apply the signing config to the 'release' build type
//            signingConfig = signingConfigs.getByName("release")
//        }
//    }
//}