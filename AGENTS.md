# AGENTS.md

## Project Context

This repo is for native Android app widgets intended to run on a Cozyla Android tablet or another Android launcher that supports standard home-screen widgets.

The correct default assumption is a standard Android App Widget. Cozyla's own support documentation says the standard launcher supports adding and resizing widgets, so do not invent a proprietary path unless device testing proves the installed launcher behaves differently.

The highest-risk product requirement is resizing. Every widget implementation must be designed and tested as a resizable surface first, not as a fixed-size layout that happens to work in one grid slot.

The public user starts from zero. Documentation and setup flows must assume the reader does not know Git, Android Studio, ADB, Gradle, Google Calendar sync, developer options, pairing ports, or widget picker behavior. Explain installation steps plainly without hiding security tradeoffs.

## Engineering Rules

- Challenge weak premises directly. Do not accept a framing just because it was stated confidently.
- Prefer native Android behavior over wrappers for home-screen widget work.
- Keep device-specific assumptions explicit and easy to change.
- Do not commit secrets, local SDK paths, signing keys, generated APKs, IDE metadata, device IPs, MAC addresses, serial numbers, pairing ports, screenshots, or private build identifiers.
- Keep the app small until the widget's real data source, update cadence, and interaction model justify more architecture.
- Add dependencies only when they improve reliability, testing, or maintainability in a concrete way.
- Verify buildability and tests before claiming implementation is done.
- Verify device connectivity with `adb devices -l` before claiming deployability.
- Commit and push completed changes after tests pass, unless the user explicitly says not to.
- Watch for architectural drift. If the implementation starts serving old assumptions rather than the current product goal, stop and restate the correct direction before editing.
- Increment Android `versionCode` for every APK intended for tablet installation. Never decrease or reuse it for normal installs.
- Keep user-facing `versionName` readable and semver-like while the product is pre-release.
- Keep README installation instructions usable by a non-technical tablet owner. Do not document only the developer shortcut path.

## Public Repository Security Rules

This is a public repository. Security and privacy checks are release blockers, not optional cleanup.

- Treat every tracked file, commit, branch, tag, issue, test fixture, screenshot, log, and CI artifact as public.
- Never read local secrets or ignored files and copy their values into source, tests, documentation, tool output, commits, or PR text.
- Never commit personal calendar data, account names, email addresses, local usernames or paths, hostnames, LAN addresses, ADB identifiers, pairing codes, tokens, credentials, or signing material.
- Use synthetic names and `example.invalid` values in tests and documentation. Do not turn real device data into fixtures.
- Do not add network permissions, analytics, telemetry, crash reporting, remote APIs, WebViews, or external data transmission without an explicit requirement, threat model, privacy review, and documentation update.
- Keep Android components non-exported unless platform behavior requires external access. Protect exported services with the narrowest platform permission and use explicit immutable `PendingIntent` objects.
- Keep calendar access read-only. Do not persist event titles, descriptions, account names, or authentication material. App-private widget preferences must remain excluded from backup.
- Prefer Android-owned account sync for Google Calendar. Do not add direct Google credential collection, OAuth client secrets, or cloud API setup unless a documented product requirement beats the added security risk.
- Pin downloaded build tooling and CI actions to checksums or immutable commit SHAs. Review dependency changes and avoid adding libraries for trivial functionality.
- Run `scripts/check-repo-privacy.sh` and `scripts/check-repo-privacy.sh --history` before every public push. A failure blocks publication.
- Run unit tests, lint, and assembly after security-sensitive manifest, permission, storage, intent, dependency, or build changes.
- Use a GitHub noreply email for commits. Never rewrite or force-push shared public history without explicit approval.
- After the one-time sanitized bootstrap, publish changes on a `codex/` or contributor branch through a pull request. Do not push directly to protected `main`.
- Report suspected vulnerabilities privately according to `SECURITY.md`; do not expose exploit details in a public issue.

## Widget Design Rules

