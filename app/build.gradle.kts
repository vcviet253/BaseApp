plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("com.google.dagger.hilt.android")
    id("com.google.devtools.ksp")
    kotlin("plugin.serialization") version "1.9.22"
}

android {
    namespace = "com.example.mealplanner"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.mealplanner"
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

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    // ViewModel and LiveData
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.8.7")  // Make sure to use the latest version

    // ViewModel Compose integration
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.6.0")  // Ensure this version matches with the ViewModel version

    // Lifecycle runtime (optional, but recommended for lifecycle-aware components)
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.0")

    //Room database
    val room_version = "2.6.1" // Use the latest version
    implementation("androidx.room:room-runtime:$room_version")
    ksp("androidx.room:room-compiler:$room_version") // Use KSP if preferred
    implementation("androidx.room:room-ktx:$room_version")

    //Color picker
    implementation("com.github.skydoves:colorpicker-compose:1.0.0")

    // Hilt - Dependency Injection
    implementation("com.google.dagger:hilt-android:2.50")
    ksp("com.google.dagger:hilt-compiler:2.50")

    // Hilt ViewModel
    implementation("androidx.hilt:hilt-navigation-compose:1.2.0")

    // Dagger Hilt WorkManager integration
    implementation("androidx.hilt:hilt-work:1.0.0")

    //Work manager
    implementation("androidx.work:work-runtime:2.10.0")

    // Retrofit core
    implementation("com.squareup.retrofit2:retrofit:2.11.0")

    // Converter (Gson is common; use Moshi if preferred)
    implementation("com.squareup.retrofit2:converter-gson:2.11.0")

    // OkHttp for logging (optional but useful)
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")

    // OkHttp (Networking)
    implementation("com.squareup.okhttp3:okhttp:4.12.0")

    //Json serialization
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.3")
    implementation("com.jakewharton.retrofit:retrofit2-kotlinx-serialization-converter:0.8.0")

    //NEVER USE GSON, USE SERIALIZATION KOTLIN INSTEAD

    // Image loading with Coil
    implementation("io.coil-kt:coil-compose:2.5.0")

    // Horizontal Pager
    implementation("com.google.accompanist:accompanist-pager:0.34.0")
    implementation("com.google.accompanist:accompanist-pager-indicators:0.34.0") // optional for page indicators

    //AndroidX Security (support EncryptedSharedPreferences with AES256 encryption)
    implementation ("androidx.security:security-crypto:1.1.0-alpha06")

    //Them ICONS Material
    implementation("androidx.compose.material:material-icons-extended:1.6.1")

}