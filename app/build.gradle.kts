plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "com.practicum.myapp"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.practicum.myapp"
        minSdk = 29
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }

    packaging {
        resources {
            excludes += setOf("/META-INF/{AL2.0,LGPL2.1}")
        }
    }
}



dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat) {
        exclude(group = "androidx.fragment", module = "fragment")
    }
    implementation(libs.material) {
        exclude(group = "androidx.fragment", module = "fragment")
    }
    implementation(libs.androidx.activity) {
        exclude(group = "androidx.fragment", module = "fragment")
    }
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.recyclerview)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:2.0.4")
}