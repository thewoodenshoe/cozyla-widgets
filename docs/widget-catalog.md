# Widget Catalog

This project is a catalog of Android widgets. Each widget should be independently understandable, testable, and installable through the shared APK.

## Current Widgets

| ID | Name | Provider | Layout | Status |
| --- | --- | --- | --- | --- |
| `clock` | Clock | `com.cozyla.widgets.clock.ClockWidgetProvider` | `widget_clock.xml` | MVP |
| `calendar` | Week Calendar | `com.cozyla.widgets.calendar.CalendarWidgetProvider` | `widget_calendar.xml` | Device verified |

## Week Calendar

- Monday-first Week and Monday-Friday Workweek modes
- per-widget selection across all calendars synced through Android
- compact, wide-short, square, default, and large-tablet size policies
- configurable whole-hour timeline on large placements, defaulting to 6:00 AM-midnight
- shorter timeline ranges enlarge each hour and event while preserving overlap lanes and compact all-day strips
- previous, today, next, refresh, open-calendar, and reconfigure actions
- `CalendarContract.Instances` queries performed off the main thread through `JobScheduler`

Direct Google OAuth is intentionally not embedded in the widget. Android owns account authentication and calendar synchronization; the widget receives read-only access after the user grants `READ_CALENDAR` in its configuration Activity.

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
