package pe.edu.upeu.pharmamobile.domain

import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.MockEngineConfig
import io.ktor.client.engine.mock.respond
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull
import kotlin.test.assertTrue
import pe.edu.upeu.pharmamobile.data.local.AlmacenSeguro
import pe.edu.upeu.pharmamobile.data.remote.crearHttpClient
import pe.edu.upeu.pharmamobile.data.repository.AuthRepositorioRest
import pe.edu.upeu.pharmamobile.domain.model.Credenciales
import pe.edu.upeu.pharmamobile.domain.model.ErrorSesion

private class TokenMemoria : AlmacenSeguro {
    var token: String? = null
    override fun guardarToken(token: String) { this.token = token }
    override fun leerToken(): String? = token
    override fun borrarToken() { token = null }
}

class AuthRepositoryTest {
    @Test fun loginGuardaTokenYNoEnviaBearerAlLogin() = runBlocking {
        val storage = TokenMemoria()
        val engine = MockEngine { request ->
            assertEquals("/api/v1/auth/login", request.url.encodedPath)
            assertNull(request.headers[HttpHeaders.Authorization])
            respond("""{"token":"a.b.c","tipo":"Bearer","expiraEnSegundos":3600,"username":"admin","nombreCompleto":"Admin","roles":["ADMIN"],"permisos":["CATALOGO_ESCRIBIR"]}""",
                HttpStatusCode.OK, headersOf(HttpHeaders.ContentType, "application/json"))
        }
        val client = crearHttpClient(engine, storage, "http://localhost:8080/", false)
        val repository = AuthRepositorioRest(client, storage)
        val sesion = repository.iniciarSesion(Credenciales("admin", "clave"))
        assertTrue(sesion.puede("CATALOGO_ESCRIBIR"))
        assertEquals("a.b.c", storage.token)
        repository.cerrarSesion()
        assertNull(storage.token)
        client.close()
    }

    @Test fun credencialesInvalidasNoGuardanToken() = runBlocking {
        val storage = TokenMemoria()
        val client = crearHttpClient(MockEngine { respond("{}", HttpStatusCode.Unauthorized) }, storage, "http://localhost:8080/", false)
        val repository = AuthRepositorioRest(client, storage)
        assertFailsWith<ErrorSesion.CredencialesInvalidas> {
            repository.iniciarSesion(Credenciales("x", "y"))
        }
        assertNull(storage.token)
        client.close()
    }

    @Test fun tokenInvalidoSeBorraAlRestaurar() = runBlocking {
        val storage = TokenMemoria().apply { token = "a.b.c" }
        val client = crearHttpClient(MockEngine { respond("{}", HttpStatusCode.Unauthorized) }, storage, "http://localhost:8080/", false)
        val repository = AuthRepositorioRest(client, storage)
        assertNull(repository.sesionGuardada())
        assertNull(storage.token)
        client.close()
    }
    @Test fun restauraSesionDesdeKeychainSimuladoYUsaBearer() = runBlocking {
        val storage = TokenMemoria().apply { token = "a.b.c" }
        val client = crearHttpClient(MockEngine { request ->
            assertEquals("Bearer a.b.c", request.headers[HttpHeaders.Authorization])
            respond("""{"username":"admin","empleadoNombre":"Administrador","roles":["ADMIN"],"permisos":["CATALOGO_LEER"]}""",
                HttpStatusCode.OK, headersOf(HttpHeaders.ContentType, "application/json"))
        }, storage, "http://localhost:8080/", false)
        val sesion = AuthRepositorioRest(client, storage).sesionGuardada()
        assertEquals("admin", sesion?.usuario)
        assertEquals("Administrador", sesion?.nombreCompleto)
        assertTrue(sesion?.puede("CATALOGO_LEER") == true)
        client.close()
    }

}
