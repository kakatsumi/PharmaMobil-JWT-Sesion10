# PharmaMobile

Aplicacion multiplataforma para la **gestion integral de una farmacia**: clientes,
productos, pedidos e inventario farmaceutico. Desarrollada con **Kotlin Multiplatform (KMP)**
y **Compose Multiplatform**, con estrategia *offline-first* y consumo de servicios REST.

> Universidad Peruana Union - Facultad de Ingenieria y Arquitectura
> Pregrado - VIII Ciclo - Semestre 2026-2

## Arquitectura y source sets

El codigo compartido vive en el modulo `shared`, organizado por *source sets*:

| Source set     | Rol principal                     | Ejemplos de contenido                                   |
|----------------|-----------------------------------|---------------------------------------------------------|
| `commonMain`   | Logica de negocio compartida      | Modelos (Cliente, Producto, Pedido), casos de uso, validaciones, calculos de stock |
| `androidMain`  | Implementacion especifica Android | Bindings y APIs del ecosistema Google Android (camara, GPS) |
| `iosMain`      | Implementacion especifica iOS     | Bindings y SDK nativo para dispositivos Apple           |

La regla practica: todo lo que sea **estructura de datos y reglas de negocio** va en
`commonMain` para escribirse una sola vez; el **acceso a hardware o APIs nativas** se
resuelve por plataforma en `androidMain` / `iosMain` mediante `expect`/`actual`.

## Estructura del proyecto

- `shared/` - codigo compartido (commonMain, androidMain, iosMain + tests).
- `androidApp/` - aplicacion Android (punto de entrada, `MainActivity`).
- `desktopApp/` - vista de escritorio JVM con ventana del tamaño de un móvil.
- `iosApp/` - aplicacion iOS (requiere macOS con Xcode para compilar).

## Como ejecutar

- **Escritorio Linux x64 (sin Android Studio ni emulador):** instala Java 17 o superior,
  abre una terminal en esta carpeta y ejecuta `./ver-movil.sh`. La primera vez
  se descarga el paquete listo para ejecutar desde GitHub Releases. Se abre una ventana de 400 × 800 con la misma
  interfaz Compose del móvil. Usa `demo` como usuario y contraseña para explorar
  Inicio, Productos, Clientes y Pedidos. Los datos de demostración son locales y
  no se envían al servidor. Para usar PharmaSoft real, primero inicia el backend
  y ejecuta `./ver-movil.sh --backend=http://localhost:8080` con sus credenciales.
  Un servidor remoto requiere HTTPS. El token de escritorio solo vive en memoria
  y se pierde al cerrar la ventana. Consulta [la guía de escritorio](docs/DESKTOP.md).
  Para abrirlo con un clic en Linux, ejecuta una vez `./instalar-acceso-directo.sh`.
  El instalador también crea **Abrir PharmaMobil** junto a esta carpeta para
  iniciarlo directamente desde el gestor de archivos.
- **Android:** abrir el proyecto en Android Studio, seleccionar un emulador o dispositivo
  fisico y ejecutar la configuracion `androidApp`. Por linea de comandos:
  `./gradlew :androidApp:assembleDebug`
- **iOS:** abrir `iosApp/` en Xcode desde un equipo Mac y ejecutar.

## Estado

La aplicación comparte interfaz y lógica entre Android, iOS y JVM Desktop.

## Configuracion

- Package base: `pe.edu.upeu.pharmamobile`
- Kotlin `2.4.10` - Compose Multiplatform `1.11.1` - AGP `9.3.1`
- minSdk 24 / targetSdk 36

## AUTENTICACIÓN JWT

La sesión 10 integra el login de PharmaSoft en la aplicación KMP. La interfaz Compose usa `SesionViewModel`, casos de uso y `AuthRepository`; `AuthRepositorioRest` consume `POST /api/v1/auth/login` y valida la sesión guardada con `GET /api/v1/auth/me`. El JWT no se muestra en pantalla ni se guarda junto con la contraseña.

Android cifra el token con AES-GCM y una llave de Android Keystore. iOS utiliza Keychain. El cliente Ktor compartido instala ContentNegotiation, DefaultRequest, Bearer Auth y Logging; en depuración registra solo cabeceras con `Authorization` saneada, y fuera de ella desactiva el registro. El Bearer se envía de inmediato en rutas protegidas y se excluye del login; la caché se limpia al iniciar/cerrar sesión.

El servidor dura 3600 segundos por token según la guía y no tiene refresh. Al reabrir la app se comprueba `/auth/me`; un 401 invalida la sesión y vuelve al login, mientras un 403 mantiene la sesión y muestra falta de permisos. La UI usa `CATALOGO_ESCRIBIR`, `CLIENTES_LEER` y `VENTAS_LEER` para mostrar acciones y secciones. El backend mantiene la autorización definitiva.

Cuentas **solo de laboratorio**: `admin` (ADMIN), `vendedor` (VENDEDOR), `almacen` (ALMACENERO), con las claves del docente. No se incluyen claves en producción. El emulador Android accede a PharmaSoft en `http://10.0.2.2:8080`; para iOS Simulator se usa `http://localhost:8080` y se requiere macOS/Xcode. En teléfono físico debe configurarse una dirección alcanzable con TLS.

**Limitaciones:** la rama previa solo tenía inventario simulado y pantallas vacías de clientes/pedidos. Se incorporó lectura y creación protegida de productos usando categoría; clientes y ventas aún no hacen operaciones REST. La matriz POST de la guía requiere completar los CRUD REST de sesiones anteriores. Véase [la documentación de sesión 10](docs/sesion10/README.md), [informe](docs/sesion10/informe/INFORME_TECNICO.md), [manual](docs/sesion10/MANUAL_USUARIO.md) y [cotejo](docs/sesion10/LISTA_COTEJO.md).
