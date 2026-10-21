# Manual de usuario · Sesión 11

**Alumno:** Ricardo Alva Pardave

1. Inicia PharmaSoft en tu computadora y comprueba `http://localhost:8080/api/health`.
2. Abre PharmaMobil. En emulador usa el servidor predeterminado `http://10.0.2.2:8080/`. En teléfono abre **Configurar servidor** y escribe `http://IP_LOCAL_DE_TU_PC:8080/`; cierra y vuelve a abrir la app. Usa una cuenta de laboratorio válida.
3. En **Productos**, pulsa **Sincronizar**. El catálogo y las categorías quedan guardados en SQLite.
4. Desactiva la red y cierra la app. Ábrela de nuevo antes de que venza el JWT. Verás los productos guardados; la sincronización mostrará el fallo de conexión sin borrar la lista.
5. Pulsa **Registrar producto**, elige una categoría previamente sincronizada y guarda. La lista mostrará **Pendiente** y un ID negativo.
6. Restaura la red y pulsa **Sincronizar**. Comprueba que el producto pierde la marca pendiente y recibe un ID positivo.
7. Verifica el mismo producto en Swagger de PharmaSoft (`http://localhost:8080/swagger-ui.html`) para acreditar que llegó al servidor.

Si no hay categorías guardadas, sincroniza con red antes de registrar offline. Si aparece 401, inicia sesión nuevamente y vuelve a sincronizar; la fila pendiente permanece. Si aparece 409 después de una respuesta de red perdida, verifica en PharmaSoft si ya existe el producto antes de reintentar.

**Vista Ubuntu:** ejecuta `./abrir-vista-previa.sh` en la raíz y usa Firefox. Para cerrar el servidor: `./abrir-vista-previa.sh --stop`. La vista web mantiene su propia base IndexedDB. Para abrir Compose Desktop conectado al backend real: `./abrir-pharmamobil.sh`.

**Instalar la APK:** copia `androidApp/build/outputs/apk/debug/androidApp-debug.apk` al teléfono y ábrela desde el gestor de archivos; Android puede solicitar permitir instalaciones desde esa fuente. Con ADB, si lo tienes instalado y el dispositivo autoriza la depuración USB, ejecuta `adb devices` y luego `adb install -r androidApp/build/outputs/apk/debug/androidApp-debug.apk`. La APK es de depuración y requiere que teléfono y computadora estén en la misma red para acceder al backend local.
