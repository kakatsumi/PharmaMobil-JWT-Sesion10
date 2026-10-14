# Sesión 10: autenticación JWT

Se trabajó sobre la rama estudiantil `main` de PharmaMobile; no había rama `develop`. La copia de trabajo conserva su historial en `feature/auth-jwt-alva`.

## Implementado

- Login `POST /api/v1/auth/login`, sesión validada con `GET /api/v1/auth/me` al abrir.
- Cliente Ktor compartido con Bearer, `sendWithoutRequest`, limpieza de caché y cabeceras saneadas en Logging.
- Token cifrado con Android Keystore y guardado en Keychain iOS.
- Comprobación inicial, login, cierre de sesión y navegación por permisos.
- Lectura y creación de productos con categoría mediante los endpoints protegidos mediante el mismo cliente HTTP.

## Alcance previo del proyecto

El proyecto de origen no tenía cliente REST, Koin ni CRUD REST de productos/clientes/ventas. Sus formularios de producto e inventario son simulados y las pantallas de clientes/pedidos siguen siendo marcadores de posición. Por ello, el formulario existente se amplió para enviar POST de productos con `categoriaId` y `estado`; clientes y ventas continúan pendientes.

## Cómo comprobar

1. Arrancar PharmaSoft con Flyway y confirmar `http://localhost:8080/api/health`.
2. Ejecutar la colección Postman adjunta en orden, con `baseUrl=http://localhost:8080`. No usar producción para peticiones POST de prueba.
3. Android: abrir el emulador y ejecutar `./gradlew :androidApp:assembleDebug`. La URL del emulador es `http://10.0.2.2:8080`.
4. iOS: en macOS/Xcode, abrir `iosApp`. El simulador usa `http://localhost:8080`; para un dispositivo físico se requiere una dirección accesible y TLS o una excepción de desarrollo específica.
5. Probar admin, vendedor y almacen; cerrar y reabrir; verificar 401, 403 y cierre de sesión.

Se adjunta una captura real del login Android; no se adjuntan capturas simuladas. Véanse `evidencias/PENDIENTES.md` y `pruebas/RESULTADOS.md`.
