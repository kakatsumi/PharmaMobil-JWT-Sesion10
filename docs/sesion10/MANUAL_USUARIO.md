# Manual de usuario

1. Abre PharmaMobil. La aplicación comprueba si existe una sesión válida.
2. Si aparece el login, escribe usuario y contraseña y pulsa **Iniciar sesión**. Las cuentas de laboratorio son `admin`, `vendedor` y `almacen`; usa las claves entregadas por el docente.
3. En **Productos** puedes consultar el catálogo del servidor. El inventario inferior es una demostración simulada de sesiones previas.
4. El botón para registrar productos se muestra solo a usuarios con `CATALOGO_ESCRIBIR`. El formulario solicita nombre, precio, stock e ID de categoría y envía el producto a PharmaSoft. Espera el mensaje de confirmación del servidor.
5. **Clientes** aparece únicamente con `CLIENTES_LEER`; esa pantalla aún está pendiente de CRUD.
6. Si aparece “La sesión expiró”, inicia sesión otra vez. Si aparece “No tienes permiso”, conserva tu sesión y usa otra sección.
7. Pulsa **Cerrar sesión** para borrar el token del dispositivo.
