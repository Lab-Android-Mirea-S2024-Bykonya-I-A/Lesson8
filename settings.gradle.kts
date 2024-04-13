pluginManagement {
    repositories {
        google()
        gradlePluginPortal()
    }
}


dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven(url = "https://jitpack.io")
    }
}

rootProject.name = "ru.mirea.Bykonya.Lesson8"
include(":app")
include(":app:yandexmapintegration")

include(":app:yandexdriver")
include(":app:osmmaps")
include(":app:mireaproject")
