# Contributing

Read `AGENTS.md` before changing code. The repository is public and its security rules apply to human and AI-generated contributions.

1. Configure Git with a GitHub noreply email.
2. Create a branch instead of committing directly to `main`.
3. Use synthetic test data and keep all local device details in ignored files.
4. Run the required checks:

```sh
scripts/check-repo-privacy.sh
scripts/check-repo-privacy.sh --history
./gradlew testDebugUnitTest lintDebug assembleDebug
```

5. Open a pull request and describe permission, exported-component, storage, dependency, or privacy effects.

Do not report vulnerabilities in a public issue. Follow `SECURITY.md`.
