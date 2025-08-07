plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.filemanagerbylufic"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.filemanagerbylufic"
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
    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    implementation ("androidx.drawerlayout:drawerlayout:1.2.0")
    implementation ("com.google.android.material:material:1.12.0")

//pdf viewer
    //implementation ("com.github.barteksc:android-pdf-viewer:3.2.0-beta.1")
   // implementation ("com.github.barteksc:android-pdf-viewer:2.8.2")
    //implementation ("com.github.chrisbanes:PhotoView:2.3.0")
   // implementation ("com.github.barteksc:android-pdf-viewer:2.8.2")
    //implementation ("com.github.barteksc:android-pdf-viewer:3.2.0-beta")

//bottom bar
    implementation ("com.google.android.material:material:1.9.0")




    implementation ("com.github.bumptech.glide:glide:4.16.0")
    implementation(libs.lifecycle.livedata.ktx)
    implementation(libs.lifecycle.viewmodel.ktx)
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)
    implementation(libs.swiperefreshlayout)
    annotationProcessor ("com.github.bumptech.glide:compiler:4.16.0")

    implementation("com.mikepenz:fastadapter:3.3.1")
    implementation("com.mikepenz:fastadapter-commons:3.3.1")

    implementation("com.mikepenz:iconics-views:3.2.5")
    implementation("com.mikepenz:materialize:1.2.0@aar")
    implementation("com.mikepenz:community-material-typeface:2.7.94.1")

    implementation ("androidx.appcompat:appcompat:1.6.1")
    // Pie Charte
    implementation("com.github.PhilJay:MPAndroidChart:3.1.0")

   // implementation("com.futuremind.recyclerfastscroll:fastscroll:0.2.5")

    implementation("androidx.preference:preference-ktx:1.2.1")

    //meterial desigen
    implementation ("com.google.android.material:material:1.12.0")
    //BioMetrixx
    implementation ("androidx.biometric:biometric:1.1.0")

    //bumcat
    annotationProcessor ("com.github.bumptech.glide:compiler:4.16.0")

//Ai
    implementation ("com.squareup.okhttp3:okhttp:4.10.0")
    implementation ("com.google.code.gson:gson:2.9.0")



    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}