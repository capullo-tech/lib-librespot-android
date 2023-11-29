pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "lib-librespot-android"
include (":app")
include(":librespot-android-decoder")
include(":librespot-android-decoder-tremolo")
include(":librespot-android-sink")
include(":librespot-android-zeroconf-server")