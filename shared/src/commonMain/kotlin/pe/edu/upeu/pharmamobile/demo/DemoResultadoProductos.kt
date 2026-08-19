package pe.edu.upeu.pharmamobile.demo

import pe.edu.upeu.pharmamobile.domain.result.ResultadoProductos

/**
 * Construye un mensaje legible segun el estado del resultado.
 * Cubre los tres casos del sealed class de forma exhaustiva.
 */
fun mostrarResultado(resultado: ResultadoProductos): String = when (resultado) {
    is ResultadoProductos.Cargando -> "Cargando productos..."
    is ResultadoProductos.Exito ->
        "Productos encontrados: ${resultado.productos.size}\n" +
            resultado.productos.joinToString("\n") { producto ->
                " - ${producto.nombre} (stock: ${producto.stock})"
            }
    is ResultadoProductos.Error -> "Error: ${resultado.mensaje}"
}

/**
 * Version de consola: imprime por pantalla el resultado (como en el demo con println).
 */
fun imprimirResultado(resultado: ResultadoProductos) {
    println(mostrarResultado(resultado))
}
