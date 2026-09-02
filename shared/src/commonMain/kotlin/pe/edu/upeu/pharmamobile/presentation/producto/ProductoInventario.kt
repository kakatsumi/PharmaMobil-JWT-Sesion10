package pe.edu.upeu.pharmamobile.presentation.producto

// Modelo simulado (Mock Data) para las pestanas de inventario.
// Se usa un nombre distinto a domain.model.Producto para no chocar con el
// modelo de dominio de la Sesion 2 (que tiene otra forma y otro proposito).
data class ProductoInventario(
    val nombre: String,
    val precio: Double,
    val stock: Int,
    val activo: Boolean
)

// Regla de negocio: stock <= 5 se cataloga como Bajo Stock (solo si el producto esta activo).
fun esBajoStock(producto: ProductoInventario): Boolean = producto.activo && producto.stock <= 5

val productosSimulados = listOf(
    ProductoInventario("Paracetamol", 15.50, 100, activo = true),
    ProductoInventario("Ibuprofeno", 18.90, 50, activo = true),
    ProductoInventario("Amoxicilina", 25.00, 5, activo = true),
    ProductoInventario("Loratadina", 12.50, 0, activo = false),
    ProductoInventario("Diclofenaco", 20.00, 3, activo = true)
)
