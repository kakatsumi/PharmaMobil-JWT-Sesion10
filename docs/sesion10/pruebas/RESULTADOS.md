# Resultados de pruebas (14 de octubre de 2026)

- `./gradlew :androidApp:assembleDebug :shared:testAndroidHostTest --no-daemon`: **BUILD SUCCESSFUL**. APK de depuración generada en `androidApp/build/outputs/apk/debug/`.
- `testAndroidHostTest`: **27 pruebas, 0 fallos**. Incluye cuatro pruebas de `AuthRepositoryTest`: login y token guardado, login 401, eliminación de token inválido al restaurar, y restauración con cabecera Bearer mediante MockEngine.
- `git diff --check`: sin errores de espacio en blanco.
- Revisión estática de la colección Postman: cuatro grupos, `baseUrl=http://localhost:8080`; la primera carpeta obtiene tokens para las demás. No se ha ejecutado la colección contra un backend vivo. Los POST de la matriz deben usarse solo en una base de laboratorio.
- El backend de referencia exige Oracle local en puerto 1522 y Flyway; no se verificó una instancia activa de Oracle/PharmaSoft. Sin ella no es posible acreditar respuestas 200/401/403 reales ni ejecutar la matriz Postman.
- iOS: pendiente compilación y ejecución en macOS/Xcode.
- Emulador Android `Medium_Phone` (API 34): APK instalada y `MainActivity` abierta. UI Automator confirmó “PharmaMobil”, “Usuario”, “Contraseña” e “Iniciar sesión”. Captura real en `../evidencias/11-android-login.png`. No se probó login contra PharmaSoft porque el backend y Oracle no estaban ejecutándose.
