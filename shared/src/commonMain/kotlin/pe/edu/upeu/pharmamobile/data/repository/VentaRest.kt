package pe.edu.upeu.pharmamobile.data.repository

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.ServerResponseException
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import kotlinx.coroutines.CancellationException
import kotlinx.io.IOException
import kotlinx.serialization.Serializable
import pe.edu.upeu.pharmamobile.domain.model.ErrorSesion

@Serializable
data class DetalleVentaRequestDto(val productoId: Long, val cantidad: Int)

@Serializable
data class VentaRequestDto(val clienteId: Long, val detalles: List<DetalleVentaRequestDto>)

@Serializable
data class DetalleVentaDto(
    val productoId: Long, val productoNombre: String, val cantidad: Int,
    val precio: Double, val subtotal: Double
)

@Serializable
data class VentaDto(
    val id: Long, val fecha: String, val clienteId: Long, val clienteNombre: String,
    val estado: String, val total: Double, val detalles: List<DetalleVentaDto>
)

/** PharmaSoft solo permite POST y GET de ventas; no ofrece PUT ni DELETE. */
class VentaRest(private val cliente: HttpClient) {
    suspend fun listar(): List<VentaDto> = proteger {
        val todas = mutableListOf<VentaDto>()
        var pagina = 0
        do {
            val respuesta: PaginaDto<VentaDto> = cliente.get("api/v1/ventas") {
                parameter("pagina", pagina)
                parameter("tamanio", 100)
            }.body()
            todas += respuesta.contenido
            pagina++
        } while (!respuesta.ultima)
        todas
    }
    suspend fun buscar(id: Long): VentaDto = proteger { cliente.get("api/v1/ventas/$id").body() }
    suspend fun registrar(datos: VentaRequestDto): VentaDto = proteger {
        cliente.post("api/v1/ventas") { setBody(datos) }.body()
    }

    private suspend fun <T> proteger(block: suspend () -> T): T = try { block() }
    catch (e: CancellationException) { throw e }
    catch (e: ClientRequestException) { throw mapearError(e.response.status, false) }
    catch (e: ServerResponseException) { throw ErrorSesion.Inesperado }
    catch (e: IOException) { throw ErrorSesion.SinConexion }
}
