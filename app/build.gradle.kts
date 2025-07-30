// app/build.gradle.kts
plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android) // This will now use the version from libs.versions.toml (2.0.0)
    alias(libs.plugins.google.gms.googleServices)
    alias(libs.plugins.google.dagger.hilt.android)
    alias(libs.plugins.google.devtools.ksp) // This will now use the version from libs.versions.toml (2.0.0-1.0.21)
}

android {
    namespace = "com.midas.whatsapp"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.midas.whatsapp"
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
        jvmTarget = "11" // Keep this at 11 as Java 11 is commonly used now
    }

    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    // Existing core dependencies
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity.ktx)
    implementation(libs.androidx.constraintlayout)

    // MVVM Architecture Components
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.lifecycle.livedata.ktx)
    implementation(libs.androidx.fragment.ktx)

    // Room Database
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    implementation(libs.androidx.activity)
    // implementation(libs.androidx.activity) // This is redundant if you're using activity-ktx
    ksp(libs.androidx.room.compiler)

    // Navigation Component
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)

    // Retrofit & OkHttp
    implementation(libs.retrofit)
    implementation(libs.retrofit.converter.gson)
    implementation(libs.okhttp.logging.interceptor)

    // Firebase (using BoM)
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.auth.ktx)
    implementation(libs.firebase.firestore.ktx)
    implementation(libs.firebase.storage.ktx)
    implementation(libs.firebase.messaging.ktx)

    // Dagger Hilt
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)

    // Kotlin Coroutines
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.kotlinx.coroutines.core)

    // Lottie for animations
    implementation(libs.lottie)

    // Testing dependencies
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    // For local unit tests of ViewModels/Coroutines that use Hilt
    testImplementation(libs.hilt.android.testing)
    kspTest(libs.hilt.compiler)

    // For instrumented tests with Hilt
    androidTestImplementation(libs.hilt.android.testing)
    kspAndroidTest(libs.hilt.compiler)

    implementation(platform("com.google.firebase:firebase-bom:33.1.2")) // Use the latest BOM version
    implementation("com.google.firebase:firebase-database")

    // Material Design for TabLayout
    implementation("com.google.android.material:material:1.12.0") // Use the latest stable version
    // ViewPager2
    implementation("androidx.viewpager2:viewpager2:1.1.0") // Use the latest stable version

}