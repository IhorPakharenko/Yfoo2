plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.junit)
    alias(libs.plugins.kotlin)
    alias(libs.plugins.kotlin.compose.compiler)
    alias(libs.plugins.ksp)
    alias(libs.plugins.kotlin.parcelize)
    alias(libs.plugins.screenshot)
}

android {
    namespace = "com.isao.yfoo2"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.isao.yfoo2"
        minSdk = 21
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables.useSupportLibrary = true
    }

    applicationVariants.all {
        addJavaSourceFoldersToModel(file("build/generated/ksp/$name/kotlin"))
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            // TODO: Create a release signing config when releasing
            signingConfig = signingConfigs.getByName("debug")
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
    buildFeatures {
        compose = true
        buildConfig = true
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
            // See https://github.com/Kotlin/kotlinx.coroutines/issues/2023#issuecomment-858644393
            excludes += "META-INF/licenses/ASM"
            pickFirsts += "win32-x86-64/attach_hotspot_windows.dll"
            pickFirsts += "win32-x86/attach_hotspot_windows.dll"
        }
    }
    sourceSets.all {
        java.srcDirs("src/$name/kotlin")
    }
    testOptions {
        unitTests {
            isIncludeAndroidResources = true
        }
    }
    tasks.withType<Test> {
        testLogging { // Enables println() in unit tests
            events("standardOut")
        }
    }
    experimentalProperties["android.experimental.enableScreenshotTest"] = true

    lint {
        // Highlighted issue is no longer the case
        // with the introduction of stabilityConfigurationFile
        disable += "ComposeUnstableCollections"
    }
}

composeCompiler {
    enableStrongSkippingMode = true
    stabilityConfigurationFile = File(projectDir, "compose_stability.conf")
}

android.applicationVariants.all {
    tasks.named(
        "compileDebugKotlin",
        org.jetbrains.kotlin.gradle.tasks.KotlinCompilationTask::class.java
    ) {
        compilerOptions {
            freeCompilerArgs.add("-Xdebug")
        }
    }
    tasks.named(
        "compileDebugUnitTestKotlin",
        org.jetbrains.kotlin.gradle.tasks.KotlinCompilationTask::class.java
    ) {
        compilerOptions {
            freeCompilerArgs.add("-Xdebug")
        }
    }
}

ksp {
    arg("room.schemaLocation", "$projectDir/schemas")
}

dependencies {
    // Compose and UI Libraries
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.compose.ui)
    implementation(libs.compose.animation)
    implementation(libs.compose.ui.tooling.preview)
    implementation(libs.compose.material3)
    implementation(libs.compose.material)
    implementation(libs.compose.material.icons.extended)
    implementation(libs.compose.activity)
    implementation(libs.coil.compose)
    implementation(libs.accompanist.navigation.animation)
    implementation(libs.accompanist.placeholder.material)
    implementation(libs.accompanist.drawablepainter)
    debugImplementation(libs.compose.ui.tooling)
    debugImplementation(libs.compose.ui.test.manifest)
    androidTestImplementation(libs.compose.ui.test.junit4)

    // Room Libraries
    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    ksp(libs.room.compiler)

    // Koin Libraries
    implementation(libs.koin.core)
    implementation(libs.koin.core.coroutines)
    implementation(libs.koin.android)
    implementation(libs.koin.compose.viewmodel)
    implementation(libs.koin.compose)
    implementation(libs.koin.compose.navigation)
    implementation(libs.koin.annotations)
    ksp(libs.koin.ksp.compiler)

    // Other Libraries
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.retrofit.kotlinx.serialization.converter)
    implementation(libs.okhttp.logging.interceptor)
    implementation(libs.timber)
    implementation(libs.navigation.compose)
    coreLibraryDesugaring(libs.desugar.jdk.libs)
    lintChecks(libs.compose.lint.checks)

    // Testing Libraries
    testImplementation(libs.spek.dsl.jvm)
    testImplementation(libs.spek.runner.junit5)
    testImplementation(libs.kotlin.reflect)
    testImplementation(libs.kotest.runner.junit5)
    testImplementation(libs.kotest.assertions.core)
    testImplementation(libs.kotest.framework.datatest)
    testImplementation(libs.kotest.extensions.android)
    testImplementation(libs.kotest.extensions.koin)
    testImplementation(libs.junit)
    testImplementation(libs.robolectric)
    testImplementation(libs.junit.vintage.engine)
    androidTestImplementation(libs.kotest.runner.android)
    testImplementation(libs.koin.test)
    testImplementation(libs.koin.test.junit5)
    testImplementation(libs.turbine)
    testImplementation(libs.mockk.android)
    testImplementation(libs.mockk.agent)
    androidTestImplementation(libs.koin.test)
    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.androidx.test.espresso.core)
    androidTestImplementation(libs.kotest.assertions.android)
    testImplementation(libs.android.test.compose)
    testImplementation(libs.androidx.espresso.intents)

    screenshotTestImplementation(libs.compose.ui.tooling)

    testImplementation(project(":shared-test"))
    androidTestImplementation(project(":shared-test"))
}
