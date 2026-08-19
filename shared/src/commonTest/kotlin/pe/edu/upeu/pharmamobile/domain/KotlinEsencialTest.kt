package pe.edu.upeu.pharmamobile.domain

import pe.edu.upeu.pharmamobile.domain.model.Cliente
import pe.edu.upeu.pharmamobile.domain.model.Producto
import pe.edu.upeu.pharmamobile.domain.model.verTel
import pe.edu.upeu.pharmamobile.domain.service.ProductoService
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class KotlinEsencialTest {

    private val servicio = ProductoService()

    private val catalogo = listOf(
        Producto(id = 1, nombre = "A", precio = 1.0, stock = 5),
        Producto(id = 2, nombre = "B", precio = 2.0, stock = 0),
        Producto(id = 3, nombre = "C", precio = 3.0, stock = 8),
    )

    @Test
    fun clientePermiteTelefonoNulo() {
        val c = Cliente(id = 1, nombre = "Ana", correo = "ana@mail.com", telefono = null)
        assertNull(c.telefono)
    }

    @Test
    fun verTelDevuelveDefaultSiEsNulo() {
        val c = Cliente(id = 1, nombre = "Ana", correo = "ana@mail.com", telefono = null)
        assertEquals("No registrado", verTel(c))
    }

    @Test
    fun verTelDevuelveElTelefonoSiExiste() {
        val c = Cliente(id = 1, nombre = "Ana", correo = "ana@mail.com", telefono = "987654321")
        assertEquals("987654321", verTel(c))
    }

    @Test
    fun reabastecerUsaCopySinMutarOriginal() {
        val original = Producto(id = 1, nombre = "Paracetamol", precio = 3.5, stock = 10)
        val actualizado = original.reabastecer(5)
        assertEquals(10, original.stock)
        assertEquals(15, actualizado.stock)
    }

    @Test
    fun nombresMapeaLaColeccion() {
        assertEquals(listOf("A", "B", "C"), servicio.nombres(catalogo))
    }

    @Test
    fun disponiblesFiltraPorStock() {
        assertEquals(2, servicio.disponibles(catalogo).size)
    }

    @Test
    fun buscarPorIdEncuentraProducto() {
        assertEquals("B", servicio.buscarPorId(catalogo, 2)?.nombre)
    }

    @Test
    fun buscarPorIdDevuelveNullSiNoExiste() {
        assertNull(servicio.buscarPorId(catalogo, 99))
    }
}
