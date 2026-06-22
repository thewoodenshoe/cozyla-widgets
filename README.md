# Cozyla Widgets

Create and install your own Android widgets for a Cozyla device.

This repo contains native, resizable Android home-screen widgets. The shared APK provides a clock and a Monday-first calendar with configurable Week and Workweek views, calendar selection, and timeline hours.

## What You Need

- A Cozyla device or another Android device that supports home-screen widgets
- A Mac, Linux, or Windows development machine
- JDK 17
- Android Studio or Android SDK 35 command-line tools
- Android Platform Tools (`adb`) for device installation
- A USB connection, or the device IP address for wireless installation

Do not commit your device IP address, MAC address, serial number, pairing ports, screenshots, signing keys, or other local identifiers.

## Build From Source

Clone the repository and open it in Android Studio, or build with the included Gradle wrapper:

```sh
git clone https://github.com/thewoodenshoe/cozyla-widgets.git
cd cozyla-widgets
./gradlew testDebugUnitTest lintDebug assembleDebug
```

Android Studio normally creates the private `local.properties` SDK path. Command-line users must set `ANDROID_HOME` or create `local.properties` themselves. The debug APK is written to `app/build/outputs/apk/debug/app-debug.apk`.

No Google Cloud project, OAuth client, or API key is required. Android manages Google account authentication and calendar sync.

## Credentials And Permissions

The project does not accept or require Google credentials in source code, Gradle files, `.env`, or GitHub Actions.

- **Google account:** Add the account through Android Settings and enable Calendar sync. Never enter a Google password into this app or repository.
- **Calendar permission:** Grant `READ_CALENDAR` when Android prompts during widget setup. The app requests no write or network permission.
- **ADB pairing:** Enter the temporary pairing code directly into `adb pair`. Do not save it in `.env`, shell scripts, screenshots, issues, or commits.
- **Local device routing:** The optional ignored `.env` file may contain only the device address and current ADB connection target.
- **APK signing:** Android's local debug key is sufficient for personal sideloading. Anyone distributing a release must create and protect their own signing key outside this repository. Never commit a keystore or signing password.
- **GitHub:** No GitHub token is needed to build or install. Contributors should enable GitHub email privacy and use a GitHub noreply commit address.

## Device Setup

On the Cozyla device:

1. Open Settings.
2. Enable Developer options.
3. Enable USB debugging or Wireless debugging.
4. For wireless installs, open Wireless debugging and choose Pair device with pairing code.
5. Note the device IP address, pairing port, debug port, and pairing code.

From your computer:

```sh
adb pair <device-ip>:<pairing-port>
adb connect <device-ip>:<debug-port>
adb devices -l
```

When the device appears as `device`, installs are ready.

## Optional Local ADB Config

Create a private local config file:

```sh
cp .env.example .env
```

Edit `.env`:

```sh
COZYLA_DEVICE_IP=<device-ip>
COZYLA_ADB_TARGET=<device-ip>:<debug-port>
```

`.env` is ignored by git. Keep real device values there or in `docs/local/`.

## Push A Widget

Push the clock widget:

```sh
scripts/push-widget.sh clock
```

Push the Week Calendar widget:

```sh
scripts/push-widget.sh calendar
```

This runs the test suite, lint, build, checks that an authorized Android device is connected, and installs the debug APK. To install without the helper script, run `adb install -r app/build/outputs/apk/debug/app-debug.apk`.

You can also say something like “push the clock widget to the device” when working with Codex after the device is paired.

## Available Widgets

| Widget | Command | Status |
| --- | --- | --- |
| Clock | `scripts/push-widget.sh clock` | MVP |
| Week Calendar | `scripts/push-widget.sh calendar` | Device verified |

## Week Calendar Setup

The calendar widget reads Android's synced calendar provider. It does not store Google passwords or OAuth refresh tokens.

1. Add each Google account to Android on the Cozyla.
2. Enable Calendar sync for those accounts and confirm their events appear in Google Calendar.
3. Add `Week Calendar` from the launcher widget picker.
4. Grant calendar access in the configuration screen.
5. Choose Week or Workweek, select the visible timeline's From and To hours, and select any calendars across the synced accounts.

Each widget instance keeps its own view mode, timeline range, and calendar selection. Long-press and choose the launcher's reconfigure action, or use the settings control in a sufficiently wide widget, to change them later.

