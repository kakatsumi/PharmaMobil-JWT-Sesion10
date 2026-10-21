# Registro de evidencias reales

**Alumno:** Ricardo Alva Pardave · 21 de octubre de 2026

Este archivo registra solo comprobaciones ejecutadas. La salida de Gradle y los XML JUnit permanecen en `shared/build/` mientras exista el directorio de compilación local; la APK queda en `androidApp/build/outputs/apk/debug/`.

| Comprobación | Resultado |
|---|---|
| Código fuente SQLDelight | `PharmaDb` generado con las tablas `producto` y `categoria`. |
| Pruebas de repositorio offline | 6 pruebas JVM, sin fallos: altas sin red, orden y reconciliación, Flow, conservación ante fallo, 403 y reapertura de SQLite. |
| Suite JVM completa | `:shared:desktopTest`: 36 pruebas, 0 fallos, 0 errores, 0 omitidas (21-10-2026). |
| Interfaz Compose Desktop | `:desktopApp:test`: 1 prueba, 0 fallos. Registró un producto en la demo local y comprobó desplazamiento, catálogo y sincronización. Capturas reales: `formulario-desktop-demo.png` y `catalogo-desktop-demo.png`; corresponden al servidor simulado del test. |
| Compilación Android | `:androidApp:assembleDebug` finalizó con `BUILD SUCCESSFUL` el 21-10-2026. APK de depuración generada y copiada a `entregables/android/PharmaMobil-debug.apk`. |
| Servidor de vista previa | Se inició en `127.0.0.1:5173`; `index.html` y `recursos/app.js` respondieron HTTP 200; se detuvo después. |
| Login y CRUD en PharmaSoft vivo | Pendiente; backend no estuvo disponible en esta ejecución. |
| Modo avión Android y archivo `pharma.db` en dispositivo | Pendiente; no hay emulador o teléfono conectado en esta ejecución. |
| Producto positivo en Swagger | Pendiente; exige backend y dispositivo o cliente de prueba conectados. |

No se adjuntan capturas de emulador, SQLite o Swagger sin haberlas obtenido de una ejecución real.

SHA-256 de la APK copiada: `9aebd3314dc44606f3f3ddac786e28c92efc8479d7cc2c381b45b0697a7c230d` (22 931 468 bytes).
