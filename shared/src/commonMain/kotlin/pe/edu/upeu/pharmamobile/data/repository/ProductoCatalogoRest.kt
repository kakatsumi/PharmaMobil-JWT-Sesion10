package pe.edu.upeu.pharmamobile.data.repository

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import kotlinx.coroutines.CancellationException
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonPrimitive
import pe.edu.upeu.pharmamobile.domain.model.Producto

@Serializable
data class CategoriaDto(val id: Long, val nombre: String, val estado: Boolean)

@Serializable
data class ProductoRequestDto(
    val nombre: String, val precio: Double, val stock: Int,
    val estado: Boolean, val categoriaId: Long
)

/** Protected course endpoints use the one shared bearer-enabled HttpClient. */
class ProductoCatalogoRest(private val cliente: HttpClient) {
    suspend fun listar(): List<String> = proteger {
        val root: JsonElement = cliente.get("api/v1/productos").body()
        val items = (root as? JsonObject)?.get("contenido") as? JsonArray ?: emptyList()
        items.mapNotNull { (it as? JsonObject)?.get("nombre")?.jsonPrimitive?.content }
    }

    suspend fun categorias(): List<CategoriaDto> = proteger {
        cliente.get("api/v1/categorias").body()
    }

    suspend fun registrar(producto: Producto, categoriaId: Long) = proteger {
        cliente.post("api/v1/productos") {
            setBody(ProductoRequestDto(producto.nombre, producto.precio, producto.stock, true, categoriaId))
        }.body<JsonElement>()
    }

    private suspend fun <T> proteger(block: suspend () -> T): T = try { block() }
    catch (e: CancellationException) { throw e }
    catch (e: ClientRequestException) { throw mapearError(e.response.status, false) }
}
