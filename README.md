# Cozyla Widgets

Welcome. This installs a small set of Android home-screen widgets for a Cozyla tablet or any Android launcher that supports standard widgets.

Included widgets:

| Widget | What it does |
| --- | --- |
| Clock | Shows the current time and date. |
| Week Calendar | Shows a Monday-first Week or Workweek calendar from calendars already synced on the tablet. |
| Quote of the Day | Fetches a daily quote with author attribution and keeps a built-in fallback. |
| Chore Wheel | Lets you enter chores and tap the wheel center to launch a casino-style animated picker. |
| Countdown | Shows a configurable countdown timer. |
| Weather | Shows weather, UV strength, tides, and a graphical moon phase. |
| Photo Frame | Shows one selected photo or a slideshow from photos picked through Android/Google Photos. |

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
- It does request Android internet access for the Quote of the Day widget.
- It does not upload calendar events anywhere.
- It does not store your event titles, descriptions, Google account names, or Google login credentials in this repo.

The calendar widget reads calendars that Android has already synced on the tablet. That is the right architecture. Putting Google passwords or OAuth setup into this widget would be worse: it would add more secrets, more security risk, and more things for non-technical users to get wrong.

## What You Need

You need these on your computer:

- Git, to download this repo
- Android Platform Tools, especially `adb`, to install to the tablet
- Android SDK Platform 35 and Build Tools, so the APK can be built
- JDK 17, so the included Gradle wrapper can build the APK
- A USB cable or Wi-Fi debugging enabled on the tablet

Recommended simple path:

1. Install [Git](https://git-scm.com/downloads).
2. Install [Android Studio](https://developer.android.com/studio), open it once, and let it install the Android SDK.
3. Install [Android Platform Tools](https://developer.android.com/tools/releases/platform-tools) if `adb` is not already available in Terminal or PowerShell.
4. Install JDK 17 if your computer does not already have it.

You do not need to use Android Studio to push the widgets to a tablet. You can install the Android command-line SDK instead, but Android Studio is the simpler path for most people.

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
countdown
weather
photos
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

For the countdown widget, a setup screen opens so you can enter the label and minutes.

For the weather widget, a setup screen opens so you can enter a place label, latitude, longitude, and an optional NOAA tide station ID.

For the photo widget, a setup screen opens so you can pick photos. If Google Photos is connected to Android's photo picker, choose photos from that album view.

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

The quote widget fetches from the public ZenQuotes quote-of-the-day API and shows quotes as `quote - author`. If the tablet is offline or the API fails, the widget uses a built-in fallback quote with author attribution.

### Chore Wheel

The chore wheel is a real widget. Tapping the wheel opens a lightweight native Android spin screen with smooth Canvas animation, then writes the picked result back to the widget.

Standard Android widgets cannot reliably run continuous high-frame-rate animation inside the home-screen surface itself. The companion spin screen is intentional: it keeps the widget addable/resizable while using normal Android rendering for the casino-style animation.

### Countdown

The countdown widget is a kitchen timer. Set minutes and seconds directly on the widget, press Start, and it beeps when done.

### Weather

The weather widget uses Open-Meteo for current weather and UV strength. It uses NOAA CO-OPS for tide predictions only when you enter a NOAA tide station ID. Moon phase is calculated on the device and drawn as a moon circle in the widget.

### Photo Frame

The photo widget uses Android's system photo picker instead of storing Google credentials. Pick one photo for a static frame, or multiple photos for a slideshow interval in minutes.

## Privacy And Security

This repo is designed to be safe to share publicly:

- no committed `.env`
- no committed `local.properties`
- no committed APKs
- no committed screenshots
- no committed signing keys
- network permission is used only for the quote API
- vibration permission is used only for the countdown timer completion alert
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
