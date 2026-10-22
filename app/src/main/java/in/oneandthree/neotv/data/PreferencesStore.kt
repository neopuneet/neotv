package `in`.oneandthree.neotv.data

import android.content.Context

class PreferencesStore(context: Context) {
    private val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)

    fun getLocation(): WeatherLocation {
        val name = prefs.getString(KEY_LOC_NAME, null)
        val lat = prefs.getString(KEY_LOC_LAT, null)?.toDoubleOrNull()
        val lon = prefs.getString(KEY_LOC_LON, null)?.toDoubleOrNull()
        if (name.isNullOrBlank() || lat == null || lon == null) return Defaults.location
        return WeatherLocation(name = name, latitude = lat, longitude = lon)
    }

    fun setLocation(location: WeatherLocation) {
        prefs.edit()
            .putString(KEY_LOC_NAME, location.name)
            .putString(KEY_LOC_LAT, location.latitude.toString())
            .putString(KEY_LOC_LON, location.longitude.toString())
            .apply()
    }

    /** Null means use [Defaults.apps]. Non-null (even empty) means custom package list. */
    fun getCustomPackages(): List<String>? {
        if (!prefs.contains(KEY_APP_PACKAGES)) return null
        val raw = prefs.getString(KEY_APP_PACKAGES, "") ?: return emptyList()
        if (raw.isBlank()) return emptyList()
        return raw.split(',').map { it.trim() }.filter { it.isNotEmpty() }
    }

    fun setCustomPackages(packages: List<String>) {
        prefs.edit().putString(KEY_APP_PACKAGES, packages.joinToString(",")).apply()
    }

    fun clearCustomPackages() {
        prefs.edit().remove(KEY_APP_PACKAGES).apply()
    }

    fun getCachedWeather(): Pair<String, Long>? {
        val json = prefs.getString(KEY_WEATHER_JSON, null) ?: return null
        val at = prefs.getLong(KEY_WEATHER_AT, 0L)
        if (at == 0L) return null
        return json to at
    }

    fun cacheWeather(json: String) {
        prefs.edit()
            .putString(KEY_WEATHER_JSON, json)
            .putLong(KEY_WEATHER_AT, System.currentTimeMillis())
            .apply()
    }

    companion object {
        private const val PREFS = "neotv_prefs"
        private const val KEY_LOC_NAME = "loc_name"
        private const val KEY_LOC_LAT = "loc_lat"
        private const val KEY_LOC_LON = "loc_lon"
        private const val KEY_APP_PACKAGES = "app_packages"
        private const val KEY_WEATHER_JSON = "weather_json"
        private const val KEY_WEATHER_AT = "weather_at"
    }
}
