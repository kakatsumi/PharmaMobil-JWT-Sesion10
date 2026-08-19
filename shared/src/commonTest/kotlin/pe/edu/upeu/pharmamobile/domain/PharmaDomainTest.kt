package pe.edu.upeu.pharmamobile.domain

import pe.edu.upeu.pharmamobile.domain.model.Cliente
import pe.edu.upeu.pharmamobile.domain.model.DetallePedido
import pe.edu.upeu.pharmamobile.domain.model.EstadoPedido
import pe.edu.upeu.pharmamobile.domain.model.Pedido
import pe.edu.upeu.pharmamobile.domain.model.Producto
import pe.edu.upeu.pharmamobile.domain.usecase.CalcularTotalPedidoUseCase
import pe.edu.upeu.pharmamobile.domain.usecase.ValidarStockPedidoUseCase
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class PharmaDomainTest {

    private fun producto(stock: Int = 10, precio: Double = 5.0) =
        Producto(id = 1, nombre = "Paracetamol", precio = precio, stock = stock)

    private fun cliente() =
        Cliente(id = 1, nombre = "Ana", correo = "ana@mail.com", telefono = "987654321")

    @Test
    fun productoRechazaPrecioInvalido() {
        assertFailsWith<IllegalArgumentException> {
            Producto(id = 1, nombre = "X", precio = 0.0, stock = 5)
        }
    }

    @Test
    fun productoRechazaStockNegativo() {
        assertFailsWith<IllegalArgumentException> {
            Producto(id = 1, nombre = "X", precio = 1.0, stock = -1)
        }
    }

    @Test
    fun disminuirStockReduceCantidad() {
        val p = producto(stock = 10)
        p.disminuirStock(3)
        assertEquals(7, p.stock)
    }

    @Test
    fun disminuirStockLanzaSiInsuficiente() {
        val p = producto(stock = 2)
        assertFailsWith<IllegalArgumentException> { p.disminuirStock(5) }
    }

    @Test
    fun valorInventarioCalculaBien() {
        val p = producto(stock = 4, precio = 2.5)
        assertEquals(10.0, p.valorInventario())
    }

    @Test
    fun clienteRechazaTelefonoInvalido() {
        assertFailsWith<IllegalArgumentException> {
            Cliente(id = 1, nombre = "Ana", correo = "ana@mail.com", telefono = "abc")
        }
    }

    @Test
    fun pedidoCalculaTotal() {
        val detalle = listOf(DetallePedido(producto(precio = 5.0), 2))
        val pedido = Pedido(1, cliente(), detalle, EstadoPedido.Pendiente)
        assertEquals(10.0, CalcularTotalPedidoUseCase()(pedido))
    }

    @Test
    fun validarStockDetectaFaltantes() {
        val pedido = Pedido(
            1, cliente(),
            listOf(DetallePedido(producto(stock = 1), 5)),
            EstadoPedido.Pendiente
        )
        val faltantes = ValidarStockPedidoUseCase()(pedido)
        assertTrue(faltantes.contains("Paracetamol"))
    }
}
