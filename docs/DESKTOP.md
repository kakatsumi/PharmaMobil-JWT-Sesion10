# Vista móvil en el escritorio

Se llama **Compose Multiplatform Desktop (JVM)**. Ejecuta la interfaz móvil en una
ventana de escritorio de 400 × 800 píxeles lógicos. No es un emulador Android:
reutiliza el código Compose de `shared/commonMain`, por lo que consume menos
recursos. No requiere Android Studio para abrirla en este equipo.

## Abrir la vista

En Linux, desde la carpeta principal del repositorio:

```bash
./ver-movil.sh
```

Si prefieres abrirlo como cualquier programa, instala una vez el acceso directo:

```bash
./instalar-acceso-directo.sh
```

Después haz doble clic en **PharmaMobil** en el escritorio, o búscalo como
**PharmaMobil** en el menú de aplicaciones. El acceso directo apunta a esta
carpeta del proyecto; si la mueves, vuelve a ejecutar el instalador.

En Linux, si moviste el archivo `.desktop` a otra carpeta y el gestor de
archivos lo abre como texto, usa el ejecutable **Abrir PharmaMobil** que el
instalador deja junto a la carpeta del proyecto. Es un pequeño iniciador
nativo; abre la misma vista sin mostrar código.

Requiere Java 17 o superior. En Linux x64, el lanzador descarga una sola vez
el JAR de la [versión de escritorio](https://github.com/kakatsumi/PharmaMobil-JWT-Sesion10/releases/tag/v0.2.0-desktop),
comprueba su SHA-256 y después lo ejecuta con un máximo de 512 MiB de heap.
El JAR queda en `dist/` y las siguientes aperturas funcionan sin internet,
Gradle ni Android Studio. Se cierra con el botón normal de la ventana.

Para compilar cambios del código fuente usa
`PHARMAMOBIL_BUILD_FROM_SOURCE=1 ./ver-movil.sh`. Ese modo requiere Gradle, sus dependencias y el SDK Android
configurado para el módulo compartido; el lanzador limita Gradle a 1,5 GiB y
el compilador Kotlin a 768 MiB. La vista precompilada evita esa compilación.

En el inicio de sesión de demostración escribe `demo` en ambos campos. El
catálogo empieza con dos productos y dos categorías de ejemplo. Puedes recorrer
Inicio, Productos, Clientes y Pedidos y probar el formulario. En Productos,
**Registrar producto** abre una vista completa con desplazamiento vertical,
selección de categoría y un botón para volver al catálogo. Este modo usa un
servidor simulado dentro del proceso; los productos registrados se conservan
mientras la app está abierta y se pierden al cerrarla. No llegan a PharmaSoft.
La barra de título dice «vista de demostración».

## Conectar con PharmaSoft

Con el backend funcionando en el mismo equipo:

```bash
./ver-movil.sh --backend=http://localhost:8080
```

Usa las credenciales reales que te proporcionaron para el laboratorio. Si el
backend está en otra máquina, usa una URL HTTPS alcanzable, por ejemplo
`./ver-movil.sh --backend=https://farmacia.ejemplo.com`. El token JWT se mantiene
solo en memoria en JVM Desktop; al cerrar se debe iniciar sesión de nuevo.

El modo demostración sirve para inspeccionar la interfaz. La conexión real
requiere PharmaSoft y su base de datos operativos; no vienen incluidos en el
lanzador.
