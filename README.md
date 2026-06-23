# Cozyla Widgets

Welcome. This installs a small set of Android home-screen widgets for a Cozyla tablet or any Android launcher that supports standard widgets.

Included widgets:

| Widget | What it does |
| --- | --- |
| Clock | Shows the current time and date. |
| Week Calendar | Shows a Monday-first Week or Workweek calendar from calendars already synced on the tablet. |
| Quote of the Day | Shows one local quote each day. |
| Chore Wheel | Lets you enter chores and launch a casino-style animated wheel to pick one at random. |

## Read This First

This is community code. It is public so anyone can inspect it, but public does not mean automatically safe. Do your own due diligence before installing anything from the internet.

A practical safety check is to ask an AI tool to inspect the repo before you install it. For example, paste this into ChatGPT, Grok, Claude, or another tool:

```text
Scan this repo for code that could exploit me, steal data, leak credentials, install malware, or do anything unsafe: https://github.com/thewoodenshoe/cozyla-widgets
```

Then read the answer and decide for yourself. This project is built by the community for the community, but you are still responsible for what you install.

## What This App Does Not Do

- It does not ask for your Google password.
- It does not use a Google Cloud API key.
- It does not use OAuth client secrets.
- It does not request Android internet access.
- It does not upload calendar events anywhere.
- It does not store your event titles, descriptions, Google account names, or Google login credentials in this repo.

The calendar widget reads calendars that Android has already synced on the tablet. That is the right architecture. Putting Google passwords or OAuth setup into this widget would be worse: it would add more secrets, more security risk, and more things for non-technical users to get wrong.

## What You Need

You need these on your computer:

- Git, to download this repo
- Android Studio, which includes the Android SDK
- Android Platform Tools, especially `adb`
- JDK 17, if your Android Studio install does not already provide it
- A USB cable or Wi-Fi debugging enabled on the tablet

Recommended simple path:

1. Install [Git](https://git-scm.com/downloads).
2. Install [Android Studio](https://developer.android.com/studio).
3. Open Android Studio once so it installs the Android SDK.
4. Install Android Platform Tools if `adb` is not already available from your terminal.

## Set Up Google Calendar

Do this on the Cozyla tablet before adding the Week Calendar widget:

1. Open Android Settings.
2. Add your Google account to Android.
3. Turn on Calendar sync for that account.
4. Open Google Calendar on the tablet once and confirm your events appear.
5. Add the Week Calendar widget from the home-screen widget picker.
6. When Android asks, allow Calendar access.
7. Choose Week or Workweek, choose the visible time range, and select the calendars to show.

That is all. There is no calendar credential setup inside this repo.

## Enable Install Over Wi-Fi

On the Cozyla tablet:

1. Open Settings.
2. Open About tablet.
3. Tap Build number several times until Developer options are enabled.
4. Go back to Settings.
5. Open Developer options.
6. Turn on Wireless debugging.
7. Open Wireless debugging.
8. Choose Pair device with pairing code.
9. Write down the IP address, pairing port, and pairing code.
10. After pairing, also write down the debug port shown for wireless debugging.

The pairing port and debug port are different. Do not mix them up.

## Download The Widgets

Open Terminal on Mac or PowerShell on Windows.

```sh
git clone https://github.com/thewoodenshoe/cozyla-widgets.git
cd cozyla-widgets
```

## Pair The Tablet

Use the pairing IP and pairing port shown on the tablet:

```sh
adb pair <tablet-ip>:<pairing-port>
```

Enter the pairing code when asked.

Then connect using the debug port:

```sh
adb connect <tablet-ip>:<debug-port>
adb devices -l
```

You are ready when `adb devices -l` shows the tablet as `device`.

If it says `unauthorized`, look at the tablet and approve the debugging prompt. If it says `offline`, turn Wireless debugging off and on, then reconnect.

## Install From Mac Or Linux

Install all widgets:

```sh
./gradlew testDebugUnitTest lintDebug assembleDebug
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

Or use the helper script, which runs checks and installs:

```sh
scripts/push-widget.sh calendar
```

You can replace `calendar` with:

```text
clock
calendar
quote
chores
```

## Install From Windows

In PowerShell:

```powershell
.\gradlew.bat testDebugUnitTest lintDebug assembleDebug
adb install -r app\build\outputs\apk\debug\app-debug.apk
```

If you use Git Bash on Windows, the Mac/Linux commands usually work too.

## Add The Widget On Cozyla

After installing:

1. Go to the Cozyla home screen.
2. Long-press an empty area.
3. Choose Widgets.
4. Find Cozyla Widgets.
5. Drag the widget onto the home screen.
6. Resize it with the home-screen handles.

For the calendar widget, a setup screen opens so you can pick calendars and time range.

For the chore wheel, a setup screen opens so you can enter chores and optionally make slot 8 a green `No chores` slot.

## Optional Local Wi-Fi Config

You can create a private `.env` file so the helper script remembers your tablet target:

```sh
cp .env.example .env
```

Then edit `.env`:

```sh
COZYLA_DEVICE_IP=<tablet-ip>
COZYLA_ADB_TARGET=<tablet-ip>:<debug-port>
```

`.env` is ignored by git. Do not commit real IP addresses, ports, serial numbers, screenshots, or pairing codes.

## Widget Notes

### Week Calendar

- Week view starts on Monday.
- Workweek view shows Monday through Friday only.
- Large layouts show a timeline with hours on the left.
- All-day events stay in a compact strip above the timed grid.
- The visible time range is configurable, for example 8:00 AM to 10:00 PM.
- Each widget instance can use different calendars and settings.

The app requests only `READ_CALENDAR`. It reads Android's local calendar provider. Android and Google Calendar handle account login and sync.

### Quote of the Day

The quote list is built into the app. It does not download quotes from the internet.

### Chore Wheel

The chore wheel is a real widget. Tapping the wheel or Spin button opens a lightweight native Android spin screen with smooth Canvas animation, then writes the picked result back to the widget.

Standard Android widgets cannot reliably run continuous high-frame-rate animation inside the home-screen surface itself. The companion spin screen is intentional: it keeps the widget addable/resizable while using normal Android rendering for the casino-style animation.

## Privacy And Security

This repo is designed to be safe to share publicly:

- no committed `.env`
- no committed `local.properties`
- no committed APKs
- no committed screenshots
- no committed signing keys
- no network permission
- no telemetry
- no analytics
- no Google credential files

Before publishing changes, run:

```sh
scripts/check-repo-privacy.sh
scripts/check-repo-privacy.sh --history
./gradlew testDebugUnitTest lintDebug assembleDebug
```

If private information ever enters git history, `.gitignore` will not fix it. Rewrite the history before sharing and rotate any exposed secret.

## For Contributors

Each widget should have:

- its own provider class
- its own layout XML
- its own `appwidget-provider` XML
- its own README entry
- tests for metadata and core behavior

Read [AGENTS.md](AGENTS.md), [CONTRIBUTING.md](CONTRIBUTING.md), and [SECURITY.md](SECURITY.md) before contributing.

## License

This project is available under the [MIT License](LICENSE).
