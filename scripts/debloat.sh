#!/usr/bin/env bash
# Safely disable stock launcher / clutter on Android TV after neoTV is Home.
# Usage: ./scripts/debloat.sh [host[:port]]
set -euo pipefail

HOST="${1:-192.168.1.14:5555}"
ROOT="$(cd "$(dirname "$0")/.." && pwd)"
LOG="$ROOT/scripts/disabled-packages.log"
ADB="${ADB:-adb}"

never_touch=(
  "com.android.vending"
  "com.google.android.gms"
  "com.google.android.gsf"
  "com.google.android.youtube.tv"
  "com.netflix.ninja"
  "com.amazon.amazonvideo.livingroom"
  "in.oneandthree.neotv"
)

candidates=(
  "com.google.android.apps.tv.launcherx"
  "com.google.android.tungsten.setupwraith"
  "com.google.android.tvlauncher"
  "com.google.android.leanbacklauncher"
  "com.mitv.tvhome"
  "com.mitv.tvhome.atv"
  "com.xiaomi.mitv.systemui"
  "com.xiaomi.voicecontrol"
  "com.xiaomi.mitv.handbook"
  "com.xiaomi.android.tvsettings.overlay"
)

echo "Connecting to $HOST …"
"$ADB" connect "$HOST" >/dev/null || true
"$ADB" -s "$HOST" wait-for-device

home="$("$ADB" -s "$HOST" shell cmd shortcut get-default-launcher 2>/dev/null || true)"
echo "Current default launcher: ${home:-unknown}"

: >"$LOG"
disabled=0
for pkg in "${candidates[@]}"; do
  skip=false
  for keep in "${never_touch[@]}"; do
    [[ "$pkg" == "$keep" ]] && skip=true && break
  done
  $skip && continue

  if "$ADB" -s "$HOST" shell pm path "$pkg" >/dev/null 2>&1; then
    echo "Disabling $pkg"
    if "$ADB" -s "$HOST" shell pm disable-user --user 0 "$pkg" >/dev/null; then
      echo "$pkg" >>"$LOG"
      disabled=$((disabled + 1))
    fi
  fi
done

echo
echo "Also listing launcher-related packages (review manually):"
"$ADB" -s "$HOST" shell pm list packages | grep -iE 'launcher|home|patchwall|tvhome' || true
echo
echo "Disabled $disabled packages. Log: $LOG"
echo "Reboot recommended: adb -s $HOST reboot"
