package `in`.oneandthree.neotv.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.util.concurrent.TimeUnit

data class WeatherSnapshot(
    val temperatureC: Int,
    val condition: String,
    val aqi: Int?,
    val aqiLabel: String,
    val locationName: String,
    val fetchedAtMs: Long,
)

class WeatherRepository(
    private val store: PreferencesStore,
    private val client: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(12, TimeUnit.SECONDS)
        .readTimeout(12, TimeUnit.SECONDS)
        .build(),
) {
    suspend fun load(force: Boolean = false): WeatherSnapshot = withContext(Dispatchers.IO) {
        val location = store.getLocation()
        val cached = store.getCachedWeather()
        if (!force && cached != null) {
            val age = System.currentTimeMillis() - cached.second
            if (age < CACHE_TTL_MS) {
                return@withContext parseCached(cached.first, cached.second, location.name)
            }
        }

        try {
            val weatherUrl =
                "https://api.open-meteo.com/v1/forecast" +
                    "?latitude=${location.latitude}&longitude=${location.longitude}" +
                    "&current=temperature_2m,weather_code" +
                    "&timezone=auto"
            val airUrl =
                "https://air-quality-api.open-meteo.com/v1/air-quality" +
                    "?latitude=${location.latitude}&longitude=${location.longitude}" +
                    "&current=us_aqi"

            val weatherJson = JSONObject(httpGet(weatherUrl))
            val airJson = runCatching { JSONObject(httpGet(airUrl)) }.getOrNull()

            val current = weatherJson.getJSONObject("current")
            val temp = current.getDouble("temperature_2m").toInt()
            val code = current.getInt("weather_code")
            val aqi = airJson
                ?.optJSONObject("current")
                ?.optInt("us_aqi", -1)
                ?.takeIf { it >= 0 }

            val snapshot = WeatherSnapshot(
                temperatureC = temp,
                condition = weatherLabel(code),
                aqi = aqi,
                aqiLabel = aqiBand(aqi),
                locationName = location.name,
                fetchedAtMs = System.currentTimeMillis(),
            )
            store.cacheWeather(toCacheJson(snapshot))
            snapshot
        } catch (_: Exception) {
            val fallback = store.getCachedWeather()
            if (fallback != null) {
                parseCached(fallback.first, fallback.second, location.name)
            } else {
                WeatherSnapshot(
                    temperatureC = 0,
                    condition = "Unavailable",
                    aqi = null,
                    aqiLabel = "—",
                    locationName = location.name,
                    fetchedAtMs = 0L,
                )
            }
        }
    }

    private fun httpGet(url: String): String {
        val req = Request.Builder().url(url).get().build()
        client.newCall(req).execute().use { resp ->
            if (!resp.isSuccessful) error("HTTP ${resp.code}")
            return resp.body?.string() ?: error("empty body")
        }
    }

    private fun toCacheJson(s: WeatherSnapshot): String =
        JSONObject()
            .put("temp", s.temperatureC)
            .put("condition", s.condition)
            .put("aqi", s.aqi ?: JSONObject.NULL)
            .put("aqiLabel", s.aqiLabel)
            .put("name", s.locationName)
            .toString()

    private fun parseCached(raw: String, at: Long, fallbackName: String): WeatherSnapshot {
        val o = JSONObject(raw)
        val aqi = if (o.isNull("aqi")) null else o.getInt("aqi")
        return WeatherSnapshot(
            temperatureC = o.getInt("temp"),
            condition = o.optString("condition", ""),
            aqi = aqi,
            aqiLabel = o.optString("aqiLabel", aqiBand(aqi)),
            locationName = o.optString("name", fallbackName),
            fetchedAtMs = at,
        )
    }

    companion object {
        private const val CACHE_TTL_MS = 30L * 60L * 1000L

        fun weatherLabel(code: Int): String = when (code) {
            0 -> "Clear"
            1, 2 -> "Partly cloudy"
            3 -> "Overcast"
            45, 48 -> "Fog"
            51, 53, 55, 56, 57 -> "Drizzle"
            61, 63, 65, 66, 67, 80, 81, 82 -> "Rain"
            71, 73, 75, 77, 85, 86 -> "Snow"
            95, 96, 99 -> "Storm"
            else -> "Outside"
        }

        fun aqiBand(aqi: Int?): String = when {
            aqi == null -> "—"
            aqi <= 50 -> "Good"
            aqi <= 100 -> "Moderate"
            aqi <= 150 -> "Sensitive"
            aqi <= 200 -> "Unhealthy"
            aqi <= 300 -> "Very unhealthy"
            else -> "Hazardous"
        }
    }
}