- Treat Android's widget quality checklist as the baseline bar: fill the launcher bounds, keep content uncropped, use sufficient contrast, provide accurate picker previews, and keep content timely.
- Support resizing deliberately. Provider metadata must include `resizeMode`, `targetCellWidth`, `targetCellHeight`, `minWidth`, `minHeight`, and appropriate resize constraints.
- Prefer a small set of responsive layouts over runtime size guessing. For Android 12 and later, use responsive or exact layout handling when the content needs materially different arrangements across sizes.
- Test at least these classes of sizes before accepting UI work: compact/small, default 4x2, wide 4x1-style, square 2x2-style, and large tablet placement.
- A rectangular widget must visually fill its grid bounds. Do not create a centered card floating inside unused launcher space.
- Minimum interactive targets must remain at least 48dp by 48dp.
- Widget text must not crop, overlap, or depend on a single Cozyla orientation. Use `maxLines`, ellipsizing, size-specific content reduction, or alternate layouts.
- Custom Canvas, bitmap, or generated-image widget surfaces must have deterministic visual-fit tests for representative worst cases before device install. At minimum, test long labels, emoji, maximum slot/event counts, optional/special slots, and compact/default/large render sizes. The tests must assert measured text or drawn bounds stay inside their intended cells/wedges, not merely that a bitmap is non-empty.
- For every screenshot-reported UI defect, inspect the screenshot, identify the exact geometry/state mismatch, and add a regression test that fails for the bad case before calling the fix done.
- After installing widget UI changes on a physical device, verify the installed `versionName`/`versionCode`, refresh or recreate any existing widget instance as needed, and capture or inspect the actual rendered widget state. Do not infer visual correctness from a successful APK install.
- Every new or changed widget must have its primary end-user workflow exercised before handoff. If the widget is a timer, start/pause/reset and completion behavior must be tested. If it is a picker-backed widget, empty, selected, and scheduled-update states must be tested. Do not count configuration-screen success as widget success.
- Any widget advertised as an in-widget tool must not open an Activity for its primary action. Use `RemoteViews` controls, broadcast actions, `Chronometer`, `AlarmManager`, or a deliberate companion Activity only when the product explicitly requires a full-screen experience.
- For resize QC, test each widget at minimum, target/default, wide, and tall/large sizes. Bitmap-rendered widgets need non-empty pixel checks at each size. XML-only widgets need inflation/layout contract checks plus physical-device screenshots whenever a launcher instance is available.
- Use `previewLayout` for Android 12+ and add a backward-compatible preview image before caring about older Android widget pickers.
- Avoid expensive work in `AppWidgetProvider` callbacks. If update work can take seconds or involve I/O, schedule it through a background worker and update the widget from there.
- Do not assume `updatePeriodMillis` is precise or sufficient for fresh data. Add explicit refresh behavior when users expect fresher content than the platform schedule can guarantee.
- Every widget must recover from app replacement and tablet reboot. Add or update a manifest-tested refresh path for `MY_PACKAGE_REPLACED` and `BOOT_COMPLETED` whenever a widget can display stale, loading, scheduled, or bitmap-rendered content.

## Android Defaults

- Package namespace: `com.cozyla.widgets`
- Minimum SDK: 23 unless the actual Cozyla tablet requires otherwise.
- Compile/target SDK: use the latest installed stable SDK in this workspace.
- Prefer Android framework `RemoteViews` for the baseline widget. Consider Jetpack Glance only if its testing and layout benefits outweigh the additional dependency and abstraction cost.
- Use JVM tests for metadata and pure logic. Use Robolectric or device/emulator tests when Android resource inflation or widget behavior matters.
- Structure the app as a catalog of widgets. Each widget gets its own provider class, layout XML, appwidget-provider XML, tests, and README entry.
- Do not make a generic widget provider responsible for multiple unrelated widgets unless shared code is factored behind explicit widget-specific providers.

## Required Verification

Run these before saying a change is complete:

```sh
./gradlew testDebugUnitTest
./gradlew lintDebug
./gradlew assembleDebug
```

For deployability, also run:

```sh
adb devices -l
```

For Canvas/bitmap-rendered widgets, also run or add a render-specific regression test that covers the reported layout case. If a physical device is available, install the APK and verify the actual widget surface after refresh/reconfigure; otherwise state that visual device verification is blocked.

For local install after ADB authorization, prefer:

```sh
scripts/push-widget.sh clock
```

## Device Notes

- Store local device details in `.env` or `docs/local/`; both are gitignored.
- ADB over TCP is not implied by developer mode. Developer mode is not the same as USB debugging or wireless debugging.
- To deploy, the tablet must appear in `adb devices -l` as `device`.
- Cozyla home-screen customization docs describe adding widgets from the standard launcher and resizing them with edge handles. Verify this on the physical device before building workaround architecture.

## Research References

- Android Developers: App widgets overview
- Android Developers: Provide flexible widget layouts
- Android Developers: Widget quality
- Android Developers: Add previews to your widget picker
- Android Developers: Create a simple widget
- Cozyla Support: Customize Your Home Screen
