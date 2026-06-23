# Versioning and Install Policy

## Versioning

Android uses two separate values:

- `versionCode`: internal positive integer used to decide upgrade/downgrade order.
- `versionName`: user-visible version string.

Project policy:

- Increment `versionCode` for every APK intended for installation on a device.
- Never reuse or decrease `versionCode` for normal installs.
- Use readable pre-release version names such as `0.1.0`, `0.2.0`, etc.
- Keep version settings in `app/build.gradle.kts`, not in `AndroidManifest.xml`.

Current release:

- `versionCode = 23`
- `versionName = "0.11.1"`
- Purpose: refresh all widgets after reboot or app update so installed widgets do not stay on initial loading layouts

## Install Path

A debug APK is sufficient for personal sideloading as long as updates are built with the same local debug signing key. Public distributors must use and protect their own release signing key outside the repository.

Required device state:

- Developer options enabled.
- USB debugging or wireless debugging enabled.
- The development machine authorized by the device.
- `adb devices -l` shows the device as `device`.

Wireless setup requires the device's Developer options > Wireless debugging screen, not just the IP address:

```sh
adb pair <device-ip>:<pairing-port>
adb connect <device-ip>:<debug-port>
adb devices -l
scripts/push-widget.sh clock
scripts/push-widget.sh calendar
scripts/push-widget.sh quote
scripts/push-widget.sh chores
scripts/push-widget.sh countdown
scripts/push-widget.sh weather
scripts/push-widget.sh photos
```

For repeated local installs, set `COZYLA_ADB_TARGET=<device-ip>:<debug-port>` in `.env`. The push script will try `adb connect` before installing.

Manual APK sideloading is a fallback, but it is worse for iteration. ADB install is the correct path for rapid widget testing.

Sources:

- https://developer.android.com/studio/publish/versioning
- https://developer.android.com/reference/android/widget/RemoteViews
- https://developer.android.com/reference/android/widget/TextClock
