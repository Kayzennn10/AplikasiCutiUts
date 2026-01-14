plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    // Plugin Firebase wajib
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.aplikasicuti"
    compileSdk = 34 // Sesuaikan dengan SDK kamu (biasanya 33 atau 34)

    defaultConfig {
        applicationId = "com.example.aplikasicuti"
        minSdk = 24
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
}

dependencies {
    // 1. Standar Bawaan Android
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")

    // 2. FIREBASE (Database)
    implementation("com.google.firebase:firebase-database:20.3.0")

    // 3. RETROFIT (API Libur)
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")

    // 4. SPLASH SCREEN (Animasi Pembuka)
    implementation("androidx.core:core-splashscreen:1.0.1")

    // 5. MPANDROIDCHART (Grafik Lingkaran - OPTIONAL G)
    // --- INI YANG BARU KITA TAMBAH ---
    implementation("com.github.PhilJay:MPAndroidChart:v3.1.0")

    // Testing
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}