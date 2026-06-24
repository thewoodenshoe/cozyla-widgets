# Cozyla Widgets

Welcome. This project installs custom Android home-screen widgets for a Cozyla tablet.

Cozyla runs Android, similar to a big phone or tablet. Android supports widgets. This repo builds one Android app called **Cozyla Widgets**, and that app adds several new widgets to Cozyla's widget picker.

## Included Widgets

One install adds all of these widgets:

| Widget | Version | What it does | Setup needed |
| --- | --- | --- | --- |
| Clock | 0.12.6 | Shows the current time and date. | None. |
| Week Calendar | 0.12.6 | Shows a Monday-first Week or Workweek calendar from calendars already synced on the tablet. | Add your Google account to Android and allow calendar access. |
| Quote of the Day | 0.12.6 | Fetches a daily quote with author attribution and keeps a built-in fallback quote. | Internet helps, but it still shows a built-in quote if offline. |
| Chore Wheel | 0.12.6 | Lets you enter chores and tap the wheel center to launch a casino-style animated picker. | Enter 2 to 8 chores. Slot 8 can optionally be green `No chores`. |
| Countdown | 0.12.6 | Kitchen timer with minutes, seconds, Start, Pause, Reset, live second display, and a done beep. | None. |
| Weather | 0.12.6 | Shows weather, UV strength, tides, and a graphical moon phase. | Search for a city. Default is Charleston, SC. Add a NOAA tide station only if you want tide cards. |
| Photo Frame | 0.12.6 | Shows one selected photo or a slideshow from photos picked on the tablet. | Pick photos through Android's photo picker or file browser. |

Open the **Cozyla Widgets** app on the tablet to see the installed app version. The version above should match that screen after you install.

## Index

