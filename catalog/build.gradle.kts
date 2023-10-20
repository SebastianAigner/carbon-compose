plugins {
    id("carbon.android.application")
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "dev.gabrieldrn.carboncatalog"

    defaultConfig {
        applicationId = "dev.gabrieldrn.carboncatalog"
        versionCode = 1
        versionName = "0.1"
    }
}

dependencies {
    implementation(project(":carbon"))

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.uiTooling)
    implementation(libs.androidx.lifecycle.runtime.ktx)

    testImplementation(libs.junit)

    androidTestImplementation(libs.androidx.test.ext)
    androidTestImplementation(libs.androidx.test.espresso)
}
