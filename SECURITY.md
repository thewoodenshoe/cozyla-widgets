# Security Policy

## Reporting A Vulnerability

Do not open a public issue for a suspected vulnerability or privacy exposure. Use GitHub's private vulnerability reporting for this repository from the Security tab.

Include the affected version or commit, reproduction steps using synthetic data, impact, and any suggested mitigation. Do not include real calendar content, account details, device identifiers, credentials, or signing keys in the report.

## Security Model

- The app reads calendars through Android's local `CalendarContract` provider after the user grants `READ_CALENDAR`.
- The app has no `INTERNET` permission and does not implement Google OAuth, analytics, telemetry, or remote APIs.
- Google accounts and credentials remain managed by Android. The app never receives or stores Google passwords or OAuth tokens.
- Event data is used in memory to render widgets. Only selected local calendar IDs and widget display preferences are stored in app-private preferences, which are excluded from backup.
- Local ADB addresses and ports belong in the ignored `.env` file. Pairing codes are entered directly into `adb pair` and must not be stored.

Only the latest commit on `main` is supported. Security fixes may be released without preserving behavior that creates unnecessary exposure.
