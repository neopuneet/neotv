#!/usr/bin/env bash
# Install neoTV, set as Home, then optionally debloat.
# Prerequisite: USB debugging enabled on the TV.
# Usage: ./scripts/install.sh [host[:port]] [path/to/neotv.apk]
set -euo pipefail

HOST="${1:-192.168.1.14:5555}"
ROOT="$(cd "$(dirname "$0")/.." && pwd)"
APK="${2:-$ROOT/app/build/outputs/apk/debug/app-debug.apk}"
ADB="${ADB:-adb}"

if [[ ! -f "$APK" ]]; then
  echo "APK not found: $APK"
  echo "Build first: ./gradlew :app:assembleDebug"
  echo "Or download: https://github.com/neopuneet/neotv/releases"
  exit 1
fi

echo "Connecting to $HOST …"
"$ADB" connect "$HOST"
"$ADB" -s "$HOST" wait-for-device

echo "Installing $APK …"
"$ADB" -s "$HOST" install -r "$APK"

echo "Setting neoTV as Home …"
"$ADB" -s "$HOST" shell cmd package set-home-activity \
  in.oneandthree.neotv/.MainActivity || true

echo "Resolving OTT packages on device …"
"$ADB" -s "$HOST" shell pm list packages | grep -iE \
  'youtube|netflix|amazon|airtel|wynk|xstream|zee5|graymatrix|sonyliv|sony' || true

echo
echo "Next: disable stock launchers so Home sticks (Xiaomi/Google TV quirk)."
read -r -p "Run debloat now? [y/N] " ans
if [[ "${ans:-}" =~ ^[Yy]$ ]]; then
  "$ROOT/scripts/debloat.sh" "$HOST"
fi

echo "Rebooting …"
"$ADB" -s "$HOST" reboot
echo "Done. After reboot, Home should open neoTV."
