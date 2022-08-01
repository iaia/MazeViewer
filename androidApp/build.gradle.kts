plugins {
    id("com.android.application")
    kotlin("android")
}

android {
    compileSdk = 32

    defaultConfig {
        applicationId = "dev.iaiabot.maze.viewer.android"
        minSdk = 26
        targetSdk = 32
        versionCode = 1
        versionName = "1.0"
    }

    buildTypes {
        debug {
            isDebuggable = true
        }
        release {
            isMinifyEnabled = false
        }
    }

    buildFeatures {
        compose = true
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.2.0"
    }
}

dependencies {
    implementation(project(":shared"))
    implementation("com.google.android.material:material:1.6.1")
    implementation("androidx.appcompat:appcompat:1.4.2")

    implementation("androidx.compose.ui:ui:1.2.0")
    implementation("androidx.compose.ui:ui-tooling:1.2.0")
    implementation("androidx.compose.foundation:foundation:1.2.0")
    implementation("androidx.compose.material:material:1.2.0")
    implementation("androidx.compose.material:material-icons-core:1.2.0")
    implementation("androidx.compose.material:material-icons-extended:1.2.0")
    implementation("androidx.activity:activity-compose:1.5.1")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.5.1")
    implementation("androidx.compose.runtime:runtime-livedata:1.2.0")
    implementation("androidx.compose.runtime:runtime-rxjava2:1.2.0")

    val maze_version = "19.0"
    implementation("dev.iaiabot.maze:mazeEntity:$maze_version")
    implementation("dev.iaiabot.maze:mazeGenerator:${maze_version}")
    implementation("dev.iaiabot.maze:mazeResolver:${maze_version}")

    androidTestImplementation("androidx.compose.ui:ui-test-junit4:1.2.0")
}
