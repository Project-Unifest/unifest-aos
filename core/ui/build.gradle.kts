@file:Suppress("INLINE_FROM_HIGHER_PLATFORM")

plugins {
    alias(libs.plugins.unifest.android.library)
    alias(libs.plugins.unifest.android.library.compose)
}

android {
    namespace = "com.unifest.android.core.ui"
}

dependencies {
    implementations(
        projects.core.common,
        projects.core.designsystem,
        projects.core.domain,

        libs.kotlinx.collections.immutable,
        libs.coil.compose,
    )
}
