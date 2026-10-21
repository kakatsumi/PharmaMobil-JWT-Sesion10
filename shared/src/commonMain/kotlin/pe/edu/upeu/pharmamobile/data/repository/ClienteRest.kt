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

@Serializable
data class ClienteDto(
    val id: Long, val dni: String, val nombres: String, val apellidos: String,
    val email: String, val telefono: String? = null, val direccion: String? = null,
    val estado: Boolean
)

@Serializable
data class ClienteRequestDto(
    val dni: String, val nombres: String, val apellidos: String,
    val email: String, val telefono: String? = null, val direccion: String? = null,
    val estado: Boolean = true
)

class ClienteRest(private val cliente: HttpClient) {
    suspend fun listar(): List<ClienteDto> = proteger {
        val todos = mutableListOf<ClienteDto>()
        var pagina = 0
        do {
            val respuesta: PaginaDto<ClienteDto> = cliente.get("api/v1/clientes") {
                parameter("pagina", pagina)
                parameter("tamanio", 100)
            }.body()
            todos += respuesta.contenido
            pagina++
        } while (!respuesta.ultima)
        todos
    }

    suspend fun buscar(id: Long): ClienteDto = proteger { cliente.get("api/v1/clientes/$id").body() }
    suspend fun registrar(datos: ClienteRequestDto): ClienteDto = proteger {
        cliente.post("api/v1/clientes") { setBody(datos) }.body()
    }
    suspend fun actualizar(id: Long, datos: ClienteRequestDto): ClienteDto = proteger {
        cliente.put("api/v1/clientes/$id") { setBody(datos) }.body()
    }
    suspend fun eliminar(id: Long) = proteger { cliente.delete("api/v1/clientes/$id"); Unit }

    private suspend fun <T> proteger(block: suspend () -> T): T = try { block() }
    catch (e: CancellationException) { throw e }
    catch (e: ClientRequestException) { throw mapearError(e.response.status, false) }
    catch (e: ServerResponseException) { throw ErrorSesion.Inesperado }
    catch (e: IOException) { throw ErrorSesion.SinConexion }
}
