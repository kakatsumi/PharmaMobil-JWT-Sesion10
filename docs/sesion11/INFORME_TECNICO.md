# Informe técnico · Sesión 11

**Universidad Peruana Unión · Facultad de Ingeniería y Arquitectura**<br>
**Asignatura:** Desarrollo de Aplicaciones Móviles, 2026-2<br>
**Alumno:** Ricardo Alva Pardave<br>
**Proyecto:** PharmaMobil sobre PharmaSoft<br>
**Fecha de la práctica:** 20 de octubre de 2026

## Introducción y objetivos

La práctica convierte el catálogo Android en offline-first. La base SQLite local es la fuente de datos que observa la pantalla; la red sincroniza esa base. El objetivo verificable es listar y registrar productos sin conexión, conservar pendientes y enviarlos a PharmaSoft al reconectar.

## Marco conceptual y arquitectura

`presentation` contiene pantallas y `CatalogoViewModel`; `domain` define `Producto`, `Categoria` y `ProductoRepository`; `data` contiene API REST, SQLDelight, drivers y repositorio offline. La pantalla recoge `observar(): Flow<List<Producto>>`. SQLDelight genera `PharmaDb` desde los archivos `.sq`.

```
Pantalla Compose -> CatalogoViewModel -> ProductoRepository -> ProductoLocal -> SQLite
                                                  |
                                                  +-> ProductoApi -> Ktor/JWT -> PharmaSoft
```

## Herramientas y esquema

Kotlin Multiplatform, Compose Multiplatform, Ktor, Koin, SQLDelight 2.1.0, SQLite y Gradle. La tabla `producto` incluye ID, nombre, precio, stock, `categoriaId`, estado y pendiente. `categoriaId` es una extensión indispensable del ejemplo de la guía: el POST real de PharmaSoft lo exige. La tabla `categoria` permite elegir una categoría ya sincronizada al crear sin red. Los IDs locales son negativos y se calculan dentro de una transacción.

## Drivers y capa local

Android usa `AndroidSqliteDriver` sobre `pharma.db`. Desktop usa `JdbcSqliteDriver` persistente. Las pruebas JVM crean una base independiente en memoria por caso. `ProductoLocal` no importa Ktor ni DTO; conserva pendientes al reemplazar filas sincronizadas y usa transacciones para evitar estados intermedios del Flow.

## Repositorio y sincronización

`registrar` escribe primero en SQLite y luego intenta sincronizar. Un fallo de red o HTTP conserva la fila pendiente. `sincronizar` usa un `Mutex`, sube cada pendiente y exige un ID positivo confirmado antes de sustituir el ID negativo; después descarga todas las páginas de productos y las categorías. `actualizar` y `eliminar` conservan el contrato REST en línea de la sesión anterior. Un 401 no borra SQLite; un 403 mantiene la sesión.

## ViewModel y UI

El ViewModel se suscribe una vez a los Flows de productos y categorías. El botón Sincronizar inicia el intercambio con PharmaSoft. La lista muestra pendientes e IDs negativos. Las pestañas filtran productos reales guardados; el inventario de ejemplo dejó de aparecer como catálogo operativo.

## Pruebas y resultados

Las seis pruebas de `ProductoRepositorioOfflineTest` cubren alta sin conexión, orden de sincronización y reconciliación, emisión de Flow, conservación ante fallo y 403, y reapertura de SQLite. `CrudRestTest` valida rutas y contratos con MockEngine. La suite JVM completa pasó con 36 pruebas y cero fallos. Los resultados ejecutados se registran en `evidencias/RESULTADOS.md`. La compilación Android y las pruebas JVM no equivalen a una prueba contra PharmaSoft vivo.

## Problemas y soluciones

- **POST exige categoría:** se añadió almacenamiento local de categorías y el campo `categoriaId`.
- **Respuesta POST perdida:** PharmaSoft no declara clave de idempotencia. El registro permanece pendiente si no hay confirmación; un reintento puede dar 409. Requiere comprobación manual del servidor antes de resolverlo.
- **Reapertura sin red:** Android conserva cifrados los datos mínimos de sesión mientras el JWT esté vigente; se puede ver el catálogo local aunque `/auth/me` no responda.
- **Memoria RAM:** Gradle y Kotlin se limitaron a 1,5 GiB y 1 GiB, respectivamente, con dos trabajadores como máximo.

## Conclusiones y límites

El flujo local, las transacciones y la sincronización están implementados y se prueban con SQLite en memoria. Siguen pendientes las evidencias físicas del ciclo en modo avión y el producto visible en Swagger, que dependen de un dispositivo y un backend activo. No se implementó iOS.

## Referencias

- Guía Práctica de Laboratorio N.º 11, Universidad Peruana Unión, 20-10-2026.
- [SQLDelight](https://sqldelight.github.io/sqldelight/).
- [Kotlin Multiplatform](https://kotlinlang.org/docs/multiplatform.html).
- [Ktor Client](https://ktor.io/docs/client-testing.html).
- [PharmaSoft del docente](https://github.com/dreyna/pharmaSoft).
