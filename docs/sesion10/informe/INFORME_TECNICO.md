# Informe técnico: Autenticación JWT y almacenamiento seguro

**Universidad:** Universidad Peruana Unión  
**Facultad:** Ingeniería y Arquitectura  
**Carrera:** Ingeniería de Sistemas  
**Curso:** Desarrollo de Aplicaciones Móviles, semestre 2026-2  
**Sesión:** 10, Unidad 2  
**Fecha:** 14 de octubre de 2026

## 1. Introducción y objetivos

Integrar JWT de PharmaSoft en PharmaMobil, conservarlo en el almacenamiento seguro de cada plataforma y proteger las consultas REST. La práctica exige distinguir autenticación (401) de autorización (403).

## 2. Tecnologías y arquitectura

Kotlin Multiplatform 2.4.10, Compose Multiplatform 1.11.1, Gradle 9.7.1, Ktor Client, Koin y Kotlin Serialization. `commonMain` contiene UI, dominio, casos de uso, repositorio y cliente Ktor. `androidMain` aporta Android Keystore; `iosMain` aporta Keychain. La secuencia es UI → SesionViewModel → UseCase → AuthRepository → Ktor → PharmaSoft; el repositorio usa AlmacenSeguro para persistir el token.

## 3. Contrato y análisis JWT

La guía y la colección indican `POST /api/v1/auth/login` con `username/password`, respuesta con `token`, `tipo`, `expiraEnSegundos`, `username`, `nombreCompleto`, `roles` y `permisos`; `GET /api/v1/auth/me` devuelve `empleadoNombre`, `username` y permisos. El catálogo paginado usa `contenido`. El token dura 3600 segundos y no hay refresh. La colección contiene cuatro grupos: login, validación JWT, permisos y exposición/CORS. El backend no estaba disponible para cotejar respuestas reales en esta entrega.

## 4. Implementación

`Credenciales`, `Sesion`, `ErrorSesion` y `AuthRepository` no dependen de HTTP. Los tres casos de uso exponen login, restauración y cierre. `AuthRepositorioRest` guarda el JWT al recibir login, limpia la caché Bearer, consulta `/auth/me` al restaurar y elimina el token al cerrar sesión. Android usa AES-GCM con llave en Keystore; las preferencias contienen solamente IV y texto cifrado. iOS usa Keychain con servicio/cuenta específicos y comprueba códigos de retorno.

El cliente Ktor comparte ContentNegotiation, DefaultRequest, Auth Bearer y Logging. `loadTokens` lee AlmacenSeguro y `sendWithoutRequest` omite solo `/auth/login`. El nivel de registro es HEADERS en depuración y NONE fuera de ella; Authorization se sanea. No se registra cuerpo de login.

`SesionViewModel` evita logins simultáneos, limpia la clave del estado al finalizar y mantiene la sesión en memoria solo durante la ejecución. `LoginScreen` oculta la clave, valida vacíos y deshabilita el botón al cargar. `App` muestra comprobación, login o navegación protegida. El permiso `CATALOGO_ESCRIBIR` controla el formulario, `CLIENTES_LEER` y `VENTAS_LEER` controlan secciones. Un 401 del catálogo cierra la sesión; un 403 muestra un aviso sin cerrarla.

## 5. Integración CRUD y resultados

La rama estudiantil anterior tenía inventario y registro local simulados, sin HTTP. Se agregó lectura y creación de `/api/v1/productos` mediante el mismo cliente Ktor; el formulario exige `categoriaId`, `estado=true`, nombre, precio y stock, conforme al DTO real. Clientes y pedidos eran pantallas de posición. Por ello no se declara cumplida la matriz completa de CRUD por rol. Las pruebas y su estado están en `../pruebas/RESULTADOS.md`.

## 6. Problemas, soluciones y limitaciones

No existía `develop` en el repositorio estudiantil; se tomó `main` como base. El directorio actual era vacío y se creó aquí una copia Git preservando el historial y el cambio local de ejecución de `gradlew`. La API de EncryptedSharedPreferences está obsoleta; se usó Android Keystore directamente. La compilación iOS, la matriz Postman y las capturas requieren macOS o backend de laboratorio operativo, respectivamente. No se fabricaron resultados.

## 7. Conclusiones y referencias

La estructura de autenticación y protección de consultas está implementada. Su funcionamiento extremo a extremo necesita validación con PharmaSoft y dispositivos. El CRUD de clientes y ventas del estudiante debe completarse antes de acreditar toda la matriz de la guía.

- Guía Práctica de Laboratorio N.º 10, Mg. Reyna Barreto Benjamin David, 2026.
- Colección Postman “PharmaBackend - Pruebas de seguridad”, adjunta a la práctica.
- [Ktor Bearer](https://ktor.io/docs/client-bearer-auth.html)
- [Koin Compose](https://insert-koin.io/docs/reference/koin-compose/compose/)
- [Android Keystore](https://developer.android.com/privacy-and-security/keystore)
- [Apple Keychain](https://developer.apple.com/documentation/security/keychain-services)

## 8. Evidencias

Ver `../evidencias/PENDIENTES.md`. `../evidencias/11-android-login.png` documenta el arranque real del login Android. Las demás capturas siguen pendientes.
