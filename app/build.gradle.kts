plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    id("kotlin-parcelize")
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
    id("com.google.gms.google-services")
    id("androidx.navigation.safeargs.kotlin")
}

android {
    namespace = "kr.nbc.momo"
    compileSdk = 34

    defaultConfig {
        applicationId = "kr.nbc.momo"
        minSdk = 33
        targetSdk = 34
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    viewBinding {
        enable = true
    }
}

dependencies {
    //room
    implementation(libs.room.runtime)
    implementation(libs.converter.gson)
    implementation(libs.gson)
    implementation(libs.logging.interceptor)
    ksp(libs.room.compiler)

    //retrofit
    implementation(libs.retrofit)

    //hilt
    implementation(libs.hilt)
    ksp(libs.hilt.compiler)
    //coil
    implementation(libs.coil)

    //fireBase
    implementation("com.google.firebase:firebase-analytics")
    implementation(platform(libs.firebase.bom))
    implementation("com.google.firebase:firebase-auth-ktx")
    implementation("com.google.firebase:firebase-database-ktx")
    implementation("com.google.firebase:firebase-firestore")
    implementation("com.google.firebase:firebase-storage")
    implementation("com.firebaseui:firebase-ui-storage:7.2.0")


    //navigation
    implementation ("androidx.navigation:navigation-fragment-ktx:2.7.7")
    implementation ("androidx.navigation:navigation-ui-ktx:2.7.7")

    //dataStore
    implementation ("androidx.datastore:datastore-preferences:1.0.0")

    implementation(libs.androidx.fragment.ktx)
    implementation(libs.firebase.storage.ktx)
    implementation(libs.androidx.activity.ktx)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}
