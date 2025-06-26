// project-level build.gradle.kts
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
    id("com.google.devtools.ksp") version "2.0.21-1.0.27" apply false
    // You don't need to add 'alias(libs.plugins.hilt.android) apply false' here
    // because it's defined in libs.versions.toml and will be resolved from there.
    // The key is ensuring settings.gradle.kts has the correct plugin repositories.
}