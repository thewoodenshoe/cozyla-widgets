# Android Widget Best Practices

These rules are based on current Android developer guidance and Cozyla's own launcher documentation. They are project requirements, not general advice.

## Product Assumptions

- Cozyla's standard launcher supports adding widgets and resizing them with edge handles.
- The baseline architecture is a standard Android App Widget using `RemoteViews`.
- Calendar widgets use Android's synced `CalendarContract` provider by default. Direct provider OAuth needs a concrete requirement that Android account sync cannot satisfy.

## Design Requirements

- Resizing is a release blocker. Every widget UI must be evaluated at compact, default 4x2, wide, square, and large tablet sizes.
- Rectangular widgets must fill their allocated launcher bounds. A smaller decorative card inside a larger widget is not acceptable.
- Text must have bounded lines, intentional truncation, or size-specific alternatives.
- Interactive controls must preserve 48dp by 48dp minimum touch targets.
- Empty/loading/error states must be explicit. A stale calendar or blank widget is a product failure, not a cosmetic issue.
- Use sufficient color contrast in every theme state we support.
- The widget picker preview must accurately represent the widget. Declare both `previewLayout` and `previewImage`; Cozyla's launcher currently uses the legacy image even on Android 14.

## Engineering Requirements

- Keep widget provider metadata explicit: `minWidth`, `minHeight`, `minResizeWidth`, `minResizeHeight`, `targetCellWidth`, `targetCellHeight`, `resizeMode`, `description`, `initialLayout`, `previewLayout`, and `previewImage`.
- Prefer a small set of responsive layouts or exact layout handling over ad hoc size guessing.
- Do not perform network or calendar sync inside `AppWidgetProvider` callbacks. Schedule background work, persist the result, then update `RemoteViews`.
- Query `CalendarContract.Instances` in a background job because recurring-event expansion is blocking. On Cozyla, prefer a permanently declared `JobService`; WorkManager component toggles can cause this launcher to rebroadcast widget updates continuously.
- Treat `updatePeriodMillis` as a coarse fallback, not a precise schedule.
- Store calendar/auth/device secrets outside git.
- Add or update tests for provider metadata and pure formatting/state logic when widget behavior changes.

## Verification

Run local verification for every completed change:

```sh
./gradlew testDebugUnitTest
./gradlew lintDebug
./gradlew assembleDebug
```

Run device verification once the Cozyla tablet is visible to ADB:

```sh
adb devices -l
./gradlew installDebug
```

Manual Cozyla checks:

- add from the widget picker
- resize horizontally and vertically
- check compact, wide, square, default, and large placements
- rotate if available
- confirm text remains readable and uncropped
- confirm the widget fills the grid bounds
- confirm tap/refresh behavior

## Sources

- https://support.cozyla.com/hc/en-us/articles/32079648161691-Customize-Your-Home-Screen
- https://developer.android.com/develop/ui/views/appwidgets/overview
- https://developer.android.com/develop/ui/views/appwidgets
- https://developer.android.com/develop/ui/views/appwidgets/layouts
- https://developer.android.com/docs/quality-guidelines/widget-quality
- https://developer.android.com/develop/ui/views/appwidgets/previews
