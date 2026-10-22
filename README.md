# neoTV

**Free, open-source minimal Android TV home** from [One and Three](https://www.oneandthree.in).

neoTV replaces the heavy Google / Xiaomi home screen with a quiet layout: a digital clock, local weather + AQI, and a few large app tiles. Built for slow Mi Boxes and Android TV devices.

> neoTV is a light home screen. Installing the APK does **not** automatically debloat other system apps. Optional ADB scripts below can disable stock launchers and clutter for more RAM headroom.

## Features

- Large digital clock + date
- Weather and US AQI via [Open-Meteo](https://open-meteo.com/) (no API key)
- Default apps: YouTube, Netflix, Prime Video, Airtel Xstream, ZEE5, SonyLIV
- Settings to change city and pick up to 6 installed apps
- Declared as `HOME` + Leanback launcher

## Install (end users)

### 1. Download

Grab the latest APK from [GitHub Releases](https://github.com/neopuneet/neotv/releases).

### 2. Sideload onto the TV

Pick one:

| Method | Steps |
|--------|--------|
| Phone | Use *ATV Tools* or *Send Files to TV*, send the APK, open it on the box |
| USB | Copy APK to a stick → open with a file manager → install (allow unknown sources if asked) |
| Computer | Enable USB debugging → `adb connect BOX_IP:5555` → `adb install -r neotv.apk` |

### 3. Set neoTV as Home

Xiaomi / Google TV often ignore “default launcher” unless the stock home is disabled.

```bash
adb shell cmd package set-home-activity in.oneandthree.neotv/.MainActivity
adb shell pm disable-user --user 0 com.google.android.apps.tv.launcherx
adb shell pm disable-user --user 0 com.google.android.tungsten.setupwraith
# Also disable any PatchWall / vendor launcher you find:
# adb shell pm list packages | grep -iE 'launcher|patchwall|tvhome'
adb reboot
```

Or use a helper such as *Launcher Manager* if you prefer not to run ADB yourself.

### 4. Configure

Open **Settings** on the neoTV home: pick your city and apps.

## Optional debloat (ADB)

After neoTV is Home:

```bash
./scripts/debloat.sh BOX_IP
```

Rollback:

```bash
./scripts/rebloat.sh BOX_IP
```

Scripts only run `pm disable-user` (reversible). They never touch Play Store, Google Play services, or your selected OTT apps.

## Build from source

```bash
./gradlew :app:assembleDebug
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

Requirements: JDK 17, Android SDK 35.

## License

MIT © One and Three

Support: support@oneandthree.in
