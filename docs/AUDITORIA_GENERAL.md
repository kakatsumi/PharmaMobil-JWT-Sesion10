# Auditoría general de PharmaMobil

**Alumno:** Ricardo Alva Pardave  
**Curso:** Desarrollo de Aplicaciones Móviles, semestre 2026-2  
**Corte:** 21 de octubre de 2026

## Fuentes y protección

- Proyecto propio: `feature/auth-jwt-alva`, commit base `da6d106` del 14 de octubre de 2026. El árbol estaba limpio antes de modificarlo. Se creó `backup/sesion10-alva` y la rama de trabajo `feature/completar-hasta-sesion10` desde ese commit.
- Referencia PharmaMobil del docente: `dreyna/pharmaMobil`, commit `fe7f420` del 9 de septiembre de 2026. Tiene repositorios en memoria y pantallas iniciales; no contiene las implementaciones de sesión 10 y 11 del alumno.
- Referencia PharmaSoft del docente: `dreyna/pharmaSoft`, commit `fff44f9` del 12 de octubre de 2026. Se leyeron controladores, DTO, seguridad y permisos. No se alteró ese repositorio.
- La guía N.º 11 del 20 de octubre de 2026 es posterior a los commits de referencia. Las funciones posteriores a esa fecha no se han atribuido al docente.

## Estado verificado antes de las correcciones

| Área | Estado | Evidencia y problema | Prioridad |
|---|---|---|---|
| Arquitectura compartida | Parcialmente implementado | `shared/commonMain` separa `presentation`, `domain`, `data`; la pantalla de productos aún mezclaba inventario simulado con catálogo REST. | Alta |
| Ktor y JWT | Implementado sin verificar contra backend vivo | `ClienteHttp.kt`, `AuthRepositorioRest.kt`, almacén Android cifrado; pruebas con MockEngine pasaban. No había PharmaSoft ejecutándose. | Alta |
| Productos | Parcialmente implementado | GET paginado solo extraía nombres y POST registraba; faltaban lectura tipada, GET por ID, PUT y DELETE. | Alta |
| Clientes | Pendiente | `ClienteScreen.kt` era una pantalla vacía. PharmaSoft sí expone GET, POST, PUT y DELETE. | Alta |
| Ventas | Pendiente | La pantalla `Pedidos` era vacía y no corresponde al recurso `/api/v1/ventas`. PharmaSoft expone POST, GET por ID y GET paginado, sin PUT ni DELETE. | Alta |
| Manejo de errores | Parcialmente implementado | Se distinguían 400, 401 y 403; faltaban 404, 409, timeout y 500. | Media |
| Persistencia offline | Pendiente | La lista visible dependía de respuestas HTTP o datos de ejemplo. No había SQLDelight ni SQLite. | Alta |
| Vista previa Ubuntu | Implementado sin verificar con backend real | `desktopApp` y `ver-movil.sh` ofrecen demo con MockEngine. La demo está rotulada, pero no equivale a la operación real. | Media |
| APK | Implementado y verificado en compilación base | `:androidApp:assembleDebug` pasó antes de los cambios. Falta prueba en dispositivo para acreditar funcionamiento. | Alta |
| iOS | Excluido por el docente | Se conservan los archivos previos sin ampliar su implementación. | No aplica |

## Contratos reales de PharmaSoft

`GET /api/v1/productos`, `/clientes` y `/ventas` devuelven `PaginaResponseDTO` con `contenido`, `pagina`, `tamanio`, `totalElementos`, `totalPaginas`, `ultima`. Producto POST/PUT exige `nombre`, `precio`, `stock`, `estado` y `categoriaId`. Cliente POST/PUT exige DNI, nombres, apellidos, email y estado. Venta POST exige `clienteId` y al menos un detalle con `productoId` y `cantidad`.

Permisos: catálogo GET requiere `CATALOGO_LEER`, sus escrituras `CATALOGO_ESCRIBIR`; clientes GET requiere `CLIENTES_LEER`, sus escrituras `CLIENTES_ESCRIBIR`; ventas GET requiere `VENTAS_LEER` y POST `VENTAS_REGISTRAR`. ADMIN tiene todos; VENDEDOR lee catálogo y opera clientes/ventas; ALMACENERO lee y escribe catálogo.

## Plan de corrección

1. Completar contratos REST tipados y pantallas existentes, corregir estados/errores y comprobar JWT mediante pruebas de cliente.
2. Crear `feature/offline-first-alva` desde la rama anterior y agregar esquema SQLDelight, driver Android y JVM, almacenamiento local y sincronización segura.
3. Hacer que la interfaz lea un `Flow` local, registrar pendientes con ID negativo y conservarlos ante errores HTTP y de red.
4. Preparar vista previa local, compilar APK y ejecutar pruebas. Registrar por separado lo que requiere backend, emulador o teléfono y no estuvo disponible.

## Riesgos conocidos

- La API no declara una clave de idempotencia para POST de productos. Si el servidor guarda un producto y la respuesta se pierde, un reintento puede responder 409 por nombre duplicado; se necesita reconciliación verificable antes de marcarlo sincronizado.
- La guía usa un esquema de productos sin `categoriaId`, pero PharmaSoft lo exige en POST. La implementación local debe preservar ese dato.
- Una captura web o Desktop no acredita el driver SQLite Android ni el ciclo en modo avión.
