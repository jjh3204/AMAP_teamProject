plugins {
    //alias(libs.plugins.androidApplication)
    //alias(libs.plugins.googleServices)
    id("com.android.application") // MyApplication.java 오류로 코드 추가
    id("com.google.gms.google-services") // Firebase 플러그인 추가
}

android {
    namespace = "com.example.a24amapteamproject"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.a24amapteamproject"
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
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    // Firebase 의존성 추가
    implementation("com.google.firebase:firebase-analytics:22.0.0")
    implementation("com.google.firebase:firebase-auth:23.0.0")
    implementation("com.google.firebase:firebase-database:21.0.0") // 이 코드는 쓰지 않아도 된다고 한다
    implementation("com.google.android.gms:play-services-auth:21.1.1") // MainActivity.java 오류 해결
    implementation ("com.google.firebase:firebase-firestore:25.0.0") // 마이페이지에 사용자 이름 표시

    /*implementation(libs.firebaseAnalytics) // 변경됨
    implementation(libs.firebaseAuth) // 변경됨
    implementation(libs.playServicesAuth) // 변경됨
    */

}

apply(plugin = "com.google.gms.google-services") // MyApplication.java 오류로 코드 추가