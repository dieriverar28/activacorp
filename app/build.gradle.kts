import java.util.Properties
import java.io.FileInputStream

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kover)
}

val keystorePropertiesFile = rootProject.file("keystore.properties")
val keystoreProperties = Properties()
if (keystorePropertiesFile.exists()) {
    keystoreProperties.load(FileInputStream(keystorePropertiesFile))
}

android {
    namespace = "com.example.activacorp"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.activacorp"
        minSdk = 28
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    signingConfigs {
        create("release") {
            storeFile = rootProject.file(keystoreProperties.getProperty("storeFile") ?: "activacorp-release.jks")
            storePassword = keystoreProperties.getProperty("storePassword")
            keyAlias = keystoreProperties.getProperty("keyAlias")
            keyPassword = keystoreProperties.getProperty("keyPassword")
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            signingConfig = signingConfigs.getByName("release")
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    lint {
        checkReleaseBuilds = false
        abortOnError = false
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        compose = true
    }
    kotlinOptions {
        jvmTarget = "11"
    }
}

dependencies {
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.retrofit)
    implementation(libs.converter.gson)
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    testImplementation(libs.junit)
    testImplementation("io.mockk:mockk:1.13.13")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.8.1")
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    debugImplementation(libs.androidx.compose.ui.tooling)
    ksp(libs.androidx.room.compiler)
}
val versionApp = android.defaultConfig.versionName ?: "1.0"
val nombreApkSalida = "activacorp-v$versionApp-release.apk"

tasks.register<Copy>("exportarApkRelease") {
    notCompatibleWithConfigurationCache("Copia y renombra el APK de release")
    dependsOn("assembleRelease")
    from(layout.buildDirectory.dir("outputs/apk/release"))
    include("*.apk")
    into(layout.buildDirectory.dir("apk-exportadas"))
    rename(".*", nombreApkSalida)
    kover {
        reports {
            filters {
                includes {
                    classes(
                        "com.example.activacorp.util.Validaciones*",
                        "com.example.activacorp.util.Permisos*",
                        "com.example.activacorp.util.Fechas*",
                        "com.example.activacorp.util.Sesion*",
                        "com.example.activacorp.domain.usecase.*",
                        "com.example.activacorp.data.remote.ApiErrorMapper*",
                        "com.example.activacorp.data.remote.ApiMappers*",
                        "com.example.activacorp.data.repository.*",
                        "com.example.activacorp.pantallas.login.LoginViewModel*",
                        "com.example.activacorp.pantallas.dashboard.DashboardViewModel*",
                        "com.example.activacorp.pantallas.contacto.CrearContactoViewModel*",
                        "com.example.activacorp.pantallas.oportunidad.CrearOportunidadViewModel*",
                        "com.example.activacorp.pantallas.oportunidad.OportunidadesViewModel*",
                        "com.example.activacorp.pantallas.organizacion.CrearOrganizacionViewModel*",
                        "com.example.activacorp.pantallas.organizacion.OrganizacionesViewModel*"
                    )
                }
            }
        }
    }

}