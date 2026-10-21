# Lista de cotejo original · 2 puntos por criterio

**Alumno:** Ricardo Alva Pardave<br>
**Alcance:** Android y JVM de prueba. iOS está excluido por indicación del docente; se conserva el criterio original sin declararlo cumplido.

| N.º | Criterio de la guía | Estado | Evidencia real o pendiente |
|---|---|---|---|
| 1 | SQLDelight genera `PharmaDb` | Verificado en compilación Android y pruebas JVM | `generateCommonMainPharmaDbInterface` y `:androidApp:assembleDebug` |
| 2 | Drivers Android, iOS y pruebas | Parcial: Android y JVM; iOS excluido | `CreadorDeDriverAndroid`, `CreadorDeDriverDesktop`, JDBC en memoria por prueba |
| 3 | Capa local sin Ktor ni DTO | Implementado | `ProductoLocal.kt` |
| 4 | Repositorio con `observar()` y `sincronizar()` | Implementado | `ProductoRepository.kt`, `ProductoRepositorioOffline.kt` |
| 5 | ViewModel se suscribe al Flow | Implementado | `CatalogoViewModel.kt` |
| 6 | Productos visibles sin backend | Pendiente de prueba en Android real | Requiere cerrar/reabrir app en modo avión |
| 7 | Registro offline pendiente | Verificado en prueba JVM; pendiente en Android | `ProductoRepositorioOfflineTest` y prueba en modo avión |
| 8 | Sincronización sube antes de bajar | Verificado en prueba JVM | Secuencia POST, GET, categorías |
| 9 | Producto aparece en PharmaSoft | Pendiente | Requiere backend y Swagger activos |
| 10 | Tres pruebas aprobadas en `commonTest` | Seis pruebas JVM en `desktopTest`, todas aprobadas; excepción técnica documentada | El driver JDBC requiere JVM; las pruebas comunes de contratos siguen en `commonTest` |

No se asigna una puntuación académica final sin las pruebas de dispositivo y backend. Los resultados concretos se consignan en `evidencias/RESULTADOS.md`.
