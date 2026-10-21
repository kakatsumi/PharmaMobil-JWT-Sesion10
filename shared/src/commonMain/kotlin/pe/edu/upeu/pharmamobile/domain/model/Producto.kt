package pe.edu.upeu.pharmamobile.domain.model

data class Producto(
    val id: Long,
    val nombre: String,
    val precio: Double,
    var stock: Int,
    val categoriaId: Long? = null,
    val estado: Boolean = true,
    val pendiente: Boolean = false
) {
    init {
        require(nombre.isNotBlank()) { "El nombre del producto no puede estar vacio" }
        require(precio > 0) { "El precio debe ser mayor que 0" }
        require(stock >= 0) { "El stock no puede ser negativo" }
    }

    fun verificarStock(cantidad: Int): Boolean = stock >= cantidad

    fun estaDisponible(): Boolean = stock > 0

    fun valorInventario(): Double = precio * stock

    fun disminuirStock(cantidad: Int) {
        require(cantidad > 0) { "La cantidad a disminuir debe ser mayor que 0" }
        require(verificarStock(cantidad)) { "Stock insuficiente para el producto: $nombre" }
        stock -= cantidad
    }

    /** Patron copy(): devuelve una nueva instancia con el stock aumentado, sin mutar el original. */
    fun reabastecer(cantidad: Int): Producto {
        require(cantidad > 0) { "La cantidad a reabastecer debe ser mayor que 0" }
        return copy(stock = stock + cantidad)
    }
}
