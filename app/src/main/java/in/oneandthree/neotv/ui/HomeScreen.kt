package `in`.oneandthree.neotv.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.tv.material3.Border
import androidx.tv.material3.ClickableSurfaceDefaults
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.Glow
import androidx.tv.material3.Surface
import androidx.tv.material3.Text
import `in`.oneandthree.neotv.data.ResolvedApp
import `in`.oneandthree.neotv.data.WeatherSnapshot
import `in`.oneandthree.neotv.ui.theme.ClockStyle
import `in`.oneandthree.neotv.ui.theme.DateStyle
import `in`.oneandthree.neotv.ui.theme.NeoAccent
import `in`.oneandthree.neotv.ui.theme.NeoBg
import `in`.oneandthree.neotv.ui.theme.NeoMuted
import `in`.oneandthree.neotv.ui.theme.NeoSurface
import `in`.oneandthree.neotv.ui.theme.NeoText
import `in`.oneandthree.neotv.ui.theme.WeatherStyle

@Composable
fun HomeScreen(
    state: HomeUiState,
    onLaunch: (String) -> Unit,
    onOpenSettings: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(NeoBg, Color(0xFF121820), NeoBg),
                ),
            ),
    ) {
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .size(420.dp)
                .background(
                    Brush.radialGradient(
                        colors = listOf(NeoAccent.copy(alpha = 0.12f), Color.Transparent),
                    ),
                ),
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 64.dp, vertical = 40.dp),
        ) {
            TopBar(
                timeText = state.timeText,
                dateText = state.dateText,
                weather = state.weather,
                weatherLoading = state.weatherLoading,
            )
            Spacer(Modifier.height(48.dp))
            AppGrid(apps = state.apps, onLaunch = onLaunch)
            Spacer(Modifier.weight(1f))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "One and Three · free & open source",
                    color = NeoMuted.copy(alpha = 0.75f),
                    style = DateStyle,
                )
                SettingsChip(onClick = onOpenSettings)
            }
        }
    }
}

@Composable
private fun TopBar(
    timeText: String,
    dateText: String,
    weather: WeatherSnapshot?,
    weatherLoading: Boolean,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top,
    ) {
        Column {
            Text(text = timeText.ifBlank { "--:--" }, style = ClockStyle)
            Spacer(Modifier.height(4.dp))
            Text(text = dateText.uppercase(), style = DateStyle)
        }
        Column(horizontalAlignment = Alignment.End) {
            when {
                weather != null && weather.fetchedAtMs > 0L -> {
                    Text(
                        text = "${weather.locationName} · ${weather.temperatureC}° · ${weather.condition}",
                        style = WeatherStyle,
                    )
                    Spacer(Modifier.height(6.dp))
                    Text(
                        text = if (weather.aqi != null) {
                            "AQI ${weather.aqi} · ${weather.aqiLabel}"
                        } else {
                            "AQI —"
                        },
                        style = WeatherStyle.copy(color = NeoAccent),
                    )
                }
                weatherLoading -> Text("Updating weather…", style = WeatherStyle.copy(color = NeoMuted))
                else -> Text("Weather unavailable", style = WeatherStyle.copy(color = NeoMuted))
            }
        }
    }
}

@Composable
private fun AppGrid(apps: List<ResolvedApp>, onLaunch: (String) -> Unit) {
    val rows = apps.chunked(3)
    Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
        rows.forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(20.dp),
            ) {
                row.forEach { app ->
                    AppTileButton(
                        app = app,
                        onClick = { app.packageName?.let(onLaunch) },
                        modifier = Modifier.weight(1f),
                    )
                }
                repeat(3 - row.size) { Spacer(Modifier.weight(1f)) }
            }
        }
    }
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
private fun AppTileButton(
    app: ResolvedApp,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var focused by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(if (focused) 1.04f else 1f, label = "tileScale")
    val accents = listOf(
        Color(0xFFE50914),
        Color(0xFFFF4444),
        Color(0xFF00A8E1),
        Color(0xFFED1C24),
        Color(0xFF8230C9),
        Color(0xFFE8C547),
    )
    val accent = accents[app.id.hashCode().ushr(1) % accents.size]
    val enabled = app.installed && app.packageName != null

    Surface(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier
            .height(120.dp)
            .scale(scale)
            .onFocusChanged { focused = it.isFocused },
        shape = ClickableSurfaceDefaults.shape(shape = RoundedCornerShape(18.dp)),
        colors = ClickableSurfaceDefaults.colors(
            containerColor = NeoSurface,
            focusedContainerColor = NeoSurface,
            disabledContainerColor = NeoSurface.copy(alpha = 0.55f),
        ),
        border = ClickableSurfaceDefaults.border(
            focusedBorder = Border(
                border = BorderStroke(2.dp, NeoAccent),
                shape = RoundedCornerShape(18.dp),
            ),
        ),
        glow = ClickableSurfaceDefaults.glow(
            focusedGlow = Glow(elevationColor = NeoAccent.copy(alpha = 0.3f), elevation = 12.dp),
        ),
        scale = ClickableSurfaceDefaults.scale(focusedScale = 1f),
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
        ) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .width(36.dp)
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(if (enabled) accent else NeoMuted.copy(alpha = 0.4f)),
            )
            Column(modifier = Modifier.align(Alignment.BottomStart)) {
                Text(
                    text = app.label,
                    color = if (enabled) NeoText else NeoMuted,
                    style = WeatherStyle,
                )
                if (!enabled) {
                    Spacer(Modifier.height(4.dp))
                    Text(text = "Not installed", style = DateStyle)
                }
            }
        }
    }
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
private fun SettingsChip(onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        shape = ClickableSurfaceDefaults.shape(shape = RoundedCornerShape(24.dp)),
        colors = ClickableSurfaceDefaults.colors(
            containerColor = Color.Transparent,
            focusedContainerColor = NeoSurface,
        ),
        border = ClickableSurfaceDefaults.border(
            border = Border(
                border = BorderStroke(1.dp, NeoMuted.copy(alpha = 0.35f)),
                shape = RoundedCornerShape(24.dp),
            ),
            focusedBorder = Border(
                border = BorderStroke(2.dp, NeoAccent),
                shape = RoundedCornerShape(24.dp),
            ),
        ),
        scale = ClickableSurfaceDefaults.scale(focusedScale = 1.05f),
    ) {
        Text(
            text = "Settings",
            modifier = Modifier.padding(horizontal = 18.dp, vertical = 10.dp),
            color = NeoText,
            style = DateStyle,
        )
    }
}
