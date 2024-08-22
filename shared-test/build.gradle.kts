plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin)
}

android {
    namespace = "com.example.yfoo2"
    compileSdk = 34

    defaultConfig {
        minSdk = 21

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17

        isCoreLibraryDesugaringEnabled = true
    }
    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {
    implementation(project(":app"))
    implementation(libs.spek.dsl.jvm)
    implementation(libs.spek.runner.junit5)
    implementation(libs.kotlin.reflect)
    implementation(libs.kotest.runner.junit5)
    implementation(libs.kotest.assertions.core)
    implementation(libs.kotest.framework.datatest)
    implementation(libs.kotest.extensions.koin)
    implementation(libs.junit)
    implementation("org.junit.vintage:junit-vintage-engine:5.10.3")

    implementation(libs.koin.test)
    implementation(libs.koin.test.junit5)
    implementation(libs.turbine)
    implementation(libs.mockk.android)
    implementation(libs.mockk.agent)
    implementation(libs.androidx.test.ext.junit)
    implementation(libs.androidx.test.espresso.core)
    implementation(libs.kotest.assertions.android)
    implementation("androidx.test.espresso:espresso-intents:3.6.1")
    implementation(libs.compose.ui.test.junit4)
    implementation(libs.timber)
    coreLibraryDesugaring(libs.desugar.jdk.libs)


}