#!/usr/bin/env bash
set -euo pipefail
RAIZ="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$RAIZ"
command -v java >/dev/null || { echo "Falta Java 17 o superior." >&2; exit 1; }
echo "Abriendo Compose Desktop con PharmaSoft real en http://localhost:8080"
exec ./gradlew --max-workers=1 :desktopApp:run --args="--backend=http://localhost:8080"
