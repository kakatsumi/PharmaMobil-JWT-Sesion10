package pe.edu.upeu.pharmamobile.domain

import pe.edu.upeu.pharmamobile.demo.mostrarResultado
import pe.edu.upeu.pharmamobile.domain.model.Producto
import pe.edu.upeu.pharmamobile.domain.result.ResultadoProductos
import pe.edu.upeu.pharmamobile.domain.service.ProductoService
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ResultadoProductosTest {

    private val servicio = ProductoService()

    private fun producto(nombre: String, stock: Int) =
        Producto(id = 1, nombre = nombre, precio = 5.0, stock = stock)

    @Test
    fun servicioDevuelveErrorSiListaVacia() {
        val resultado = servicio.obtenerProductos(emptyList())
        assertTrue(resultado is ResultadoProductos.Error)
    }

    @Test
    fun servicioDevuelveExitoConProductos() {
        val resultado = servicio.obtenerProductos(listOf(producto("Ibuprofeno", 5)))
        assertTrue(resultado is ResultadoProductos.Exito)
        assertEquals(1, (resultado as ResultadoProductos.Exito).productos.size)
    }

    @Test
    fun productosDisponiblesFiltraSinStock() {
        val lista = listOf(producto("Con stock", 3), producto("Sin stock", 0))
        val resultado = servicio.productosDisponibles(lista)
        assertTrue(resultado is ResultadoProductos.Exito)
        assertEquals(1, (resultado as ResultadoProductos.Exito).productos.size)
    }

    @Test
    fun mostrarResultadoDeError() {
        val texto = mostrarResultado(ResultadoProductos.Error("fallo"))
        assertEquals("Error: fallo", texto)
    }

    @Test
    fun mostrarResultadoDeExito() {
        val texto = mostrarResultado(ResultadoProductos.Exito(listOf(producto("Amoxicilina", 8))))
        assertTrue(texto.contains("Productos encontrados: 1"))
        assertTrue(texto.contains("Amoxicilina"))
    }
}