1. [Read This First](#read-this-first)
2. [Getting Started](#getting-started)
3. [One-Time Computer Setup](#one-time-computer-setup)
4. [One-Time Cozyla Setup](#one-time-cozyla-setup)
5. [Download This Repo](#download-this-repo)
6. [Connect To Cozyla](#connect-to-cozyla)
7. [Install The Widgets](#install-the-widgets)
8. [Add A Widget On Cozyla](#add-a-widget-on-cozyla)
9. [Widget Setup Notes](#widget-setup-notes)
10. [Privacy](#privacy)
11. [For Contributors](#for-contributors)

## Read This First

This is community code. It is public so anyone can inspect it, but public does not mean automatically safe. Before installing anything from the internet, do your own check.

An easy safety check is to paste this into ChatGPT, Grok, Claude, or another AI tool:

```text
Scan this repo for code that could exploit me, steal data, leak credentials, install malware, or do anything unsafe: https://github.com/thewoodenshoe/cozyla-widgets
```

Then read the answer and decide for yourself.

## Getting Started

The simple idea:

1. Prepare your computer once.
2. Open Cozyla settings once so your computer is allowed to install apps over Wi-Fi.
3. Download this repo.
4. Run one command to build and install the widgets.
5. Add the widgets from Cozyla's normal widget picker.

Do you need Android Studio? Not to use the widgets day to day. But if you are installing from source code, your computer does need the Android SDK so it can build an APK file. Android Studio is the easiest way for most people to install that SDK once.

The helper script builds the app for you. You do not need to understand Android development.

## One-Time Computer Setup

Install these on your computer:

1. [Git](https://git-scm.com/downloads), so you can download this repo.
2. [Android Studio](https://developer.android.com/studio), then open it once so it installs the Android SDK.
3. [Android Platform Tools](https://developer.android.com/tools/releases/platform-tools), especially `adb`, if your terminal does not already know the `adb` command.
4. JDK 17, if your Android Studio setup did not already include Java.

This is a one-time setup. Once it works, installing updates is much faster.

## One-Time Cozyla Setup

On the Cozyla tablet:

1. Open **Settings**.
2. Open **About tablet**.
3. Tap **Build number** several times until Developer options are enabled.
4. Go back to **Settings**.
5. Open **Developer options**.
6. Turn on **Wireless debugging**.
7. Open **Wireless debugging**.
8. Choose **Pair device with pairing code**.
9. Write down the IP address, pairing port, and pairing code.
10. After pairing, also write down the wireless debugging port.

The pairing port and wireless debugging port are different. That part matters.

## Download This Repo

Open Terminal on Mac or PowerShell on Windows:

```sh
git clone https://github.com/thewoodenshoe/cozyla-widgets.git
cd cozyla-widgets
```

## Connect To Cozyla

Use the pairing IP and pairing port shown on Cozyla:

```sh
adb pair <tablet-ip>:<pairing-port>
```

Enter the pairing code when asked.

Then connect using the wireless debugging port:

```sh
adb connect <tablet-ip>:<debug-port>
adb devices -l
```

You are ready when `adb devices -l` shows the tablet as `device`.

If it says `unauthorized`, look at Cozyla and approve the debugging prompt. If it says `offline`, turn Wireless debugging off and on, then reconnect.

## Install The Widgets

Mac or Linux:

```sh
scripts/push-widget.sh clock
```

Windows PowerShell:

```powershell
.\gradlew.bat testDebugUnitTest lintDebug assembleDebug
adb install -r app\build\outputs\apk\debug\app-debug.apk
```

The helper script installs the whole **Cozyla Widgets** app, briefly opens it once so existing widgets refresh, then returns to the home screen. You do not install each widget separately. The word after the script is only a friendly label for the install command, so any of these work:

```text
clock
calendar
quote
chores
countdown
weather
photos
```

## Add A Widget On Cozyla

After installing:

1. Go to the Cozyla home screen.
2. Long-press an empty area.
3. Choose **Widgets**.
4. Find **Cozyla Widgets**.
5. Drag a widget onto the home screen.
6. Resize it with the home-screen handles.

## Widget Setup Notes

### Clock

No setup. Add it from the widget picker and resize it.

### Week Calendar

Before using the calendar widget, add your Google account to Android on the Cozyla tablet and make sure Calendar sync is on. The widget reads Android's local calendar. It does not ask for your Google password.

When adding or editing the widget, choose Week or Workweek view, choose calendars, and set the visible time range.

### Quote of the Day

No account setup. It fetches a quote from the internet when available and uses a built-in fallback quote when offline.

### Chore Wheel

When you add it, enter chores and optionally make slot 8 a green `No chores` slot.

### Countdown

Set minutes and seconds directly on the widget. Press **Start**. It beeps and vibrates when done.

### Weather

Search for a city. The default is Charleston, SC. Weather and UV use Open-Meteo. Tides use NOAA only when a station is configured.

If you want tides, enter a NOAA tide station ID for your area. If you do not know the station ID, leave it alone and the widget will still show weather, UV, and moon phase.

### Photo Frame

Pick one photo for a still frame, or multiple photos for a slideshow.

Important: this widget does not sign in to Google Photos directly and does not store Google credentials. It asks Android to pick photos. If this Cozyla tablet exposes Google Photos in Android's picker, choose photos there. If it does not, use **Browse files** or open Google Photos on the tablet first so the photos are available locally.

## Privacy

This repo is designed to be safe to share publicly:

- no committed `.env`
- no committed `local.properties`
- no committed APKs
- no committed screenshots
- no committed signing keys
- no telemetry
- no analytics
- no Google credential files

Permissions used by the app:

- `READ_CALENDAR`: calendar widget
- `INTERNET`: quote and weather widgets
- `ACCESS_NETWORK_STATE`: lets quote and weather refresh jobs wait for network
- `VIBRATE`: countdown timer completion alert
- `RECEIVE_BOOT_COMPLETED`: refreshes widgets after Cozyla restarts

Private local setup belongs in `.env`, which is ignored by git:

```sh
cp .env.example .env
```

Then edit `.env`:

```sh
COZYLA_DEVICE_IP=<tablet-ip>
COZYLA_ADB_TARGET=<tablet-ip>:<debug-port>
```

Never commit real IP addresses, ports, serial numbers, screenshots, pairing codes, or credentials.

## For Contributors

Most people can ignore this section.

Before publishing changes, run:

```sh
scripts/check-repo-privacy.sh
scripts/check-repo-privacy.sh --history
./gradlew testDebugUnitTest lintDebug assembleDebug
```

Read [AGENTS.md](AGENTS.md), [CONTRIBUTING.md](CONTRIBUTING.md), and [SECURITY.md](SECURITY.md) before contributing.

## License

This project is available under the [MIT License](LICENSE).
