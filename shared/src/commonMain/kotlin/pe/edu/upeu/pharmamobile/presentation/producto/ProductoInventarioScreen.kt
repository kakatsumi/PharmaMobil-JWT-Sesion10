package pe.edu.upeu.pharmamobile.presentation.producto

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.weight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
 * Destino Productos del Reto 02 (guia autonoma): agrega pestanas para clasificar
 * un inventario simulado, y reutiliza [ProductoScreen] (Reto 01) para el registro,
 * sin duplicar su logica de validacion.
 */
@Composable
fun ProductosScreenConTabs(
    modifier: Modifier = Modifier,
    onProductoRegistrado: (Producto) -> Unit = {}
) {
    var tabSeleccionada by remember { mutableStateOf(0) }
    var mostrarFormulario by remember { mutableStateOf(false) }

    if (mostrarFormulario) {
        Column(modifier = modifier.fillMaxSize()) {
            TextButton(onClick = { mostrarFormulario = false }) {
                Text("< Volver al inventario")
            }
            ProductoScreen(onProductoRegistrado = onProductoRegistrado)
        }
        return
    }

    Column(modifier = modifier.fillMaxSize().padding(PharmaDimens.md)) {
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
                modifier = Modifier.padding(top = PharmaDimens.lg),
                style = MaterialTheme.typography.bodyMedium
            )
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f).fillMaxWidth().padding(top = PharmaDimens.sm),
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
        Button(
            onClick = { mostrarFormulario = true },
            modifier = Modifier.fillMaxWidth().padding(top = PharmaDimens.sm)
        ) {
            Text("+ Registrar nuevo producto")
        }
    }
}
