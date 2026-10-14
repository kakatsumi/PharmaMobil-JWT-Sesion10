#!/usr/bin/env bash
set -euo pipefail

raiz="$(cd "$(dirname "$0")" && pwd)"
aplicaciones="${HOME}/.local/share/applications"
escritorio="$(xdg-user-dir DESKTOP 2>/dev/null || true)"
if [[ -z "$escritorio" ]]; then escritorio="${HOME}/Escritorio"; fi

mkdir -p "$aplicaciones" "$escritorio"
archivo="${aplicaciones}/pharmamobil-desktop.desktop"
cat > "$archivo" <<EOF
[Desktop Entry]
Type=Application
Name=PharmaMobil
Comment=Vista móvil ligera de PharmaMobil
Exec="${raiz}/ver-movil.sh"
Icon=${raiz}/shared/src/commonMain/composeResources/drawable/pharmamobil_logo.png
Terminal=false
Categories=Utility;
StartupNotify=true
EOF
chmod +x "$archivo"
cp "$archivo" "${escritorio}/PharmaMobil.desktop"
chmod +x "${escritorio}/PharmaMobil.desktop"
gio set "${escritorio}/PharmaMobil.desktop" metadata::trusted true 2>/dev/null || true

if command -v cc >/dev/null 2>&1; then
  cc -std=c11 -O2 -Wall -Wextra "${raiz}/desktopApp/launcher/launcher.c" -o "${raiz}/Abrir PharmaMobil"
  acceso_archivos="$(dirname "$raiz")/Abrir PharmaMobil"
  if [[ ! -e "$acceso_archivos" || -L "$acceso_archivos" ]]; then
    ln -sfn "${raiz}/Abrir PharmaMobil" "$acceso_archivos"
    echo "Ejecutable para Archivos: $acceso_archivos"
  fi
fi

echo "Acceso directo instalado en el escritorio y en el menú de aplicaciones."
