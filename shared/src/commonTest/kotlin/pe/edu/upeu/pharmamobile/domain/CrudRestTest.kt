package pe.edu.upeu.pharmamobile.domain

import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue
import pe.edu.upeu.pharmamobile.data.local.AlmacenSeguro
import pe.edu.upeu.pharmamobile.data.remote.crearHttpClient
import pe.edu.upeu.pharmamobile.data.repository.ClienteRequestDto
import pe.edu.upeu.pharmamobile.data.repository.ClienteRest
import pe.edu.upeu.pharmamobile.data.repository.DetalleVentaRequestDto
import pe.edu.upeu.pharmamobile.data.repository.ProductoCatalogoRest
import pe.edu.upeu.pharmamobile.data.repository.VentaRequestDto
import pe.edu.upeu.pharmamobile.data.repository.VentaRest
import pe.edu.upeu.pharmamobile.domain.model.ErrorSesion
import pe.edu.upeu.pharmamobile.domain.model.Producto

private class TokenPrueba : AlmacenSeguro {
    var token: String? = "token-de-prueba"
    override fun guardarToken(token: String) { this.token = token }
    override fun leerToken(): String? = token
    override fun borrarToken() { token = null }
}

private val jsonHeader = headersOf(HttpHeaders.ContentType, "application/json")

class CrudRestTest {
    @Test fun productosUsanPaginacionYCuerpoReal() = runBlocking {
        val rutas = mutableListOf<String>()
        val cliente = crearHttpClient(MockEngine { request ->
            rutas += "${request.method.value} ${request.url.encodedPath}"
            assertEquals("Bearer token-de-prueba", request.headers[HttpHeaders.Authorization])
            val json = when (request.method.value + " " + request.url.encodedPath) {
                "GET /api/v1/productos" -> if (request.url.parameters["pagina"] == "0")
                    """{"contenido":[{"id":1,"nombre":"Uno","precio":1.5,"stock":2,"estado":true,"categoriaId":3}],"pagina":0,"tamanio":100,"totalElementos":2,"totalPaginas":2,"ultima":false}"""
                else """{"contenido":[{"id":2,"nombre":"Dos","precio":2.5,"stock":3,"estado":true,"categoriaId":3}],"pagina":1,"tamanio":100,"totalElementos":2,"totalPaginas":2,"ultima":true}"""
                "POST /api/v1/productos" -> """{"id":3,"nombre":"Tres","precio":3.5,"stock":4,"estado":true,"categoriaId":3}"""
                "PUT /api/v1/productos/3" -> """{"id":3,"nombre":"Tres","precio":3.5,"stock":5,"estado":true,"categoriaId":3}"""
                "DELETE /api/v1/productos/3" -> ""
                else -> error("Ruta inesperada: ${request.url}")
            }
            respond(json, if (request.method.value == "POST") HttpStatusCode.Created else HttpStatusCode.OK, jsonHeader)
        }, TokenPrueba(), "http://localhost:8080/", false)
        val api = ProductoCatalogoRest(cliente)
        assertEquals(listOf(1L, 2L), api.listar().map { it.id })
        assertEquals(3L, api.registrar(Producto(0, "Tres", 3.5, 4, 3)).id)
        assertEquals(5, api.actualizar(Producto(3, "Tres", 3.5, 5, 3)).stock)
        api.eliminar(3)
        assertEquals(5, rutas.size)
        cliente.close()
    }

    @Test fun clientesYVentasUsanLosRecursosDelBackend() = runBlocking {
        val rutas = mutableListOf<String>()
        val cliente = crearHttpClient(MockEngine { request ->
            rutas += "${request.method.value} ${request.url.encodedPath}"
            val json = when (request.url.encodedPath) {
                "/api/v1/clientes" -> if (request.method.value == "POST")
                    """{"id":7,"dni":"12345678","nombres":"Ana","apellidos":"Rios","email":"a@b.pe","estado":true}"""
                else """{"contenido":[],"pagina":0,"tamanio":100,"totalElementos":0,"totalPaginas":0,"ultima":true}"""
                "/api/v1/ventas" -> if (request.method.value == "POST")
                    """{"id":8,"fecha":"2026-10-20T10:00:00","clienteId":7,"clienteNombre":"Ana Rios","estado":"REGISTRADA","total":3.5,"detalles":[]}"""
                else """{"contenido":[],"pagina":0,"tamanio":100,"totalElementos":0,"totalPaginas":0,"ultima":true}"""
                else -> error("Ruta inesperada")
            }
            respond(json, HttpStatusCode.OK, jsonHeader)
        }, TokenPrueba(), "http://localhost:8080/", false)
        assertTrue(ClienteRest(cliente).listar().isEmpty())
        val nuevo = ClienteRest(cliente).registrar(ClienteRequestDto("12345678", "Ana", "Rios", "a@b.pe"))
        assertEquals(7L, nuevo.id)
        assertTrue(VentaRest(cliente).listar().isEmpty())
        assertEquals(8L, VentaRest(cliente).registrar(VentaRequestDto(7, listOf(DetalleVentaRequestDto(3, 1)))).id)
        assertEquals(listOf("GET /api/v1/clientes", "POST /api/v1/clientes", "GET /api/v1/ventas", "POST /api/v1/ventas"), rutas)
        cliente.close()
    }

    @Test fun error403ConservaToken() = runBlocking {
        val almacen = TokenPrueba()
        val cliente = crearHttpClient(MockEngine { respond("{}", HttpStatusCode.Forbidden, jsonHeader) }, almacen, "http://localhost:8080/", false)
        assertFailsWith<ErrorSesion.SinPermiso> { ProductoCatalogoRest(cliente).listar() }
        assertEquals("token-de-prueba", almacen.token)
        cliente.close()
    }
}
