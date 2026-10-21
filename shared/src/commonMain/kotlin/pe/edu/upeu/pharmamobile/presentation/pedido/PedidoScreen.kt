package pe.edu.upeu.pharmamobile.presentation.pedido

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import org.koin.compose.koinInject
import pe.edu.upeu.pharmamobile.data.repository.DetalleVentaRequestDto
import pe.edu.upeu.pharmamobile.data.repository.VentaRequestDto
import pe.edu.upeu.pharmamobile.ui.theme.PharmaDimens

@Composable
fun PedidosScreen(modifier: Modifier = Modifier, puedeRegistrar: Boolean = false) {
    val vm: VentaViewModel = koinInject()
    var mostrarFormulario by remember { mutableStateOf(false) }
    var clienteId by remember { mutableStateOf("") }
    var productoId by remember { mutableStateOf("") }
    var cantidad by remember { mutableStateOf("") }
    var errorLocal by remember { mutableStateOf<String?>(null) }
    LaunchedEffect(Unit) { vm.cargar() }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(PharmaDimens.screenPadding),
        verticalArrangement = Arrangement.spacedBy(PharmaDimens.sm)
    ) {
        item { Text("Ventas de PharmaSoft") }
        if (puedeRegistrar) item { Button(onClick = { mostrarFormulario = true }) { Text("Registrar venta") } }
        item { TextButton(onClick = vm::cargar, enabled = !vm.cargando) { Text("Actualizar") } }
        if (vm.cargando) item { Text("Cargando…") }
        vm.mensaje?.let { item { Text(it) } }
        if (mostrarFormulario && puedeRegistrar) {
            item { OutlinedTextField(clienteId, { clienteId = it }, label = { Text("ID de cliente") }, modifier = Modifier.fillMaxWidth()) }
            item { OutlinedTextField(productoId, { productoId = it }, label = { Text("ID de producto") }, modifier = Modifier.fillMaxWidth()) }
            item { OutlinedTextField(cantidad, { cantidad = it }, label = { Text("Cantidad") }, modifier = Modifier.fillMaxWidth()) }
            errorLocal?.let { item { Text(it) } }
            item {
                Button(onClick = {
                    val c = clienteId.toLongOrNull()
                    val p = productoId.toLongOrNull()
                    val n = cantidad.toIntOrNull()
                    if (c == null || c <= 0 || p == null || p <= 0 || n == null || n <= 0) {
                        errorLocal = "Ingresa IDs positivos y una cantidad mayor que cero."
                    } else {
                        vm.registrar(VentaRequestDto(c, listOf(DetalleVentaRequestDto(p, n)))) {
                            mostrarFormulario = false
                            clienteId = ""; productoId = ""; cantidad = ""
                        }
                    }
                }, enabled = !vm.cargando) { Text("Guardar venta") }
                TextButton(onClick = { mostrarFormulario = false }) { Text("Cancelar") }
            }
        }
        items(vm.ventas) { venta ->
            Card(modifier = Modifier.fillMaxWidth()) {
                Text("Venta #${venta.id} · ${venta.clienteNombre}")
                Text("Total S/ ${venta.total} · ${venta.estado}")
                Text(venta.fecha)
            }
        }
        if (!vm.cargando && vm.ventas.isEmpty()) item { Text("No hay ventas registradas.") }
    }
}
