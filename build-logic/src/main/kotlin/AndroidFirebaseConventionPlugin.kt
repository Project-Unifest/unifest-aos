import com.unifest.android.Plugins
import com.unifest.android.applyPlugins
import com.unifest.android.implementation
import com.unifest.android.libs
import org.gradle.kotlin.dsl.dependencies

internal class AndroidFirebaseConventionPlugin : BuildLogicConventionPlugin(
    {
        applyPlugins(Plugins.GoogleServices, Plugins.FirebaseCrashlytics)

        dependencies {
            implementation(platform(libs.firebase.bom))
            implementation(libs.firebase.analytics)
            implementation(libs.firebase.crashlytics)
            implementation(libs.firebase.config)
        }
    },
)
