package pe.edu.upeu.pharmamobile.data

import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlin.test.assertFailsWith
import java.nio.file.Files
import pe.edu.upeu.pharmamobile.data.local.CreadorDeDriverDesktop
import pe.edu.upeu.pharmamobile.data.local.ProductoLocal
import pe.edu.upeu.pharmamobile.data.repository.ProductoApi
import pe.edu.upeu.pharmamobile.data.repository.ProductoRepositorioOffline
import pe.edu.upeu.pharmamobile.db.PharmaDb
import pe.edu.upeu.pharmamobile.domain.model.Categoria
import pe.edu.upeu.pharmamobile.domain.model.ErrorSesion
import pe.edu.upeu.pharmamobile.domain.model.Producto

private fun baseEnMemoria(): ProductoLocal {
    val driver = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY)
    PharmaDb.Schema.create(driver)
    return ProductoLocal(PharmaDb(driver))
}

private class ApiPrueba : ProductoApi {
    var conectado = false
    var fallo: ErrorSesion? = null
    val eventos = mutableListOf<String>()
    val servidor = mutableListOf<Producto>()
    override suspend fun listar(): List<Producto> {
        eventos += "GET"
        if (!conectado) throw ErrorSesion.SinConexion
        return servidor.toList()
    }
    override suspend fun buscar(id: Long): Producto = servidor.first { it.id == id }
    override suspend fun registrar(producto: Producto): Producto {
        eventos += "POST"
        fallo?.let { throw it }
        if (!conectado) throw ErrorSesion.SinConexion
        return producto.copy(id = (servidor.maxOfOrNull { it.id } ?: 0) + 1).also { servidor += it }
    }
    override suspend fun actualizar(producto: Producto): Producto = producto
    override suspend fun eliminar(id: Long) { servidor.removeAll { it.id == id } }
    override suspend fun categorias(): List<Categoria> {
        eventos += "CATEGORIAS"
        if (!conectado) throw ErrorSesion.SinConexion
        return listOf(Categoria(3, "Medicamentos", true))
    }
}

class ProductoRepositorioOfflineTest {
    private fun nuevo(nombre: String) = Producto(0, nombre, 4.5, 20, categoriaId = 3)

    @Test fun sinConexionSeConservaPendienteConIdNegativo() = runBlocking {
        val local = baseEnMemoria()
        val api = ApiPrueba()
        val repo = ProductoRepositorioOffline(local, api)
        val primero = repo.registrar(nuevo("Ibuprofeno"))
        val segundo = repo.registrar(nuevo("Paracetamol"))
        assertEquals(-1, primero.id)
        assertEquals(-2, segundo.id)
        assertEquals(2, repo.observar().first().count { it.pendiente })
        assertEquals(2, local.pendientes().size)
    }

    @Test fun sincronizarSubeAntesDeBajarYReconciliaElId() = runBlocking {
        val local = baseEnMemoria()
        val api = ApiPrueba()
        val repo = ProductoRepositorioOffline(local, api)
        repo.registrar(nuevo("Ibuprofeno"))
        api.eventos.clear()
        api.conectado = true
        repo.sincronizar()
        assertEquals(listOf("POST", "GET", "CATEGORIAS"), api.eventos)
        assertTrue(local.pendientes().isEmpty())
        val producto = repo.observar().first().single()
        assertEquals(1, producto.id)
        assertFalse(producto.pendiente)
        assertEquals(1, api.servidor.size)
    }

    @Test fun flowEmiteAlGuardarEnLaBaseLocal() = runBlocking {
        val local = baseEnMemoria()
        val emisiones = async(start = CoroutineStart.UNDISPATCHED) {
            withTimeout(3_000) { local.observar().take(2).toList() }
        }
        local.guardarPendiente(nuevo("Ibuprofeno"))
        val listas = emisiones.await()
        assertTrue(listas.first().isEmpty())
        assertEquals(-1, listas.last().single().id)
    }

    @Test fun falloDeSubidaNoBorraPendientesNiDatosSincronizados() = runBlocking {
        val local = baseEnMemoria()
        local.reemplazarSincronizados(listOf(Producto(10, "Servidor", 3.0, 2, 3)))
        val api = ApiPrueba()
        val repo = ProductoRepositorioOffline(local, api)
        repo.registrar(nuevo("Local"))
        try { repo.sincronizar() } catch (_: ErrorSesion.SinConexion) { }
        val lista = repo.observar().first()
        assertEquals(setOf("Servidor", "Local"), lista.map { it.nombre }.toSet())
        assertEquals(1, local.pendientes().size)
    }

    @Test fun error403ConservaPendienteParaReintento() = runBlocking {
        val local = baseEnMemoria()
        val api = ApiPrueba().apply { conectado = true; fallo = ErrorSesion.SinPermiso }
        val repo = ProductoRepositorioOffline(local, api)
        repo.registrar(nuevo("Local"))
        assertEquals(-1, local.pendientes().single().id)
        assertFailsWith<ErrorSesion.SinPermiso> { repo.sincronizar() }
        assertEquals(1, local.pendientes().size)
    }

    @Test fun baseDesktopPersistePendientesTrasReabrir() = runBlocking {
        val archivo = Files.createTempFile("pharmamobil-offline-", ".db").toFile()
        archivo.delete()
        try {
            val primero = CreadorDeDriverDesktop(archivo).crear()
            val local = ProductoLocal(PharmaDb(primero))
            local.guardarPendiente(nuevo("Persistente"))
            local.reemplazarCategorias(listOf(Categoria(3, "Medicamentos", true)))
            primero.close()

            val segundo = CreadorDeDriverDesktop(archivo).crear()
            val reabierto = ProductoLocal(PharmaDb(segundo))
            assertEquals(-1, reabierto.pendientes().single().id)
            assertEquals("Medicamentos", reabierto.observarCategorias().first().single().nombre)
            segundo.close()
        } finally { archivo.delete() }
    }
}
