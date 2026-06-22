#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
cd "$ROOT_DIR"

EXPLICIT_DEVICE_IP="${COZYLA_DEVICE_IP:-}"
EXPLICIT_ADB_TARGET="${COZYLA_ADB_TARGET:-}"

if [[ -f ".env" ]]; then
  set -a
  # shellcheck disable=SC1091
  source ".env"
  set +a
fi

if [[ -n "$EXPLICIT_DEVICE_IP" ]]; then
  COZYLA_DEVICE_IP="$EXPLICIT_DEVICE_IP"
fi
if [[ -n "$EXPLICIT_ADB_TARGET" ]]; then
  COZYLA_ADB_TARGET="$EXPLICIT_ADB_TARGET"
fi

WIDGET_ID="${1:-clock}"

case "$WIDGET_ID" in
  clock|calendar)
    ;;
  *)
    echo "Unknown widget: $WIDGET_ID"
    echo "Available widgets: clock, calendar"
    exit 64
    ;;
esac

scripts/check-repo-privacy.sh
./gradlew testDebugUnitTest lintDebug assembleDebug

is_authorized_target() {
  local target="$1"
  adb devices \
    | awk -v target="$target" \
      '$1 == target && $2 == "device" { found = 1 } END { exit found ? 0 : 1 }'
}

INSTALL_TARGET="${COZYLA_ADB_TARGET:-}"
if [[ -n "$INSTALL_TARGET" ]]; then
  adb connect "$INSTALL_TARGET" || true
  if ! is_authorized_target "$INSTALL_TARGET"; then
    DISCOVERED_TARGET=""
    if [[ -n "${COZYLA_DEVICE_IP:-}" ]]; then
      DISCOVERED_TARGET="$(
        adb mdns services \
          | awk -v ip="$COZYLA_DEVICE_IP" \
            '$2 == "_adb-tls-connect._tcp" && index($3, ip ":") == 1 { print $3; exit }'
      )"
    fi

    if [[ -n "$DISCOVERED_TARGET" ]]; then
      INSTALL_TARGET="$DISCOVERED_TARGET"
      adb connect "$INSTALL_TARGET" || true
    fi
  fi
fi

if [[ -n "$INSTALL_TARGET" ]]; then
  if ! is_authorized_target "$INSTALL_TARGET"; then
    echo "Configured Cozyla ADB target is not authorized: $INSTALL_TARGET"
    adb devices -l
    exit 1
  fi
elif ! adb devices | awk 'NR > 1 && $2 == "device" { found = 1 } END { exit found ? 0 : 1 }'; then
  echo "No authorized ADB device is available."
  echo
  adb devices -l
  echo
  echo "Pair or connect your Cozyla device first:"
  echo "  adb pair <device-ip>:<pairing-port>"
  echo "  adb connect <device-ip>:<debug-port>"
  echo
  echo "If you use .env, set:"
  echo "  COZYLA_DEVICE_IP=<device-ip>"
  echo "  COZYLA_ADB_TARGET=<device-ip>:<debug-port>"
  exit 1
fi

if [[ -n "$INSTALL_TARGET" ]]; then
  ANDROID_SERIAL="$INSTALL_TARGET" ./gradlew installDebug
else
  ./gradlew installDebug
fi

echo "Installed widget APK. Add or refresh the '$WIDGET_ID' widget from the device launcher."
