

buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:8.3.2") // MyApplication.java 오류로 코드 추가
        classpath("com.google.gms:google-services:4.4.1") // 최신 버전 확인
    }
}

