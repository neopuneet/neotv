#!/usr/bin/env bash
# Re-enable packages disabled by debloat.sh
# Usage: ./scripts/rebloat.sh [host[:port]]
set -euo pipefail

HOST="${1:-192.168.1.14:5555}"
ROOT="$(cd "$(dirname "$0")/.." && pwd)"
LOG="$ROOT/scripts/disabled-packages.log"
ADB="${ADB:-adb}"

if [[ ! -f "$LOG" ]]; then
  echo "No log at $LOG — nothing to restore."
  exit 1
fi

"$ADB" connect "$HOST" >/dev/null || true
"$ADB" -s "$HOST" wait-for-device

while IFS= read -r pkg; do
  [[ -z "$pkg" ]] && continue
  echo "Re-enabling $pkg"
  "$ADB" -s "$HOST" shell pm enable "$pkg" >/dev/null || true
done <"$LOG"

echo "Done. Reboot recommended: adb -s $HOST reboot"
