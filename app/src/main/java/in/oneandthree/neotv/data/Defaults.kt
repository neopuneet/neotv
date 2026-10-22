package `in`.oneandthree.neotv.data

data class AppShortcut(
    val id: String,
    val label: String,
    val packageNames: List<String>,
)

data class WeatherLocation(
    val name: String,
    val latitude: Double,
    val longitude: Double,
)

object Defaults {
    val location = WeatherLocation(
        name = "Sargasan",
        latitude = 23.186,
        longitude = 72.636,
    )

    val apps = listOf(
        AppShortcut(
            id = "youtube",
            label = "YouTube",
            packageNames = listOf(
                "com.google.android.youtube.tv",
                "com.google.android.youtube.googletv",
                "com.google.android.youtube",
            ),
        ),
        AppShortcut(
            id = "netflix",
            label = "Netflix",
            packageNames = listOf("com.netflix.ninja", "com.netflix.mediaclient"),
        ),
        AppShortcut(
            id = "prime",
            label = "Prime Video",
            packageNames = listOf(
                "com.amazon.amazonvideo.livingroom",
                "com.amazon.avod.thirdpartyclient",
            ),
        ),
        AppShortcut(
            id = "xstream",
            label = "Airtel Xstream",
            packageNames = listOf(
                "tv.accedo.airtel.wynk",
                "com.xstream.interactive",
                "com.airtel.tv",
                "in.airtel.tv",
            ),
        ),
        AppShortcut(
            id = "zee5",
            label = "ZEE5",
            packageNames = listOf(
                "com.graymatrix.jtv",
                "com.zee5.television",
                "com.graymatrix.zee5",
            ),
        ),
        AppShortcut(
            id = "sonyliv",
            label = "SonyLIV",
            packageNames = listOf(
                "com.sonyliv",
                "com.sony.sonyliv",
                "com.sonyliv.tv",
            ),
        ),
    )
}
