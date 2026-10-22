package `in`.oneandthree.neotv.settings

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import `in`.oneandthree.neotv.data.AppResolver
import `in`.oneandthree.neotv.data.Defaults
import `in`.oneandthree.neotv.data.PreferencesStore
import `in`.oneandthree.neotv.ui.theme.NeoTVTheme

class SettingsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val prefs = PreferencesStore(this)
        val resolver = AppResolver(this)
        val selected = prefs.getCustomPackages()
            ?: Defaults.apps.mapNotNull { shortcut ->
                shortcut.packageNames.firstOrNull { pkg ->
                    runCatching { packageManager.getPackageInfo(pkg, 0); true }.getOrDefault(false)
                } ?: shortcut.packageNames.first()
            }
        setContent {
            NeoTVTheme {
                SettingsScreen(
                    initialLocation = prefs.getLocation(),
                    selectedPackages = selected,
                    installedApps = resolver.listLaunchableApps(),
                    onSaveLocation = prefs::setLocation,
                    onSaveApps = prefs::setCustomPackages,
                    onResetApps = {
                        prefs.clearCustomPackages()
                    },
                    onBack = { finish() },
                )
            }
        }
    }
}
