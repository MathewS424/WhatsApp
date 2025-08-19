// build.gradle.kts (Project Level)
plugins {
    id("com.android.application") version "8.7.3" apply false // Use your actual AGP version
    // IMPORTANT: Align Kotlin plugin version to 2.0.0 (or higher) to match Firebase requirements
    id("org.jetbrains.kotlin.android") version "2.0.0" apply false // CHANGED FROM 1.9.0 to 2.0.0
    id("com.google.gms.google-services") version "4.4.1" apply false // Keep your current version
    // IMPORTANT: Update KSP plugin version to be compatible with Kotlin 2.0.0
    id("com.google.devtools.ksp") version "2.0.0-1.0.21" apply false // CHANGED FROM 1.9.0-1.0.13 to 2.0.0-1.0.21 (compatible with Kotlin 2.0.0)
}

