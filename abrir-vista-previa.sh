#!/usr/bin/env bash
set -euo pipefail

RAIZ="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PID_ARCHIVO="$RAIZ/.vista-previa.pid"
LOG_ARCHIVO="$RAIZ/.vista-previa.log"
URL="http://127.0.0.1:5173/"

if [[ "${1:-}" == "--stop" ]]; then
  if [[ -f "$PID_ARCHIVO" ]]; then
    PID="$(cat "$PID_ARCHIVO")"
    if [[ -r "/proc/$PID/cmdline" ]] && tr '\0' ' ' < "/proc/$PID/cmdline" | grep -q 'http.server 5173'; then
      kill "$PID"
      echo "Vista previa detenida."
    fi
    rm -f "$PID_ARCHIVO"
  else
    echo "No hay servidor iniciado por este archivo."
  fi
  exit 0
fi

command -v python3 >/dev/null || { echo "Falta Python 3." >&2; exit 1; }
comprobar() {
  python3 -c 'import sys, urllib.request; urllib.request.urlopen(sys.argv[1], timeout=2).close()' "$URL" >/dev/null 2>&1
}
puerto_ocupado() {
  python3 -c 'import socket,sys; s=socket.socket(); s.settimeout(1); sys.exit(0 if s.connect_ex(("127.0.0.1", 5173)) == 0 else 1)' >/dev/null 2>&1
}

ACTIVO=false
if [[ -f "$PID_ARCHIVO" ]]; then
  PID="$(cat "$PID_ARCHIVO")"
  if [[ -r "/proc/$PID/cmdline" ]] && tr '\0' ' ' < "/proc/$PID/cmdline" | grep -q 'http.server 5173'; then
    ACTIVO=true
  else
    rm -f "$PID_ARCHIVO"
  fi
fi

if [[ "$ACTIVO" == false ]]; then
  if puerto_ocupado; then
    echo "El puerto 5173 ya está ocupado por otro servidor." >&2
    exit 1
  fi
  nohup python3 -m http.server 5173 --bind 127.0.0.1 --directory "$RAIZ/vista-previa" >"$LOG_ARCHIVO" 2>&1 </dev/null &
  echo "$!" > "$PID_ARCHIVO"
  for _ in {1..20}; do
    if comprobar; then break; fi
    sleep 0.2
  done
  if ! comprobar; then
    echo "No se pudo iniciar la vista previa; revisa $LOG_ARCHIVO" >&2
    exit 1
  fi
fi

echo "Vista previa: $URL"
echo "Para cerrarla: ./abrir-vista-previa.sh --stop"
echo "PharmaSoft debe estar en http://localhost:8080 para las operaciones REST."
if [[ "${1:-}" != "--no-browser" ]]; then
  if command -v firefox >/dev/null; then
    firefox "$URL" >/dev/null 2>&1 &
  else
    echo "Firefox no está disponible; abre la dirección anterior en un navegador."
  fi
fi
