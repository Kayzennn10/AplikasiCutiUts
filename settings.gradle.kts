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
        // Tambahan untuk jaga-jaga (biasanya opsional di sini)
        maven { url = uri("https://jitpack.io") }
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()

        // --- TAMBAHAN PENTING (WAJIB ADA) ---
        // Ini pintu gerbang supaya library Grafik bisa masuk
        maven { url = uri("https://jitpack.io") }
    }
}

rootProject.name = "AplikasiCuti"
include(":app")