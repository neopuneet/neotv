package `in`.oneandthree.neotv.data

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager

data class ResolvedApp(
    val id: String,
    val label: String,
    val packageName: String?,
    val installed: Boolean,
)

class AppResolver(private val context: Context) {
    private val pm: PackageManager = context.packageManager

    fun resolveHomeApps(store: PreferencesStore): List<ResolvedApp> {
        val custom = store.getCustomPackages()
        if (custom != null) {
            return custom.mapIndexed { index, pkg ->
                val installed = isInstalled(pkg)
                val label = if (installed) labelFor(pkg) else pkg.substringAfterLast('.')
                ResolvedApp(
                    id = "custom_$index",
                    label = label,
                    packageName = pkg.takeIf { installed },
                    installed = installed,
                )
            }
        }

        return Defaults.apps.map { shortcut ->
            val pkg = shortcut.packageNames.firstOrNull { isInstalled(it) }
            ResolvedApp(
                id = shortcut.id,
                label = shortcut.label,
                packageName = pkg,
                installed = pkg != null,
            )
        }
    }

    fun launch(packageName: String): Boolean {
        val intent = pm.getLeanbackLaunchIntentForPackage(packageName)
            ?: pm.getLaunchIntentForPackage(packageName)
            ?: return false
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
        return true
    }

    fun listLaunchableApps(): List<Pair<String, String>> {
        val leanback = Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LEANBACK_LAUNCHER)
        val launcher = Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER)
        val seen = linkedMapOf<String, String>()
        listOf(leanback, launcher).forEach { intent ->
            pm.queryIntentActivities(intent, 0).forEach { info ->
                val pkg = info.activityInfo.packageName
                if (pkg == context.packageName) return@forEach
                if (!seen.containsKey(pkg)) {
                    seen[pkg] = info.loadLabel(pm).toString()
                }
            }
        }
        return seen.entries
            .map { it.key to it.value }
            .sortedBy { it.second.lowercase() }
    }

    private fun isInstalled(packageName: String): Boolean =
        try {
            pm.getPackageInfo(packageName, 0)
            true
        } catch (_: PackageManager.NameNotFoundException) {
            false
        }

    private fun labelFor(packageName: String): String =
        try {
            val app = pm.getApplicationInfo(packageName, 0)
            pm.getApplicationLabel(app).toString()
        } catch (_: Exception) {
            packageName
        }
}
