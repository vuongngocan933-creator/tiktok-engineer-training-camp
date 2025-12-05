import java.util.Properties
import java.io.FileInputStream

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("com.google.devtools.ksp")
}

android {
    namespace = "com.example.myapplication"
    compileSdk {
        version = release(36)
    }

    defaultConfig {
        applicationId = "com.example.myapplication"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"
        // 2. 读取 local.properties 中的值，并注入到 BuildConfig 类中
        // 获取 local.properties 文件
        val properties = Properties()
        val localPropertiesFile = project.rootProject.file("local.properties")
        if (localPropertiesFile.exists()) {
            properties.load(localPropertiesFile.inputStream())
        }

        // 读取 Key (如果读不到给个默认空值防止报错)
        val apiKey = properties.getProperty("DASHSCOPE_API_KEY") ?: ""

        // 生成名为 DASHSCOPE_API_KEY 的常量
        // 注意：value 部分需要转义引号，格式为 "\"真实的值\""
        buildConfigField("String", "DASHSCOPE_API_KEY", "\"$apiKey\"")

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
        buildConfig = true
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
            excludes += "META-INF/DEPENDENCIES"
        }
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.fragment)
    implementation(libs.androidx.recyclerview)
    implementation("androidx.compose.material:material-icons-extended")
    // 图片加载库 Coil
    implementation("io.coil-kt:coil-compose:2.6.0")
    // 1. 网络请求 (Retrofit + OkHttp)
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0") // JSON解析
    implementation("com.squareup.okhttp3:logging-interceptor:4.11.0") // 打印网络日志

    // 引入阿里云 DashScope SDK
    implementation("com.alibaba:dashscope-sdk-java:2.14.0") {
        // 1. 排除日志冲突 (之前提到过)
        exclude(group = "org.slf4j", module = "slf4j-simple")

        // 2. 【关键】排除它自带的 Guava (JRE版)，防止跟 Android 冲突
        exclude(group = "com.google.guava", module = "guava")

        // 3. 顺手排除 listenablefuture，防止报 "1.0" 那个错
        exclude(group = "com.google.guava", module = "listenablefuture")
    }
    // FFmpeg 用于音频转码
//    implementation("com.arthenica:smart-exception-java:0.2.1")
//    implementation(files("libs/ffmpeg-kit-fix.aar"))
    implementation("com.mrljdx:ffmpeg-kit-full:6.1.4")


    // 3. 【手动补充】添加 Android 兼容版的 Guava
    // 这样阿里云 SDK 运行时能找到 Guava，Android 构建也不会报错
    implementation("com.google.guava:guava:32.1.3-android")

    val room_version = "2.6.1"
    implementation("androidx.room:room-runtime:$room_version")
    implementation("androidx.room:room-ktx:$room_version")
    ksp("androidx.room:room-compiler:$room_version") // 使用 ksp 而不是 kapt
    implementation("androidx.navigation:navigation-compose:2.7.7")

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}