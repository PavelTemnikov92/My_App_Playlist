// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
}

// Commenting out the force statement to use default dependency resolution
/*
allprojects {
    configurations.all {
        resolutionStrategy {
            // Force the specific version of fragment that should not have the resource issue
            force("androidx.fragment:fragment:1.8.5")
        }
    }
}
*/

