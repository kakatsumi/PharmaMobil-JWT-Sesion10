package pe.edu.upeu.pharmamobile.presentation.producto

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.weight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import pe.edu.upeu.pharmamobile.domain.model.Producto
import pe.edu.upeu.pharmamobile.ui.components.ProductCard
import pe.edu.upeu.pharmamobile.ui.theme.PharmaDimens

private val titulosTabs = listOf("Activos", "Inactivos", "Bajo stock")

// Filtra el inventario simulado (Mock Data, Paso 4 guia autonoma) segun la pestana:
// 0: Activos (activo && no bajo stock) | 1: Inactivos | 2: Bajo stock (activo && stock <= 5)
private fun filtrarPorTab(tab: Int): List<ProductoInventario> = when (tab) {
    0 -> productosSimulados.filter { it.activo && !esBajoStock(it) }
    1 -> productosSimulados.filter { !it.activo }
    else -> productosSimulados.filter { esBajoStock(it) }
}

/**
 * Destino Productos del Reto 02 (guia autonoma): el formulario de registro
 * (Reto 01, [ProductoScreen]) queda arriba tal cual, sin duplicar su codigo,
 * y debajo se muestra el inventario simulado (Mock Data) organizado en pestanas.
 */
@Composable
fun ProductosScreenConTabs(
    modifier: Modifier = Modifier,
    onProductoRegistrado: (Producto) -> Unit = {}
) {
    var tabSeleccionada by remember { mutableStateOf(0) }

    Column(modifier = modifier.fillMaxSize()) {
        // --- Reto 01: formulario reutilizado, ocupa la mitad superior ---
        Box(modifier = Modifier.weight(0.55f)) {
            ProductoScreen(onProductoRegistrado = onProductoRegistrado)
        }

        HorizontalDivider()

        // --- Reto 02: inventario simulado (Mock Data) con Tabs ---
        Column(modifier = Modifier.weight(0.45f).padding(PharmaDimens.md)) {
            Text("Inventario simulado", style = MaterialTheme.typography.titleMedium)
            TabRow(selectedTabIndex = tabSeleccionada) {
                titulosTabs.forEachIndexed { index, titulo ->
                    Tab(
                        selected = tabSeleccionada == index,
                        onClick = { tabSeleccionada = index },
                        text = { Text(titulo) }
                    )
                }
            }
            val lista = filtrarPorTab(tabSeleccionada)
            if (lista.isEmpty()) {
                Text(
                    "No hay productos en esta categoria.",
                    modifier = Modifier.padding(top = PharmaDimens.sm),
                    style = MaterialTheme.typography.bodyMedium
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth().padding(top = PharmaDimens.sm),
                    verticalArrangement = Arrangement.spacedBy(PharmaDimens.sm)
                ) {
                    items(lista) { producto ->
                        ProductCard(
                            nombre = producto.nombre,
                            descripcion = if (producto.activo) "Activo" else "Inactivo",
                            precio = producto.precio,
                            stock = producto.stock
                        )
                    }
                }
            }
        }
    }
}
