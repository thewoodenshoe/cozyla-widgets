#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
cd "$ROOT_DIR"

SENSITIVE_PATTERN='(/Users/[^/[:space:]]+|/home/[^/[:space:]]+|192\.168\.[0-9]{1,3}\.[0-9]{1,3}|10\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}|172\.(1[6-9]|2[0-9]|3[01])\.[0-9]{1,3}\.[0-9]{1,3}|fe80:[0-9A-F:]+|([0-9A-F]{2}:){5}[0-9A-F]{2}|adb-[A-Z0-9_-]+\._adb-tls|[A-Z0-9._%+-]+@[A-Z0-9.-]+\.[A-Z]{2,}|BEGIN (RSA |OPENSSH |EC )?PRIVATE KEY|AIza[0-9A-Za-z_-]{20,}|[0-9]+-[A-Z0-9_-]+\.apps\.googleusercontent\.com|gh[pousr]_[A-Z0-9_]{20,}|github_pat_[A-Z0-9_]{20,}|AKIA[0-9A-Z]{16}|xox[baprs]-[A-Z0-9-]{10,})'
FAILED=0

while IFS= read -r -d '' file; do
  if [[ "$file" == "scripts/check-repo-privacy.sh" || ! -f "$file" ]]; then
    continue
  fi
  if LC_ALL=C grep -IqE "$SENSITIVE_PATTERN" "$file"; then
    echo "Potential private data: $file"
    FAILED=1
  fi
done < <(git ls-files --cached --others --exclude-standard -z)

if [[ "${1:-}" == "--history" ]]; then
  PUBLIC_REVISIONS="$(git rev-list --branches --tags)"
  if [[ -z "$PUBLIC_REVISIONS" ]]; then
    PUBLIC_REVISIONS="$(git rev-parse HEAD)"
  fi

  while IFS= read -r match; do
    [[ -z "$match" ]] && continue
    echo "Potential private data in history: $match"
    FAILED=1
  done < <(
    git grep -Il -E "$SENSITIVE_PATTERN" $PUBLIC_REVISIONS -- \
      ':!scripts/check-repo-privacy.sh' 2>/dev/null || true
  )

  while IFS=$'\t' read -r commit email; do
    if [[ ! "$email" =~ @users\.noreply\.github\.com$ \
      && ! "$email" =~ @example\.invalid$ ]]; then
      echo "Commit exposes a non-noreply email: $commit"
      FAILED=1
    fi
  done < <(git log --branches --tags --format='%H%x09%ae')
fi

if (( FAILED != 0 )); then
  echo "Privacy check failed. Review the named files or commits without publishing them."
  exit 1
fi

echo "Privacy check passed."
