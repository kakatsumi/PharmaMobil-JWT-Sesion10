package pe.edu.upeu.pharmamobile.desktop

import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.engine.mock.toByteArray
import kotlinx.serialization.json.*
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import java.net.URI
import pe.edu.upeu.pharmamobile.App
import pe.edu.upeu.pharmamobile.data.local.AlmacenSeguro
import pe.edu.upeu.pharmamobile.di.iniciarDependencias

/** The desktop preview keeps JWTs in memory, so closing the window ends the session. */
private class AlmacenEnMemoria : AlmacenSeguro {
    private var token: String? = null
    override fun guardarToken(token: String) { this.token = token }
    override fun leerToken(): String? = token
    override fun borrarToken() { token = null }
}

internal fun motorDemostracion(): HttpClientEngine {
    val nombres = mutableListOf("Paracetamol 500 mg", "Vitamina C")
    return MockEngine { request ->
    val json = when (request.url.encodedPath) {
        "/api/v1/auth/login" -> """{"token":"demo-token-local","tipo":"Bearer","expiraEnSegundos":3600,"username":"demo","nombreCompleto":"Vista de demostración","roles":["ADMIN"],"permisos":["CATALOGO_ESCRIBIR","CLIENTES_LEER","VENTAS_LEER"]}"""
        "/api/v1/auth/me" -> """{"username":"demo","empleadoNombre":"Vista de demostración","roles":["ADMIN"],"permisos":["CATALOGO_ESCRIBIR","CLIENTES_LEER","VENTAS_LEER"]}"""
        "/api/v1/productos" -> if (request.method.value == "POST") {
            val producto = Json.parseToJsonElement(request.body.toByteArray().decodeToString()).jsonObject
            nombres.add(producto.getValue("nombre").jsonPrimitive.content)
            "{}"
        } else buildJsonObject {
            put("contenido", buildJsonArray { nombres.forEach { nombre -> add(buildJsonObject { put("nombre", nombre) }) } })
        }.toString()
        "/api/v1/categorias" -> """[{"id":1,"nombre":"Medicamentos","estado":true},{"id":2,"nombre":"Suplementos","estado":true}]"""
        else -> return@MockEngine respond("{}", HttpStatusCode.NotFound)
    }
    respond(json, HttpStatusCode.OK, headersOf(HttpHeaders.ContentType, "application/json"))
    }
}

private fun backendSeguro(argumentos: Array<String>): String? {
    val valor = argumentos.firstOrNull { it.startsWith("--backend=") }?.substringAfter('=') ?: return null
    val uri = URI(valor)
    require(uri.scheme == "https" ||
        (uri.scheme == "http" && uri.host in setOf("localhost", "127.0.0.1", "::1"))) {
        "El backend remoto debe usar HTTPS. Para pruebas locales se admite http://localhost:8080"
    }
    require(!uri.host.isNullOrBlank()) { "Indica una URL de backend válida" }
    return valor.trimEnd('/') + "/"
}

fun main(args: Array<String>) {
    val backend = backendSeguro(args)
    val demostracion = backend == null
    iniciarDependencias(
        almacen = AlmacenEnMemoria(),
        motor = if (demostracion) motorDemostracion() else OkHttp.create(),
        baseUrl = backend ?: "http://localhost:8080/",
        depuracion = false
    )

    application {
        Window(
            onCloseRequest = ::exitApplication,
            title = if (demostracion) "PharmaMobil · vista de demostración" else "PharmaMobil · backend",
            state = rememberWindowState(size = DpSize(400.dp, 800.dp)),
            resizable = false
        ) {
            App(modoDemostracion = demostracion)
        }
    }
}
