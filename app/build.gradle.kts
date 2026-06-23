plugins {
    id("com.android.application")
}

android {
    namespace = "com.cozyla.widgets"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.cozyla.widgets"
        minSdk = 23
        targetSdk = 35
        versionCode = 19
        versionName = "0.8.6"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    testOptions {
        unitTests {
            isIncludeAndroidResources = true
        }
    }
}

dependencies {
    testImplementation("junit:junit:4.13.2")
    testImplementation("androidx.test:core:1.7.0")
    testImplementation("org.robolectric:robolectric:4.16.1")
}
