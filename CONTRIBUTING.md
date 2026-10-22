# Contributing to neoTV

Thanks for helping make Android TV feel lighter.

## Development

1. Install JDK 17 and Android SDK (API 35).
2. Clone this repo and open it in Android Studio, or build with Gradle:

```bash
./gradlew :app:assembleRelease
```

3. Sideload onto a device with USB debugging enabled:

```bash
adb install -r app/build/outputs/apk/release/app-release-unsigned.apk
```

## Guidelines

- Keep the home screen minimal: clock, weather/AQI, a handful of apps.
- Prefer D-pad / remote focus over touch-only patterns.
- Do not add analytics, ads, or account requirements.
- Debloat scripts must only `pm disable-user` (never uninstall system packages) and must ship with rollback.

## Support

Questions and issues: open a GitHub issue or email support@oneandthree.in.
