# Widget Catalog

This project is a catalog of Android widgets. Each widget should be independently understandable, testable, and installable through the shared APK.

## Current Widgets

| ID | Name | Provider | Layout | Status |
| --- | --- | --- | --- | --- |
| `clock` | Clock | `com.cozyla.widgets.clock.ClockWidgetProvider` | `widget_clock.xml` | MVP |
| `calendar` | Week Calendar | `com.cozyla.widgets.calendar.CalendarWidgetProvider` | `widget_calendar.xml` | Device verified |
| `quote` | Quote of the Day | `com.cozyla.widgets.quote.QuoteWidgetProvider` | `widget_quote.xml` | MVP |
| `chores` | Chore Wheel | `com.cozyla.widgets.chores.ChoreWheelProvider` | `widget_chore_wheel.xml` | MVP |
| `countdown` | Countdown | `com.cozyla.widgets.countdown.CountdownWidgetProvider` | `widget_countdown.xml` | MVP |
| `weather` | Weather | `com.cozyla.widgets.weather.WeatherWidgetProvider` | `widget_weather.xml` | MVP |
| `photos` | Photo Frame | `com.cozyla.widgets.photos.PhotoFrameWidgetProvider` | `widget_photo_frame.xml` | MVP |

## Week Calendar

- Monday-first Week and Monday-Friday Workweek modes
- per-widget selection across all calendars synced through Android
- compact, wide-short, square, default, and large-tablet size policies
- configurable whole-hour timeline on large placements, defaulting to 6:00 AM-midnight
- shorter timeline ranges enlarge each hour and event while preserving overlap lanes and compact all-day strips
- previous, today, next, refresh, open-calendar, and reconfigure actions
- `CalendarContract.Instances` queries performed off the main thread through `JobScheduler`

Direct Google OAuth is intentionally not embedded in the widget. Android owns account authentication and calendar synchronization; the widget receives read-only access after the user grants `READ_CALENDAR` in its configuration Activity.

## Quote of the Day

- fetches quote of the day from ZenQuotes
- falls back to built-in attributed quotes when offline or the API fails
- displayed as `quote - author`
- resizable static widget

## Chore Wheel

- configuration Activity accepts chores and can reserve slot 8 as a green `No chores` result
- chores are stored only in app-private preferences for that widget instance
- wheel tap opens a native spin Activity with smooth Canvas animation and redraws the widget when the result is picked

## Countdown

- in-widget minute/second controls
- launcher Chronometer handles live countdown display
- AlarmManager alarm plays a beep and updates widget state when finished
- no setup Activity is required for normal timer use

## Weather

- configuration Activity accepts place label, latitude, longitude, and optional NOAA tide station ID
- Open-Meteo supplies weather and UV strength without API keys
- NOAA CO-OPS supplies high/low tide prediction cards when a station is configured
- moon phase is calculated locally and drawn as a circle in the widget bitmap
- rendered as a bitmap for better visual control across resized widget surfaces

## Photo Frame

- configuration Activity uses Android's system photo picker
- stores selected photo URIs and slideshow interval only in app-private preferences
- supports static photo or slideshow mode
- tapping the widget opens Google Photos when installed, otherwise photos.google.com
- close-call animation targets land near a segment boundary about half the time
- result text is computed from the final wheel rotation under the pointer, and the green slot is explicit metadata rather than a label-name guess
- full-screen spin view includes `Spin again` and `Done` controls after each completed spin
- wheel labels are placed from the same segment-center angle used to paint their colored wedge
- long wheel labels are wrapped/scaled against measured segment width so they stay inside their wedge

## Adding A Widget

For each new widget:

1. Add a provider class under `app/src/main/java/com/cozyla/widgets/<widget-id>/`.
2. Add a layout under `app/src/main/res/layout/widget_<widget-id>.xml`.
3. Add metadata under `app/src/main/res/xml/<widget-id>_widget_info.xml`.
4. Register the provider in `AndroidManifest.xml`.
5. Add tests for metadata and layout contracts.
6. Add the widget to `README.md` and this catalog.
7. Update the picker preview and verify the widget on a physical device.

Keep widget IDs stable because scripts and docs use them.
