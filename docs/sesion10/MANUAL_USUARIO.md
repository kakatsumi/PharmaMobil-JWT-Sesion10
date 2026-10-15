# Manual de usuario

1. Abre PharmaMobil. La aplicación comprueba si existe una sesión válida.
2. Si aparece el login, escribe usuario y contraseña y pulsa **Iniciar sesión**. Las cuentas de laboratorio son `admin`, `vendedor` y `almacen`; usa las claves entregadas por el docente.
3. En **Productos** puedes consultar el catálogo del servidor. El inventario inferior es una demostración simulada de sesiones previas.
4. En Productos, el botón «Registrar producto» se muestra solo a usuarios con `CATALOGO_ESCRIBIR`. Abre un formulario completo y desplazable: ingresa nombre, precio y stock, y elige una categoría de la lista. Al guardarse vuelve al catálogo con la confirmación del servidor. «Volver al catálogo» permite salir sin registrar.
5. **Clientes** aparece únicamente con `CLIENTES_LEER`; esa pantalla aún está pendiente de CRUD.
6. Si aparece “La sesión expiró”, inicia sesión otra vez. Si aparece “No tienes permiso”, conserva tu sesión y usa otra sección.
7. Pulsa **Cerrar sesión** para borrar el token del dispositivo.
