# AI Contributor Instructions

Read and follow `/AGENTS.md` before changing this repository. Its Public Repository Security Rules are mandatory.

- Assume all generated code, tests, documentation, commit messages, and workflow output will be public.
- Use only synthetic fixture data. Never copy values from a connected Android device, calendar provider, `.env`, `local.properties`, ignored files, terminal history, or screenshots.
- Preserve the app's local-only privacy model: `READ_CALENDAR` is the only runtime data permission and the app must not gain network access, telemetry, analytics, or remote credential handling without explicit approval and a documented threat model.
- Minimize exported Android components, keep `PendingIntent` objects explicit and immutable, and keep app-private preferences out of backup.
- Pin build downloads and GitHub Actions to immutable versions. Do not add dependencies without a concrete reliability or security benefit.
- Before proposing a public push, run:

```sh
scripts/check-repo-privacy.sh
scripts/check-repo-privacy.sh --history
./gradlew testDebugUnitTest lintDebug assembleDebug
```

Stop publication when any check fails. Follow `SECURITY.md` for vulnerability reports.
