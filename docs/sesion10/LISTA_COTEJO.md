# Lista de cotejo (2 puntos por criterio)

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
| 10 | Android e iOS autenticados | PENDIENTE DE VERIFICACIÓN | Entradas de plataforma | Sin prueba iOS | Ejecutar en ambas plataformas |

**Puntuación comprobada: 4/20.** Las dos pruebas verifican el comportamiento del cliente Ktor con MockEngine. Los demás criterios necesitan dispositivo o backend real.
