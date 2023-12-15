plugins {
    id("com.android.application")
}

android {
    namespace = "com.example.challenge3"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.challenge3"
        minSdk = 26
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

    implementation("androidx.appcompat:appcompat:1.6.1")
    //implementation ("androidx.core:core:2.2.0")
    implementation("com.google.android.material:material:1.10.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation ("com.github.AnyChart:AnyChart-Android:1.1.5")
    implementation ("org.eclipse.paho:org.eclipse.paho.client.mqttv3:1.2.5")
    //implementation ("org.eclipse.paho:org.eclipse.paho.android.service:1.1.1")
    implementation ("androidx.legacy:legacy-support-v4:1.0.0")
    implementation ("com.github.hannesa2:paho.mqtt.android:3.5.1")
    implementation("androidx.room:room-common:2.6.1")
    annotationProcessor ("androidx.room:room-compiler:2.6.1")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}