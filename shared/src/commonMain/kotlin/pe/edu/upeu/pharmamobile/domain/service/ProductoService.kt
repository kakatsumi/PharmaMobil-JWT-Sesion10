package pe.edu.upeu.pharmamobile.domain.service

import pe.edu.upeu.pharmamobile.domain.model.Producto
import pe.edu.upeu.pharmamobile.domain.result.ResultadoProductos

/**
 * Servicio de consulta de productos.
 * Encapsula obtencion, validacion y consultas sobre colecciones (filter, map, find).
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
        val disponibles = disponibles(productos)
        return if (disponibles.isEmpty()) {
            ResultadoProductos.Error("No hay productos con stock disponible")
        } else {
            ResultadoProductos.Exito(disponibles)
        }
    }

    /** filter: selecciona los productos con stock disponible. */
    fun disponibles(productos: List<Producto>): List<Producto> =
        productos.filter { it.stock > 0 }

    /** map: transforma la coleccion de productos a la lista de sus nombres. */
    fun nombres(productos: List<Producto>): List<String> =
        productos.map { it.nombre }

    /** find: localizacion segura por id, devuelve null si no existe (tipo anulable). */
    fun buscarPorId(productos: List<Producto>, id: Long): Producto? =
        productos.find { it.id == id }
}
