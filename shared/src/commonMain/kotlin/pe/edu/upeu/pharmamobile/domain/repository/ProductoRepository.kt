package pe.edu.upeu.pharmamobile.domain.repository

import kotlinx.coroutines.flow.Flow
import pe.edu.upeu.pharmamobile.domain.model.Categoria
import pe.edu.upeu.pharmamobile.domain.model.Producto

interface ProductoRepository {
    fun observar(): Flow<List<Producto>>
    fun observarCategorias(): Flow<List<Categoria>>
    suspend fun registrar(producto: Producto): Producto
    suspend fun actualizar(producto: Producto): Producto
    suspend fun eliminar(id: Long)
    suspend fun sincronizar()
}
