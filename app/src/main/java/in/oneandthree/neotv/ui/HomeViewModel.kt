package `in`.oneandthree.neotv.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import `in`.oneandthree.neotv.data.AppResolver
import `in`.oneandthree.neotv.data.PreferencesStore
import `in`.oneandthree.neotv.data.ResolvedApp
import `in`.oneandthree.neotv.data.WeatherRepository
import `in`.oneandthree.neotv.data.WeatherSnapshot
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

data class HomeUiState(
    val timeText: String = "",
    val dateText: String = "",
    val weather: WeatherSnapshot? = null,
    val weatherLoading: Boolean = true,
    val apps: List<ResolvedApp> = emptyList(),
)

class HomeViewModel(application: Application) : AndroidViewModel(application) {
    private val prefs = PreferencesStore(application)
    private val resolver = AppResolver(application)
    private val weatherRepo = WeatherRepository(prefs)

    private val _state = MutableStateFlow(HomeUiState())
    val state: StateFlow<HomeUiState> = _state.asStateFlow()

    private val timeFmt = DateTimeFormatter.ofPattern("HH:mm")
    private val dateFmt = DateTimeFormatter.ofPattern("EEEE · d MMM", Locale.getDefault())

    init {
        refreshApps()
        viewModelScope.launch { tickClock() }
        viewModelScope.launch { weatherLoop() }
    }

    fun refreshApps() {
        _state.value = _state.value.copy(apps = resolver.resolveHomeApps(prefs))
    }

    fun launchApp(packageName: String) {
        resolver.launch(packageName)
    }

    private suspend fun tickClock() {
        while (viewModelScope.isActive) {
            val now = LocalDateTime.now()
            _state.value = _state.value.copy(
                timeText = now.format(timeFmt),
                dateText = now.format(dateFmt),
            )
            delay(1_000)
        }
    }

    private suspend fun weatherLoop() {
        while (viewModelScope.isActive) {
            _state.value = _state.value.copy(weatherLoading = _state.value.weather == null)
            val snap = weatherRepo.load(force = true)
            _state.value = _state.value.copy(weather = snap, weatherLoading = false)
            delay(30 * 60 * 1_000L)
        }
    }
}
