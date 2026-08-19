package pe.edu.upeu.pharmamobile.domain.usecase

import pe.edu.upeu.pharmamobile.domain.model.Pedido

/** Calcula el total a pagar de un pedido sumando los subtotales de cada detalle. */
class CalcularTotalPedidoUseCase {
    operator fun invoke(pedido: Pedido): Double = pedido.total()
}
