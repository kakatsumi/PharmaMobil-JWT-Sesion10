#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")"

if ! command -v java >/dev/null 2>&1; then
  echo "Se necesita Java 17 o superior. Instala OpenJDK y vuelve a intentar." >&2
  exit 1
fi

version_java="$(java -version 2>&1 | head -n 1 | cut -d '"' -f 2)"
mayor_java="${version_java%%.*}"
if [[ "$mayor_java" == "1" ]]; then mayor_java="$(printf '%s' "$version_java" | cut -d . -f 2)"; fi
if [[ ! "$mayor_java" =~ ^[0-9]+$ ]] || ((mayor_java < 17)); then
  echo "Se necesita Java 17 o superior (encontrado: $version_java)." >&2
  exit 1
fi

if [[ "$(uname -s)" == Linux && "$(uname -m)" == x86_64 && "${PHARMAMOBIL_BUILD_FROM_SOURCE:-0}" != 1 ]]; then
  jar="dist/PharmaMobil-desktop-linux-x64.jar"
  suma="14d425f4c78997ac09d4dc0f14a0e9196c6098ee2387285dc0b5eefdf85a5bac"
  if [[ ! -f "$jar" ]] || ! printf '%s  %s\n' "$suma" "$jar" | sha256sum --check --status; then
    if ! command -v curl >/dev/null 2>&1; then
      echo "Instala curl para descargar la versión de escritorio, o descarga el JAR desde GitHub Releases a dist/." >&2
      exit 1
    fi
    mkdir -p dist
    temporal="$(mktemp dist/.pharmamobil.XXXXXX)"
    trap 'rm -f "$temporal"' EXIT
    url="https://github.com/kakatsumi/PharmaMobil-JWT-Sesion10/releases/download/v0.2.0-desktop/PharmaMobil-desktop-linux-x64.jar"
    echo "Descargando PharmaMobil Desktop (solo la primera vez)..."
    curl --fail --location --silent --show-error "$url" --output "$temporal"
    printf '%s  %s\n' "$suma" "$temporal" | sha256sum --check --status || {
      echo "La descarga no pasó la verificación SHA-256." >&2
      exit 1
    }
    mv "$temporal" "$jar"
    trap - EXIT
  fi
  exec java -Xmx512m -jar "$jar" "$@"
fi

# Development fallback; only needed on other systems or when rebuilding source.
argumentos=()
if (($#)); then
  argumentos=("--args=$*")
fi
exec ./gradlew --no-daemon --max-workers=1 -Dorg.gradle.jvmargs="-Xmx1536m -Dfile.encoding=UTF-8" \
  -Pkotlin.daemon.jvmargs="-Xmx768m" :desktopApp:run "${argumentos[@]}"
