package `in`.oneandthree.neotv.settings

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.unit.dp
import androidx.tv.material3.Border
import androidx.tv.material3.ClickableSurfaceDefaults
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.Surface
import androidx.tv.material3.Text
import `in`.oneandthree.neotv.data.Defaults
import `in`.oneandthree.neotv.data.WeatherLocation
import `in`.oneandthree.neotv.ui.theme.DateStyle
import `in`.oneandthree.neotv.ui.theme.NeoAccent
import `in`.oneandthree.neotv.ui.theme.NeoBg
import `in`.oneandthree.neotv.ui.theme.NeoMuted
import `in`.oneandthree.neotv.ui.theme.NeoSurface
import `in`.oneandthree.neotv.ui.theme.NeoText
import `in`.oneandthree.neotv.ui.theme.WeatherStyle

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun SettingsScreen(
    initialLocation: WeatherLocation,
    selectedPackages: List<String>,
    installedApps: List<Pair<String, String>>,
    onSaveLocation: (WeatherLocation) -> Unit,
    onSaveApps: (List<String>) -> Unit,
    onResetApps: () -> Unit,
    onBack: () -> Unit,
) {
    var name by remember { mutableStateOf(initialLocation.name) }
    var lat by remember { mutableStateOf(initialLocation.latitude.toString()) }
    var lon by remember { mutableStateOf(initialLocation.longitude.toString()) }
    val selected = remember {
        mutableStateListOf<String>().also { it.addAll(selectedPackages) }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(NeoBg)
            .padding(48.dp),
    ) {
        Text(text = "Settings", style = WeatherStyle.copy(fontSize = WeatherStyle.fontSize * 1.4f))
        Spacer(Modifier.height(8.dp))
        Text(
            text = "Weather location and home apps. Changes apply when you return home.",
            style = DateStyle,
        )
        Spacer(Modifier.height(28.dp))

        Text(text = "Location", color = NeoAccent, style = WeatherStyle)
        Spacer(Modifier.height(12.dp))
        FieldRow(label = "Name", value = name, onValueChange = { name = it })
        Spacer(Modifier.height(10.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            FieldRow(label = "Latitude", value = lat, onValueChange = { lat = it }, modifier = Modifier.weight(1f))
            FieldRow(label = "Longitude", value = lon, onValueChange = { lon = it }, modifier = Modifier.weight(1f))
        }
        Spacer(Modifier.height(12.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            ActionChip("Use Sargasan") {
                name = Defaults.location.name
                lat = Defaults.location.latitude.toString()
                lon = Defaults.location.longitude.toString()
            }
            ActionChip("Save location") {
                val la = lat.toDoubleOrNull()
                val lo = lon.toDoubleOrNull()
                if (la != null && lo != null && name.isNotBlank()) {
                    onSaveLocation(WeatherLocation(name.trim(), la, lo))
                }
            }
            ActionChip("Reset apps to defaults") {
                selected.clear()
                selected.addAll(
                    Defaults.apps.map { it.packageNames.first() },
                )
                onResetApps()
            }
        }

        Spacer(Modifier.height(28.dp))
        Text(text = "Home apps (tap to toggle, max 6)", color = NeoAccent, style = WeatherStyle)
        Spacer(Modifier.height(12.dp))

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            items(installedApps, key = { it.first }) { (pkg, label) ->
                val on = selected.contains(pkg)
                Surface(
                    onClick = {
                        if (on) selected.remove(pkg)
                        else if (selected.size < 6) selected.add(pkg)
                        onSaveApps(selected.toList())
                    },
                    shape = ClickableSurfaceDefaults.shape(shape = RoundedCornerShape(12.dp)),
                    colors = ClickableSurfaceDefaults.colors(
                        containerColor = if (on) NeoSurface else NeoBg,
                        focusedContainerColor = NeoSurface,
                    ),
                    border = ClickableSurfaceDefaults.border(
                        focusedBorder = Border(
                            border = BorderStroke(2.dp, NeoAccent),
                            shape = RoundedCornerShape(12.dp),
                        ),
                    ),
                    scale = ClickableSurfaceDefaults.scale(focusedScale = 1.02f),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Column {
                            Text(text = label, color = NeoText, style = WeatherStyle)
                            Text(text = pkg, color = NeoMuted, style = DateStyle)
                        }
                        Text(
                            text = if (on) "ON" else "OFF",
                            color = if (on) NeoAccent else NeoMuted,
                            style = DateStyle,
                        )
                    }
                }
            }
        }

        Spacer(Modifier.height(16.dp))
        ActionChip("Back to home", onClick = onBack)
    }
}

@Composable
private fun FieldRow(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        Text(text = label, color = NeoMuted, style = DateStyle)
        Spacer(Modifier.height(4.dp))
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            singleLine = true,
            textStyle = WeatherStyle.copy(color = NeoText),
            cursorBrush = SolidColor(NeoAccent),
            modifier = Modifier
                .fillMaxWidth()
                .background(NeoSurface, RoundedCornerShape(10.dp))
                .padding(horizontal = 14.dp, vertical = 12.dp),
        )
    }
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
private fun ActionChip(label: String, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        shape = ClickableSurfaceDefaults.shape(shape = RoundedCornerShape(20.dp)),
        colors = ClickableSurfaceDefaults.colors(
            containerColor = NeoSurface,
            focusedContainerColor = NeoAccent.copy(alpha = 0.2f),
        ),
        border = ClickableSurfaceDefaults.border(
            focusedBorder = Border(
                border = BorderStroke(2.dp, NeoAccent),
                shape = RoundedCornerShape(20.dp),
            ),
        ),
        scale = ClickableSurfaceDefaults.scale(focusedScale = 1.05f),
    ) {
        Text(
            text = label,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
            color = NeoText,
            style = DateStyle,
        )
    }
}
