package pe.edu.upeu.pharmamobile.data.repository

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.Flow
import pe.edu.upeu.pharmamobile.domain.model.Producto
import pe.edu.upeu.pharmamobile.domain.model.Categoria
import pe.edu.upeu.pharmamobile.domain.repository.ProductoRepository

/** Conserva el comportamiento previo para plataformas sin driver local configurado. */
class ProductoRepositorioEnRed(private val remoto: ProductoApi) : ProductoRepository {
    private val productos = MutableStateFlow<List<Producto>>(emptyList())
    private val categorias = MutableStateFlow<List<Categoria>>(emptyList())
    override fun observar(): Flow<List<Producto>> = productos
    override fun observarCategorias(): Flow<List<Categoria>> = categorias
    override suspend fun registrar(producto: Producto): Producto = remoto.registrar(producto).also { sincronizar() }
    override suspend fun actualizar(producto: Producto): Producto = remoto.actualizar(producto).also { sincronizar() }
    override suspend fun eliminar(id: Long) { remoto.eliminar(id); sincronizar() }
    override suspend fun sincronizar() {
        productos.value = remoto.listar()
        categorias.value = remoto.categorias()
    }
}
