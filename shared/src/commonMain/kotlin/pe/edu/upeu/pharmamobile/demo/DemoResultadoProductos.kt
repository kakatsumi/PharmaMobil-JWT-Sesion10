package pe.edu.upeu.pharmamobile.demo

import pe.edu.upeu.pharmamobile.domain.result.ResultadoProductos

/**
 * Convierte un ResultadoProductos en un mensaje legible para mostrar en pantalla o consola.
 */
fun mostrarResultado(resultado: ResultadoProductos): String = when (resultado) {
    is ResultadoProductos.Cargando -> "Cargando productos..."
    is ResultadoProductos.Exito ->
        "Se encontraron ${resultado.productos.size} producto(s):\n" +
            resultado.productos.joinToString("\n") { producto ->
                " - ${producto.nombre} (stock: ${producto.stock})"
            }
    is ResultadoProductos.Error -> "Error: ${resultado.mensaje}"
}
