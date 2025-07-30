plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.skinny.skinnyapp"
    compileSdk = 36  // ★★★ 안정적인 버전으로 변경

    defaultConfig {
        applicationId = "com.skinny.skinnyapp"
        minSdk = 26  // ★★★ 24로 낮춤 (Material 3 지원 + 호환성)
        targetSdk = 36  // ★★★ 안정적인 버전으로 변경
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

    // ★★★ Android 16 호환 Material Design 의존성 ★★★
    implementation("com.google.android.material:material:1.12.0")

    // ★★★ Compose BOM (Android 16 호환 버전) ★★★
    implementation(platform("androidx.compose:compose-bom:2024.11.00"))
    implementation(libs.material3)
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material:material-icons-extended")

    // ★★★ Core 라이브러리 (최신 버전) ★★★
    implementation("androidx.core:core-ktx:1.15.0")
    implementation("androidx.activity:activity-compose:1.9.3")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.7")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.7")

    // ★★★ Navigation (최신 버전) ★★★
    implementation("androidx.navigation:navigation-compose:2.8.4")

    // ★★★ Splash Screen (Android 16 지원) ★★★
    implementation("androidx.core:core-splashscreen:1.2.0-alpha02")

    // ★★★ Foundation (반응형 UI) ★★★
    implementation("androidx.compose.foundation:foundation")

    // ★★★ 기본 libs 의존성 (Version Catalog 사용) ★★★
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.navigation.runtime.android)
    implementation(libs.coil.compose)

    // ★★★ Compose BOM (버전 관리 통합) - 한 번만 선언 ★★★
    implementation(platform("androidx.compose:compose-bom:2023.10.01"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material:material-icons-extended")

    // ★★★ Core 라이브러리들 (최신 안정 버전) ★★★
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.activity:activity-compose:1.8.2")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0")

    // ★★★ Navigation Compose (최신 버전) ★★★
    implementation("androidx.navigation:navigation-compose:2.7.4")

    // ★★★ Material Design 3 지원 ★★★
    implementation("androidx.compose.material3:material3-window-size-class:1.1.2")

    // ★★★ Splash Screen API ★★★
    implementation("androidx.core:core-splashscreen:1.0.1")

    // ★★★ 반응형 UI 및 WindowInsets 지원 ★★★
    implementation("androidx.compose.foundation:foundation:1.5.4")

    // ★★★ 카메라 관련 (CameraX) ★★★
    implementation("androidx.camera:camera-core:1.3.0")
    implementation("androidx.camera:camera-camera2:1.3.0")
    implementation("androidx.camera:camera-lifecycle:1.3.0")
    implementation("androidx.camera:camera-view:1.3.0")

    // ★★★ 권한 관리 (Accompanist) ★★★
    implementation("com.google.accompanist:accompanist-permissions:0.32.0")

    // ★★★ 네트워크 통신 (Retrofit) ★★★
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")

    // ★★★ 코루틴 (최신 버전으로 통합) ★★★
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.7.3")

    // ★★★ 얼굴 인식 (MLKit) ★★★
    implementation("com.google.mlkit:face-detection:16.1.5")

    // ★★★ 이미지 처리 ★★★
    implementation("androidx.exifinterface:exifinterface:1.4.1")

    // ★★★ 테스트 의존성 (기존 유지) ★★★
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform("androidx.compose:compose-bom:2023.10.01"))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    // ★★★ 범용 적응형 UI 지원 라이브러리 추가 ★★★
    implementation("androidx.compose.material3:material3-window-size-class:1.1.2")
    implementation("androidx.compose.foundation:foundation-layout:1.5.4")
    implementation("androidx.window:window:1.2.0")
}
