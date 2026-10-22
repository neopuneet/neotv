package `in`.oneandthree.neotv.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.darkColorScheme

val NeoBg = Color(0xFF0B0F14)
val NeoSurface = Color(0xFF161C24)
val NeoAccent = Color(0xFFE8C547)
val NeoMuted = Color(0xFF8A93A3)
val NeoText = Color(0xFFF2F4F7)

@OptIn(ExperimentalTvMaterial3Api::class)
private val NeoColors = darkColorScheme(
    primary = NeoAccent,
    onPrimary = NeoBg,
    surface = NeoSurface,
    onSurface = NeoText,
    background = NeoBg,
    onBackground = NeoText,
    border = Color(0xFF2A3340),
)

val ClockStyle = TextStyle(
    fontFamily = FontFamily.Serif,
    fontWeight = FontWeight.Medium,
    fontSize = 64.sp,
    letterSpacing = (-1).sp,
    color = NeoText,
)

val DateStyle = TextStyle(
    fontFamily = FontFamily.SansSerif,
    fontWeight = FontWeight.Normal,
    fontSize = 18.sp,
    color = NeoMuted,
)

val WeatherStyle = TextStyle(
    fontFamily = FontFamily.SansSerif,
    fontWeight = FontWeight.Medium,
    fontSize = 22.sp,
    color = NeoText,
)

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun NeoTVTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = NeoColors,
        content = content,
    )
}
