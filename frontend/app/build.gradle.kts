plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.hilt)  // Hilt plugin

    //alias(libs.plugins.ksp)   // KSP for annotation processing
    id("com.google.devtools.ksp")
    kotlin("kapt")
//    id ("kotlinx-serialization")
}

android {
    namespace = "com.wmc.eventplaner"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.wmc.eventplaner"
        minSdk = 24
        targetSdk = 35
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
    hilt {
        enableAggregatingTask = false
    }}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.benchmark.macro)
    implementation(libs.googleid)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    implementation("androidx.hilt:hilt-navigation-compose:1.2.0") // Check for the latest version
    // Hilt

    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)  // Use 'ksp' instead of 'kapt'
    implementation(libs.hilt.navigation.compose)  // For NavController integration
    implementation(libs.retrofit.converter.gson)
    implementation(libs.okhttp.logging.interceptor)
    implementation(libs.retrofit)
    implementation(libs.okhttp)

    implementation("com.squareup:javapoet:1.13.0")
    implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.5.0")

    // ViewModel Lifecycle
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.1") // Check for the latest version
    implementation ("com.google.android.gms:play-services-auth:21.0.0")
    //coil
    implementation("io.coil-kt.coil3:coil-compose:3.2.0")
    implementation("io.coil-kt.coil3:coil-network-okhttp:3.2.0")
    implementation("androidx.compose.material3:material3:1.3.2")
    implementation("androidx.compose.material3:material3-window-size-class:1.2.0")
    implementation ("androidx.compose.material3:material3:1.2.0-alpha05")
    implementation ("androidx.activity:activity-compose:1.7.2")
    implementation ("androidx.lifecycle:lifecycle-runtime-ktx:2.6.2")

        implementation ("androidx.credentials:credentials:1.5.0")
        implementation ("androidx.credentials:credentials-play-services-auth:1.5.0")
        implementation ("com.google.android.libraries.identity.googleid:googleid:1.1.1")
       implementation ("androidx.datastore:datastore-preferences:1.1.7")
      implementation ("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.1")

}