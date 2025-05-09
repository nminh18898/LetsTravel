pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
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

rootProject.name = "Let's Travel"
include(":app")
include(":architecture")
include(":account")
include(":ui_components")
include(":app_navigation")
include(":core")
include(":discover")
include(":firebase")
include(":manage_trip")
include(":test_utils")
include(":photo_view")
include(":trip_data")
