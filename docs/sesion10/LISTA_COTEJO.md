# Lista de cotejo (2 puntos por criterio)

Actualización del 21 de octubre de 2026: el docente excluyó iOS del alcance. Se conserva la fila original 10 como referencia de la guía, pero no se declara verificada en iOS. Las pruebas nuevas con MockEngine cubren rutas y contratos de productos, clientes y ventas; no sustituyen una prueba con PharmaSoft activo.

| N.º | Criterio | Estado | Evidencia / archivo | Prueba y resultado | Pendiente |
|---|---|---|---|---|---|
| 1 | Login obtiene JWT | PENDIENTE DE VERIFICACIÓN | `AuthRepositorioRest.kt` | Sin backend disponible | Probar HTTP 200 |
| 2 | Token seguro por plataforma | PENDIENTE DE VERIFICACIÓN | `AlmacenSeguroAndroid.kt`, `AlmacenSeguroIos.kt` | Revisión de código | Probar dispositivos |
| 3 | Ktor Auth adjunta token | CUMPLE | `ClienteHttp.kt`, `AuthRepositoryTest.kt` | MockEngine verificó Bearer en `/auth/me` | Confirmar además con backend real |
| 4 | sendWithoutRequest | CUMPLE | `ClienteHttp.kt`, `AuthRepositoryTest.kt` | MockEngine verificó ausencia de Bearer en login y presencia en `/auth/me` | Confirmar además con backend real |
| 5 | Logging sanea Authorization | PENDIENTE DE VERIFICACIÓN | `ClienteHttp.kt` | Revisión de código | Captura de log |
| 6 | Sesión persistente | PENDIENTE DE VERIFICACIÓN | `AuthRepositorioRest.kt` | Revisión de código | Reiniciar app |
| 7 | 401 cierra sesión | PENDIENTE DE VERIFICACIÓN | `CatalogoViewModel.kt` | Revisión de código | Provocar 401 |
| 8 | 403 mantiene sesión | PENDIENTE DE VERIFICACIÓN | `CatalogoViewModel.kt` | Revisión de código | Provocar 403 |
| 9 | UI respeta permisos | PENDIENTE DE VERIFICACIÓN | `App.kt` | Revisión de código | Probar los tres roles |
| 10 | Android e iOS autenticados | EXCLUIDO EN iOS POR EL DOCENTE; ANDROID PENDIENTE DE PRUEBA EN DISPOSITIVO | Entrada Android y código iOS previo conservado | Compilación Android; sin prueba de dispositivo | Probar Android real o emulador |

**Puntuación de la guía original:** no se asigna un valor nuevo sin probar el backend y el dispositivo. Las pruebas de cliente y la compilación Android pasan; los criterios de comportamiento de extremo a extremo siguen pendientes.
