plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.google.gms.google.services)
    alias(libs.plugins.kotlin.android)
}

android {

    namespace = "com.example.firstpage"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.firstpage"
        minSdk = 30
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


    dependencies {
        implementation(libs.appcompat)
        implementation(libs.material) // Ensure `libs.material` points to version 1.9.0 or your preferred version.
        implementation(libs.activity)
        implementation(libs.constraintlayout)
        implementation(libs.firebase.auth)
        implementation(libs.firebase.firestore)
        implementation(libs.recyclerview)
        implementation(libs.core.ktx)
        implementation(libs.material3.android)
        testImplementation(libs.junit)
        androidTestImplementation(libs.ext.junit)
        androidTestImplementation(libs.espresso.core)
        implementation(libs.cardview)
        implementation("com.google.android.material:material:1.9.0")

        // Firebase and Play Services dependencies
        implementation(platform("com.google.firebase:firebase-bom:33.5.1"))
        implementation("com.google.firebase:firebase-database")
        implementation("com.google.android.gms:play-services-auth:21.2.0")

    }
}
dependencies {
    implementation(libs.vision.common)
    implementation(libs.image.labeling.common)
    implementation(libs.image.labeling.default.common)
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.play.services.location)
    implementation(libs.cardview)
    implementation(libs.mlkit.image.labeling)
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.database)
    implementation(libs.play.services.auth)
    implementation(libs.firebase.storage)
}
