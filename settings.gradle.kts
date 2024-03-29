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

rootProject.name = "ru.mirea.Bykonya.Lesson8"
include(":app")
include(":app:yandexmapintegration")

include(":app:yandexdriver")
include(":app:osmmaps")
include(":app:mireaproject")
