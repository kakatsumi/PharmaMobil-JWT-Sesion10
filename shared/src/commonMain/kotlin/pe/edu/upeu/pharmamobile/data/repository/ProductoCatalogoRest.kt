package pe.edu.upeu.pharmamobile.data.repository

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.ServerResponseException
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import kotlinx.coroutines.CancellationException
import kotlinx.io.IOException
import kotlinx.serialization.Serializable
import pe.edu.upeu.pharmamobile.domain.model.ErrorSesion
import pe.edu.upeu.pharmamobile.domain.model.Producto

@Serializable
data class CategoriaDto(val id: Long, val nombre: String, val estado: Boolean)

@Serializable
data class ProductoRequestDto(
    val nombre: String, val precio: Double, val stock: Int,
    val estado: Boolean, val categoriaId: Long
)

@Serializable
data class ProductoResponseDto(
    val id: Long, val nombre: String, val precio: Double, val stock: Int,
    val estado: Boolean, val categoriaId: Long
) {
    fun aDominio() = Producto(id, nombre, precio, stock, categoriaId, estado)
}

@Serializable
data class PaginaDto<T>(
    val contenido: List<T>, val pagina: Int, val tamanio: Int,
    val totalElementos: Long, val totalPaginas: Int, val ultima: Boolean
)

/** Contrato confirmado con ProductoController de PharmaSoft. */
interface ProductoApi {
    suspend fun listar(): List<Producto>
    suspend fun buscar(id: Long): Producto
    suspend fun registrar(producto: Producto): Producto
    suspend fun actualizar(producto: Producto): Producto
    suspend fun eliminar(id: Long)
    suspend fun categorias(): List<CategoriaDto>
}

class ProductoCatalogoRest(private val cliente: HttpClient) : ProductoApi {
    override suspend fun listar(): List<Producto> = proteger {
        val todos = mutableListOf<Producto>()
        var pagina = 0
        do {
            val respuesta: PaginaDto<ProductoResponseDto> = cliente.get("api/v1/productos") {
                parameter("pagina", pagina)
                parameter("tamanio", 100)
            }.body()
            todos += respuesta.contenido.map { it.aDominio() }
            pagina++
        } while (!respuesta.ultima)
        todos
    }

    override suspend fun buscar(id: Long): Producto = proteger {
        cliente.get("api/v1/productos/$id").body<ProductoResponseDto>().aDominio()
    }

    override suspend fun categorias(): List<CategoriaDto> = proteger {
        cliente.get("api/v1/categorias").body()
    }

    override suspend fun registrar(producto: Producto): Producto = proteger {
        cliente.post("api/v1/productos") { setBody(producto.aRequest()) }
            .body<ProductoResponseDto>().aDominio()
    }

    override suspend fun actualizar(producto: Producto): Producto = proteger {
        require(producto.id > 0) { "El producto debe existir en el servidor" }
        cliente.put("api/v1/productos/${producto.id}") { setBody(producto.aRequest()) }
            .body<ProductoResponseDto>().aDominio()
    }

    override suspend fun eliminar(id: Long) = proteger {
        cliente.delete("api/v1/productos/$id")
        Unit
    }

    private fun Producto.aRequest() = ProductoRequestDto(
        nombre, precio, stock, estado,
        requireNotNull(categoriaId) { "Selecciona una categoría" }
    )

    private suspend fun <T> proteger(block: suspend () -> T): T = try { block() }
    catch (e: CancellationException) { throw e }
    catch (e: ClientRequestException) { throw mapearError(e.response.status, false) }
    catch (e: ServerResponseException) { throw ErrorSesion.Inesperado }
    catch (e: IOException) { throw ErrorSesion.SinConexion }
}
