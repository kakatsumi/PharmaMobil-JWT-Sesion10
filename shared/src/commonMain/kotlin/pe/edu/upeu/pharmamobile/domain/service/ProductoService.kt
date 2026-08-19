package pe.edu.upeu.pharmamobile.domain.service

import pe.edu.upeu.pharmamobile.domain.model.Producto
import pe.edu.upeu.pharmamobile.domain.result.ResultadoProductos

/**
 * Servicio de consulta de productos.
 * Encapsula la obtencion y la validacion, devolviendo siempre un ResultadoProductos.
 */
class ProductoService {

    fun obtenerProductos(productos: List<Producto>): ResultadoProductos {
        return try {
            if (productos.isEmpty()) {
                ResultadoProductos.Error("No hay productos registrados")
            } else {
                ResultadoProductos.Exito(productos)
            }
        } catch (e: Exception) {
            ResultadoProductos.Error(e.message ?: "Error desconocido")
        }
    }

    fun productosDisponibles(productos: List<Producto>): ResultadoProductos {
        val disponibles = productos.filter { it.estaDisponible() }
        return if (disponibles.isEmpty()) {
            ResultadoProductos.Error("No hay productos con stock disponible")
        } else {
            ResultadoProductos.Exito(disponibles)
        }
    }
}
