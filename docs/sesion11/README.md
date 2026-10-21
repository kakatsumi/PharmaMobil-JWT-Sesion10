# Sesión 11 · Persistencia local y offline-first

**Alumno:** Ricardo Alva Pardave<br>
**Curso:** Desarrollo de Aplicaciones Móviles · Universidad Peruana Unión · 2026-2<br>
**Guía:** práctica N.º 11, 20 de octubre de 2026

Esta rama continúa `feature/completar-hasta-sesion10`. La interfaz Android observa SQLite mediante SQLDelight y Flow. Un producto creado sin red recibe un ID negativo y `pendiente = 1`; al sincronizar se envían pendientes antes de descargar el catálogo. `categoriaId` y las categorías también se conservan localmente porque PharmaSoft exige ese campo para POST.

## Archivos clave

- `shared/src/commonMain/sqldelight/.../Producto.sq` y `Categoria.sq`: esquema y consultas.
- `shared/src/androidMain/.../CreadorDeDriverAndroid.kt`: `pharma.db` en Android.
- `shared/src/desktopMain/.../CreadorDeDriverDesktop.kt`: SQLite persistente en `data/pharma.db`.
- `shared/src/commonMain/.../data/local/ProductoLocal.kt`: transacciones y Flow, sin Ktor ni DTO.
- `shared/src/commonMain/.../data/repository/ProductoRepositorioOffline.kt`: cola local y sincronización.
- `shared/src/commonMain/.../presentation/producto/CatalogoViewModel.kt`: suscripción al Flow.

## Ejecutar

Desde la raíz: `./gradlew :androidApp:assembleDebug` y `./gradlew :shared:desktopTest`. El primer comando genera `androidApp/build/outputs/apk/debug/androidApp-debug.apk`. El proyecto también puede abrirse en IntelliJ IDEA o Android Studio; el flujo por terminal no depende de ellos.

Para el emulador, el backend predeterminado es `http://10.0.2.2:8080/`. En un teléfono físico, abre **Configurar servidor** en la aplicación de depuración y escribe `http://IP_LOCAL_DE_TU_PC:8080/`; cierra y vuelve a abrir la aplicación. Teléfono y computadora deben estar en la misma red. La APK de depuración admite HTTP para el laboratorio; la configuración de producción exige HTTPS.

La vista Firefox se abre con `./abrir-vista-previa.sh` y se detiene con `./abrir-vista-previa.sh --stop`. Usa IndexedDB independiente de la base Android. Compose Desktop real se abre con `./abrir-pharmamobil.sh` cuando PharmaSoft está activo.

## Alcance de la verificación

Las pruebas automatizadas ejercitan altas sin red, IDs negativos, orden subir-bajar, reconciliación y emisión de Flow con una base SQLite en memoria. La compilación de Android comprueba generación del esquema y empaquetado. El ciclo de modo avión, la creación física de `pharma.db` y la existencia del producto en Swagger requieren emulador o teléfono y PharmaSoft ejecutándose; no se sustituyen por la vista web.

No se amplió iOS por indicación del docente. Los archivos iOS anteriores permanecen en el repositorio.
