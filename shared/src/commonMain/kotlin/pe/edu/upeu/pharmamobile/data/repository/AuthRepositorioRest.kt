package pe.edu.upeu.pharmamobile.data.repository

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.ServerResponseException
import io.ktor.client.plugins.auth.authProvider
import io.ktor.client.plugins.auth.providers.BearerAuthProvider
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.CancellationException
import kotlinx.io.IOException
import pe.edu.upeu.pharmamobile.data.local.AlmacenSeguro
import pe.edu.upeu.pharmamobile.data.remote.dto.LoginRequestDto
import pe.edu.upeu.pharmamobile.data.remote.dto.LoginResponseDto
import pe.edu.upeu.pharmamobile.data.remote.dto.UsuarioActualDto
import pe.edu.upeu.pharmamobile.domain.model.Credenciales
import pe.edu.upeu.pharmamobile.domain.model.ErrorSesion
import pe.edu.upeu.pharmamobile.domain.model.Sesion
import pe.edu.upeu.pharmamobile.domain.repository.AuthRepository

class AuthRepositorioRest(private val cliente: HttpClient, private val almacen: AlmacenSeguro) : AuthRepository {
    override suspend fun iniciarSesion(credenciales: Credenciales): Sesion = try {
        val dto: LoginResponseDto = cliente.post("api/v1/auth/login") {
            setBody(LoginRequestDto(credenciales.usuario, credenciales.clave))
        }.body()
        require(dto.tipo.equals("Bearer", ignoreCase = true) && dto.token.isNotBlank())
        almacen.guardarToken(dto.token)
        cliente.authProvider<BearerAuthProvider>()?.clearToken()
        Sesion(dto.token, dto.username, dto.nombreCompleto, dto.permisos.toSet())
    } catch (e: CancellationException) { throw e }
      catch (e: ClientRequestException) { throw mapearError(e.response.status, true) }
      catch (e: IOException) { throw ErrorSesion.SinConexion }
      catch (e: Exception) { throw ErrorSesion.Inesperado }

    override suspend fun sesionGuardada(): Sesion? {
        val token = almacen.leerToken() ?: return null
        return try {
            val me: UsuarioActualDto = cliente.get("api/v1/auth/me").body()
            Sesion(token, me.username, me.empleadoNombre, me.permisos.toSet())
        } catch (e: CancellationException) { throw e }
          catch (e: ClientRequestException) {
              if (e.response.status == HttpStatusCode.Unauthorized) {
                  cerrarSesion()
                  null
              } else throw mapearError(e.response.status, false)
          } catch (e: IOException) { throw ErrorSesion.SinConexion }
    }

    override suspend fun cerrarSesion() {
        almacen.borrarToken()
        cliente.authProvider<BearerAuthProvider>()?.clearToken()
    }
}

fun mapearError(status: HttpStatusCode, duranteLogin: Boolean): ErrorSesion = when (status) {
    HttpStatusCode.BadRequest -> ErrorSesion.SolicitudInvalida
    HttpStatusCode.Unauthorized -> if (duranteLogin) ErrorSesion.CredencialesInvalidas else ErrorSesion.SesionExpirada
    HttpStatusCode.Forbidden -> ErrorSesion.SinPermiso
    HttpStatusCode.NotFound -> ErrorSesion.NoEncontrado
    HttpStatusCode.Conflict -> ErrorSesion.Conflicto
    else -> ErrorSesion.Inesperado
}
