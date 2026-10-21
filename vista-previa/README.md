# Vista previa local de PharmaMobil

Desde la raíz del proyecto ejecuta `./abrir-vista-previa.sh`. El archivo inicia un servidor Python limitado a `127.0.0.1:5173` y abre Firefox. Para detenerlo, ejecuta `./abrir-vista-previa.sh --stop`. No instala nada ni crea accesos directos. También puedes abrir `http://127.0.0.1:5173/` manualmente después de iniciar el servidor. `file://` no funciona para el API por las reglas de CORS del navegador.

La interfaz llama a los contratos reales de PharmaSoft en `http://localhost:8080/api/v1`: login JWT, productos, clientes y ventas. Debes iniciar el backend en esa dirección. PharmaSoft permite el origen `http://127.0.0.1:5173` en su configuración CORS. La vista nunca muestra datos inventados. Sin servidor, enseña solo los productos que se hayan sincronizado previamente.

Los productos y categorías de la vista se guardan en IndexedDB. El JWT vive únicamente en memoria; al recargar la página con red, vuelve a iniciar sesión. Se conserva solo el usuario y sus permisos hasta que expire la sesión para permitir altas locales sin conexión. Una creación sin red recibe un ID negativo y queda pendiente. `Sincronizar` envía primero los pendientes y luego descarga el catálogo. Clientes y ventas requieren conexión.

Esta base del navegador es independiente de `pharma.db` de Android y Desktop. La vista web sirve para explorar operaciones y escenarios del navegador; **la evidencia académica de SQLite y modo avión debe tomarse en Android**. Si un POST se guarda en PharmaSoft y su respuesta se pierde, el backend no ofrece idempotencia: el producto permanece pendiente y un reintento puede responder 409. Comprueba el servidor antes de resolver ese caso.
