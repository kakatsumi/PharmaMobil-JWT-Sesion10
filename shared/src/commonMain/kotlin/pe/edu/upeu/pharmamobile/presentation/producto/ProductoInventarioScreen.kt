package pe.edu.upeu.pharmamobile.presentation.producto

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import org.koin.compose.koinInject
import pe.edu.upeu.pharmamobile.domain.model.Producto
import pe.edu.upeu.pharmamobile.ui.components.ProductCard
import pe.edu.upeu.pharmamobile.ui.theme.PharmaDimens

private val titulosTabs = listOf("Activos", "Inactivos", "Bajo stock")
private fun filtrarPorTab(tab: Int): List<ProductoInventario> = when (tab) {
    0 -> productosSimulados.filter { it.activo && !esBajoStock(it) }
    1 -> productosSimulados.filter { !it.activo }
    else -> productosSimulados.filter { esBajoStock(it) }
}

/** Each destination owns one vertical scroll area. */
@Composable
fun ProductosScreenConTabs(
    modifier: Modifier = Modifier,
    onProductoRegistrado: (Producto) -> Unit = {},
    puedeRegistrar: Boolean = true
) {
    var tabSeleccionada by rememberSaveable { mutableStateOf(0) }
    var mostrarFormulario by rememberSaveable { mutableStateOf(false) }
    val catalogo: CatalogoViewModel = koinInject()
    LaunchedEffect(Unit) { catalogo.cargar() }
    if (mostrarFormulario && puedeRegistrar) {
        ProductoScreen(
            modifier = modifier,
            categorias = catalogo.categorias,
            cargando = catalogo.cargando,
            mensajeServidor = catalogo.mensaje,
            onVolver = { mostrarFormulario = false },
            onProductoRegistrado = { producto, categoriaId, limpiar ->
                catalogo.registrar(producto, categoriaId) {
                    onProductoRegistrado(producto)
                    limpiar()
                    mostrarFormulario = false
                }
            }
        )
        return
    }
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(PharmaDimens.screenPadding),
        verticalArrangement = Arrangement.spacedBy(PharmaDimens.md)
    ) {
        item { Text("Catálogo", style = MaterialTheme.typography.headlineSmall) }
        if (puedeRegistrar) item {
            Button(onClick = { catalogo.limpiarMensaje(); mostrarFormulario = true }, modifier = Modifier.fillMaxWidth()) {
                Text("Registrar producto")
            }
        }
        if (catalogo.cargando) item { Text("Cargando productos…") }
        item {
            TextButton(onClick = catalogo::cargar, enabled = !catalogo.cargando) { Text("Actualizar catálogo") }
        }
        catalogo.mensaje?.let { mensaje ->
            item { Text(mensaje, color = if (catalogo.exito) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error) }
        }
        items(catalogo.productos) { Text(it, style = MaterialTheme.typography.bodyLarge) }
        if (!catalogo.cargando && catalogo.productos.isEmpty()) item { Text("No hay productos disponibles.") }
        item { HorizontalDivider() }
        item { Text("Inventario de ejemplo", style = MaterialTheme.typography.titleMedium) }
        item {
            PrimaryTabRow(selectedTabIndex = tabSeleccionada) {
                titulosTabs.forEachIndexed { index, titulo ->
                    Tab(selected = tabSeleccionada == index, onClick = { tabSeleccionada = index }, text = { Text(titulo) })
                }
            }
        }
        val lista = filtrarPorTab(tabSeleccionada)
        if (lista.isEmpty()) item { Text("No hay productos en esta categoría.") }
        items(lista) { producto ->
            ProductCard(nombre = producto.nombre, descripcion = if (producto.activo) "Activo" else "Inactivo", precio = producto.precio, stock = producto.stock)
        }
    }
}
