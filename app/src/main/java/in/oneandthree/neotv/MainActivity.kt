package `in`.oneandthree.neotv

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import `in`.oneandthree.neotv.settings.SettingsActivity
import `in`.oneandthree.neotv.ui.HomeScreen
import `in`.oneandthree.neotv.ui.HomeViewModel
import `in`.oneandthree.neotv.ui.theme.NeoTVTheme

class MainActivity : ComponentActivity() {
    private val viewModel: HomeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            NeoTVTheme {
                val state by viewModel.state.collectAsState()
                HomeScreen(
                    state = state,
                    onLaunch = viewModel::launchApp,
                    onOpenSettings = {
                        startActivity(Intent(this, SettingsActivity::class.java))
                    },
                )
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.refreshApps()
    }
}
