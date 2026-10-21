package pe.edu.upeu.pharmamobile.data.local

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import pe.edu.upeu.pharmamobile.domain.model.Categoria
import pe.edu.upeu.pharmamobile.db.PharmaDb
import pe.edu.upeu.pharmamobile.db.Producto as FilaProducto
import pe.edu.upeu.pharmamobile.domain.model.Producto

/** La interfaz observa únicamente esta base; aquí no hay dependencias de red. */
class ProductoLocal(private val base: PharmaDb) {
    private val productos = base.productoQueries
    private val categorias = base.categoriaQueries

    fun observar(): Flow<List<Producto>> = productos.listarTodos().asFlow()
        .mapToList(Dispatchers.Default).map { filas -> filas.map { it.aDominio() } }

    fun observarCategorias(): Flow<List<Categoria>> = categorias.listarCategorias().asFlow()
        .mapToList(Dispatchers.Default).map { filas ->
            filas.map { Categoria(it.id, it.nombre, it.estado == 1L) }
        }

    fun pendientes(): List<Producto> = productos.listarPendientes().executeAsList().map { it.aDominio() }

    fun guardarPendiente(producto: Producto): Producto {
        var guardado: Producto? = null
        productos.transaction {
            val id = minOf(productos.siguienteIdLocal().executeAsOne(), 0L) - 1L
            guardado = producto.copy(id = id, pendiente = true)
            insertar(requireNotNull(guardado))
        }
        return requireNotNull(guardado)
    }

    fun confirmarSubida(idLocal: Long, productoServidor: Producto) {
        require(idLocal < 0 && productoServidor.id > 0)
        productos.transaction {
            productos.borrarPorId(idLocal)
            insertar(productoServidor.copy(pendiente = false))
        }
    }

    fun reemplazarSincronizados(productosServidor: List<Producto>) {
        productos.transaction {
            productos.borrarSincronizados()
            productosServidor.forEach { insertar(it.copy(pendiente = false)) }
        }
    }

    fun reemplazarCategorias(lista: List<Categoria>) {
        categorias.transaction {
            categorias.borrarCategorias()
            lista.forEach { categorias.insertarCategoria(it.id, it.nombre, if (it.estado) 1L else 0L) }
        }
    }

    private fun insertar(producto: Producto) {
        productos.insertarProducto(
            producto.id, producto.nombre, producto.precio, producto.stock.toLong(),
            requireNotNull(producto.categoriaId), if (producto.estado) 1L else 0L,
            if (producto.pendiente) 1L else 0L
        )
    }

    private fun FilaProducto.aDominio() = Producto(
        id, nombre, precio, stock.toInt(), categoriaId, estado == 1L, pendiente == 1L
    )
}
