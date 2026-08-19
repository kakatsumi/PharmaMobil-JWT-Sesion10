package pe.edu.upeu.pharmamobile.data.repository

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import pe.edu.upeu.pharmamobile.domain.model.Producto

/**
 * Simula la observacion reactiva de productos (Paso 6: corrutinas y Flow).
 * Emite primero una lista vacia (estado de carga) y, tras una espera no bloqueante,
 * los productos "remotos".
 */
class ProductoRemoteRepository {

    private val productosRemotos = listOf(
        Producto(id = 1, nombre = "Paracetamol 500mg", precio = 3.50, stock = 120),
        Producto(id = 2, nombre = "Ibuprofeno 400mg", precio = 5.20, stock = 80),
        Producto(id = 3, nombre = "Amoxicilina 500mg", precio = 8.90, stock = 0),
    )

    fun observarProductos(): Flow<List<Producto>> = flow {
        emit(emptyList())
        delay(1000)
        emit(productosRemotos)
    }

    /** Version puntual (suspend) que devuelve el catalogo tras una espera simulada. */
    suspend fun obtenerCatalogo(): List<Producto> {
        delay(500)
        return productosRemotos
    }
}
