package pe.edu.upeu.pharmamobile.domain.usecase

import pe.edu.upeu.pharmamobile.domain.model.Pedido

/**
 * Valida que haya stock suficiente para cada detalle del pedido.
 * Devuelve la lista de nombres de productos sin stock suficiente.
 * Una lista vacia significa que el pedido es atendible.
 */
class ValidarStockPedidoUseCase {
    operator fun invoke(pedido: Pedido): List<String> =
        pedido.detalle
            .filter { !it.producto.verificarStock(it.cantidad) }
            .map { it.producto.nombre }
}
