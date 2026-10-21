package pe.edu.upeu.pharmamobile.data.repository

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.flow.Flow
import pe.edu.upeu.pharmamobile.data.local.ProductoLocal
import pe.edu.upeu.pharmamobile.domain.model.ErrorSesion
import pe.edu.upeu.pharmamobile.domain.model.Categoria
import pe.edu.upeu.pharmamobile.domain.model.Producto
import pe.edu.upeu.pharmamobile.domain.repository.ProductoRepository

class ProductoRepositorioOffline(
    private val local: ProductoLocal,
    private val remoto: ProductoApi
) : ProductoRepository {
    private val candado = Mutex()

    override fun observar(): Flow<List<Producto>> = local.observar()
    override fun observarCategorias(): Flow<List<Categoria>> = local.observarCategorias()

    override suspend fun registrar(producto: Producto): Producto {
        val guardado = local.guardarPendiente(producto)
        try { sincronizar() }
        catch (e: CancellationException) { throw e }
        catch (_: ErrorSesion) { /* La escritura local ya quedó confirmada. */ }
        return guardado
    }

    override suspend fun actualizar(producto: Producto): Producto = candado.withLock {
        // La práctica exige altas offline. Las ediciones anteriores conservan su contrato REST.
        val actualizado = remoto.actualizar(producto)
        local.reemplazarSincronizados(remoto.listar())
        actualizado
    }

    override suspend fun eliminar(id: Long) = candado.withLock {
        remoto.eliminar(id)
        local.reemplazarSincronizados(remoto.listar())
    }

    override suspend fun sincronizar() = candado.withLock {
        // Orden indispensable: enviar cada pendiente y confirmar su ID antes de descargar.
        local.pendientes().forEach { pendiente ->
            val confirmado = remoto.registrar(pendiente.copy(id = 0, pendiente = false))
            require(confirmado.id > 0) { "PharmaSoft no devolvió un ID válido" }
            local.confirmarSubida(pendiente.id, confirmado)
        }
        val productosServidor = remoto.listar()
        local.reemplazarSincronizados(productosServidor)
        // Categorías se guardan después; las existentes siguen disponibles si falla esta petición.
        local.reemplazarCategorias(remoto.categorias())
    }
}
