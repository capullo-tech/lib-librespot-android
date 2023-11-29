plugins {
    id("com.android.library")
    id("maven-publish")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "xyz.gianlu.librespot.player.decoders.tremolo"
    compileSdk = libs.versions.compileSdk.get().toInt()

    defaultConfig {
        minSdk = libs.versions.minSdk.get().toInt()

        testInstrumentationRunner  = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")

        // TODO use modern way to build native library
        sourceSets.getByName("main") {
            jniLibs.setSrcDirs(listOf("src/main/libs"))

            jni.setSrcDirs(listOf<String>()) //disable automatic ndk-build call
        }

        // call regular ndk-build(.cmd) script from app directory
        task<Exec>("ndkBuild") {
            commandLine(
                    "ndk-build.cmd",
                    "-C",
                    file("src/main").absolutePath
            )
        }

        //tasks.withType(JavaCompile) {
        // compileTask -> compileTask.dependsOn ndkBuild
        //}
    }

    buildTypes {
        release {
            isMinifyEnabled  = false
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
    implementation("xyz.gianlu.librespot:librespot-decoder-api:1.6.3")
}

publishing {
    publications {
        register<MavenPublication>("release") {
            groupId = "tech.capullo"
            artifactId = "librespot-android-decoder-tremolo"
            version = "0.1.0"

            afterEvaluate {
                from(components["release"])
            }
        }
    }
}
