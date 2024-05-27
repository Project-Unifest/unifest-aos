@file:Suppress("INLINE_FROM_HIGHER_PLATFORM")

plugins {
    alias(libs.plugins.unifest.android.feature)
    // alias(libs.plugins.compose.investigator)
    id("kotlin-parcelize")
}

android {
    namespace = "com.unifest.android.feature.map"
}

dependencies {
    implementations(
        projects.core.data,
        projects.feature.festival,

        libs.kotlinx.collections.immutable,
        libs.timber,
        libs.android.play.services.location,

        libs.bundles.naver.map.compose,
    )
}
