package pe.edu.upeu.pharmamobile.domain.model

data class Pedido(
    val id: Long,
    val cliente: Cliente,
    val detalle: List<DetallePedido>,
    val estado: EstadoPedido
) {
    init {
        require(detalle.isNotEmpty()) { "El pedido debe tener al menos un detalle" }
    }

    fun total(): Double = detalle.sumOf { it.subtotal() }

    fun cantidadItems(): Int = detalle.sumOf { it.cantidad }
}
