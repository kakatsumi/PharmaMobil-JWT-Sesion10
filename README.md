# PharmaMobil

Aplicación de gestión farmacéutica del curso **Desarrollo de Aplicaciones Móviles** (Universidad Peruana Unión, 2026-2). Alumno: **Ricardo Alva Pardave**. Este proyecto continúa el trabajo de la sesión 10; PharmaSoft es el backend REST del docente.

## Estado y arquitectura

El módulo `shared` contiene modelos y contratos de dominio, servicios Ktor, repositorios, SQLDelight, ViewModels y pantallas Compose. `androidApp` empaqueta la aplicación Android. `desktopApp` muestra la interfaz Compose en Ubuntu. Los archivos iOS heredados se conservan sin ampliar su implementación porque el docente excluyó iOS.

Productos usa `GET/POST/PUT/DELETE /api/v1/productos` y categorías `GET /api/v1/categorias`; clientes usa su CRUD `/api/v1/clientes`; ventas usa `GET/POST /api/v1/ventas`, que son las operaciones reales del backend. La interfaz respeta los permisos que devuelve `/api/v1/auth/me`; el servidor decide finalmente la autorización. Las operaciones protegidas usan JWT con Ktor Auth. Android cifra el token y los datos mínimos de sesión con AES-GCM y Android Keystore. No se guardan contraseñas ni se registran cabeceras `Authorization`.

La pantalla de productos observa `PharmaDb` mediante Flow. Un alta se guarda primero con ID negativo y `pendiente = 1`; al sincronizar, se envía cada pendiente antes de descargar todas las páginas del catálogo. Los datos locales sobreviven a cierres de la aplicación. Las categorías se guardan para poder crear productos sin red. Edición y baja siguen siendo operaciones REST en línea. El backend no ofrece refresh token ni idempotencia de POST; un 401 requiere volver a iniciar sesión y un 409 después de una respuesta perdida requiere comprobar el servidor.

## Requisitos

- Java 17 y Android SDK para compilar la APK. El wrapper Gradle incluido resuelve las dependencias la primera vez.
- PharmaSoft del docente en el puerto 8080 para login, CRUD y sincronización reales.
- Python 3 y Firefox para la vista web local. No hace falta instalar la aplicación en Ubuntu.

## Android y APK

```bash
./gradlew :shared:desktopTest
./gradlew :androidApp:assembleDebug
```

La APK queda en `androidApp/build/outputs/apk/debug/androidApp-debug.apk` y se copia para entrega a `entregables/android/PharmaMobil-debug.apk`. Se puede copiar al teléfono y abrir desde su gestor de archivos, o instalar con `adb install -r entregables/android/PharmaMobil-debug.apk` si ADB está disponible y autorizado por el dispositivo. La copia binaria es local y está excluida del historial Git.

El emulador usa `http://10.0.2.2:8080/` por defecto. En un teléfono físico, abre **Configurar servidor** e introduce `http://IP_PRIVADA_DE_LA_PC:8080/`; cierra y reabre la aplicación. El teléfono y la computadora deben estar en la misma red y PharmaSoft debe escuchar en una interfaz accesible. La APK de depuración admite HTTP de laboratorio; la configuración de producción exige HTTPS. Cambiar esta URL no requiere recompilar. IntelliJ IDEA y Android Studio siguen siendo compatibles con los módulos Gradle.

## Vista Ubuntu sin IDE

```bash
./abrir-vista-previa.sh
./abrir-vista-previa.sh --stop
./abrir-pharmamobil.sh
```

La primera orden abre Firefox en `http://127.0.0.1:5173/` y usa la API real en `http://localhost:8080`. La vista guarda productos en IndexedDB, ofrece login, catálogo, altas pendientes, sincronización, clientes y ventas. No es una maqueta ni comparte la base SQLite Android. La tercera orden abre Compose Desktop conectado al backend real con una base persistente en `data/pharma.db`. El módulo Desktop heredado también conserva su modo de demostración rotulado; los scripts nuevos abren el backend real. Ninguno instala accesos directos ni modifica el menú de Ubuntu.

## Pruebas y documentación

`commonTest` contiene pruebas de autenticación y contratos REST con Ktor MockEngine. `desktopTest` crea una base SQLite en memoria por prueba para verificar altas offline, IDs negativos, sincronización subir-antes-de-bajar, Flow y conservación de pendientes. Estas pruebas no sustituyen la verificación de modo avión en Android ni la comprobación del producto en Swagger.

- [Auditoría general](docs/AUDITORIA_GENERAL.md)
- [Sesión 10](docs/sesion10/README.md)
- [Sesión 11](docs/sesion11/README.md)
- [Informe técnico](docs/sesion11/INFORME_TECNICO.md)
- [Manual de usuario](docs/sesion11/MANUAL_USUARIO.md)
- [Lista de cotejo](docs/sesion11/LISTA_COTEJO.md)
- [Vista previa web](vista-previa/README.md)

## Limitaciones verificables

La prueba de instalación, el ciclo en modo avión y la confirmación en Swagger dependen de un emulador o teléfono y de PharmaSoft activos. No se presentan capturas ni resultados de esas pruebas si no se ejecutaron. El backend no admite una clave de idempotencia para altas: ante una respuesta perdida, la fila local permanece pendiente y se debe comprobar el servidor antes de reintentar.


## Persistencia offline-first

PharmaMobil utiliza SQLDelight y SQLite para almacenar
productos localmente y permitir su consulta sin conexión.

Los productos registrados sin internet se guardan con
un identificador negativo y quedan pendientes de sincronización.

Cuando se recupera la conexión con PharmaSoft, la aplicación
intenta enviar primero los productos pendientes y después
actualiza el catálogo local con los datos del servidor.

La interfaz utiliza Flow para reflejar automáticamente
los cambios almacenados en la base de datos.