The widget supports previous week, current week, next week, manual refresh, and tap-to-open-calendar actions. Large placements default to a 6:00 AM-midnight hourly timeline. A shorter range, such as 8:00 AM-10:00 PM, gives each visible hour and event more vertical space. Overlapping timed events appear side by side and all-day events stay in a compact strip above the grid. Small placements use a reduced summary because an hourly timeline is not legible at compact heights. Calendar queries run through Android's `JobScheduler`, which keeps blocking provider work out of widget callbacks.

## Privacy And Sharing

The app requests only `READ_CALENDAR`; it does not request Android's `INTERNET` permission. It reads events from Android's local calendar provider, renders them in the widget, and does not write event titles, descriptions, account names, passwords, or OAuth tokens to app files, logs, or this repository. It stores only each widget's selected Android calendar IDs, Week/Workweek mode, timeline hours, and week offset in app-private preferences excluded from backup.

Widget broadcast receivers are not exported. The configuration Activity remains exported because Android launchers must open it, and the update service is protected by Android's signature-level `BIND_JOB_SERVICE` permission. Cleartext network traffic and application backup are disabled as defense in depth.

Before sharing a fork:

1. Keep device addresses and ports in `.env` or `docs/local/`; both are ignored.
2. Never add screenshots containing calendar data, signing keys, `local.properties`, `google-services.json`, or Android Studio captures.
3. Run `git grep` for names, email addresses, local paths, IP addresses, and credentials.
4. Inspect commit identity with `git log --format='%an <%ae>'` and audit old commits. `.gitignore` does not remove information already committed.
5. If private data entered Git history, rewrite the history before publishing, then rotate any exposed credentials.

Run the included current-tree check before every push. Use its stricter history mode before making a repository public:

```sh
scripts/check-repo-privacy.sh
scripts/check-repo-privacy.sh --history
```

CI runs the same privacy checks plus tests, lint, and APK assembly. GitHub Actions are pinned to immutable commits and Dependabot monitors Gradle and workflow dependencies. See [CONTRIBUTING.md](CONTRIBUTING.md) before submitting code and report vulnerabilities privately as described in [SECURITY.md](SECURITY.md).

## Build And Test

```sh
./gradlew testDebugUnitTest
./gradlew lintDebug
./gradlew assembleDebug
```

The debug APK is generated at:

```sh
app/build/outputs/apk/debug/app-debug.apk
```

## Project Structure

```text
app/src/main/java/com/cozyla/widgets/
  MainActivity.java
  calendar/
    CalendarWidgetProvider.java
    CalendarWidgetConfigureActivity.java
    CalendarWidgetUpdateJobService.java
    CalendarWidgetRenderer.java
  clock/
    ClockWidgetProvider.java

app/src/main/res/layout/
  activity_calendar_widget_configure.xml
  widget_calendar.xml
  widget_calendar_preview.xml
  widget_clock.xml

app/src/main/res/xml/
  calendar_widget_info.xml
  clock_widget_info.xml

scripts/
  push-widget.sh
```

Each widget has its own provider, layout, metadata XML, tests, and README entry.

## Versioning

Android uses:

- `versionCode`: internal integer used for upgrades
- `versionName`: user-facing version label

Increment `versionCode` for every APK you plan to install on a device. Keep `versionName` readable, such as `0.2.0`, while this project is pre-release.

## Widget Quality

Resizing is the main product risk. A widget is not done if it only works at one size.

Before accepting widget UI work:

- Add the widget from the launcher widget picker.
- Test compact, default, wide, square, and large placements.
- Confirm text is not cropped or overlapping.
- Confirm the widget fills its allocated bounds.
- Confirm tap behavior is intentional.

## References

- [Cozyla: Customize Your Home Screen](https://support.cozyla.com/hc/en-us/articles/32079648161691-Customize-Your-Home-Screen)
- [Android Developers: App widgets overview](https://developer.android.com/develop/ui/views/appwidgets/overview)
- [Android Developers: Provide flexible widget layouts](https://developer.android.com/develop/ui/views/appwidgets/layouts)
- [Android Developers: Widget quality](https://developer.android.com/docs/quality-guidelines/widget-quality)

## License

This project is available under the [MIT License](LICENSE).
