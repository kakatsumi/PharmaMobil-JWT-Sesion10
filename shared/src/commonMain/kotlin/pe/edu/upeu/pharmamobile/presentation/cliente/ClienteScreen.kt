package pe.edu.upeu.pharmamobile.presentation.cliente

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
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
import pe.edu.upeu.pharmamobile.data.repository.ClienteDto
import pe.edu.upeu.pharmamobile.data.repository.ClienteRequestDto
import pe.edu.upeu.pharmamobile.ui.theme.PharmaDimens

@Composable
fun ClientesScreen(modifier: Modifier = Modifier, puedeEscribir: Boolean = false) {
    val vm: ClienteViewModel = koinInject()
    var editando by remember { mutableStateOf<ClienteDto?>(null) }
    var mostrarFormulario by remember { mutableStateOf(false) }
    var confirmarBaja by remember { mutableStateOf<ClienteDto?>(null) }
    var dni by remember { mutableStateOf("") }
    var nombres by remember { mutableStateOf("") }
    var apellidos by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var telefono by remember { mutableStateOf("") }
    var direccion by remember { mutableStateOf("") }
    var errorLocal by remember { mutableStateOf<String?>(null) }
    LaunchedEffect(Unit) { vm.cargar() }

    fun editar(cliente: ClienteDto?) {
        editando = cliente
        dni = cliente?.dni.orEmpty()
        nombres = cliente?.nombres.orEmpty()
        apellidos = cliente?.apellidos.orEmpty()
        email = cliente?.email.orEmpty()
        telefono = cliente?.telefono.orEmpty()
        direccion = cliente?.direccion.orEmpty()
        errorLocal = null
        mostrarFormulario = true
    }

    confirmarBaja?.let { cliente ->
        AlertDialog(
            onDismissRequest = { confirmarBaja = null },
            title = { Text("Dar de baja cliente") },
            text = { Text("¿Dar de baja a ${cliente.nombres} ${cliente.apellidos}?") },
            confirmButton = { TextButton(onClick = { vm.eliminar(cliente.id); confirmarBaja = null }) { Text("Dar de baja") } },
            dismissButton = { TextButton(onClick = { confirmarBaja = null }) { Text("Cancelar") } }
        )
    }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(PharmaDimens.screenPadding),
        verticalArrangement = Arrangement.spacedBy(PharmaDimens.sm)
    ) {
        item { Text("Clientes") }
        if (puedeEscribir) item { Button(onClick = { editar(null) }) { Text("Registrar cliente") } }
        item { TextButton(onClick = vm::cargar, enabled = !vm.cargando) { Text("Actualizar") } }
        if (vm.cargando) item { Text("Cargando…") }
        vm.mensaje?.let { item { Text(it) } }
        if (mostrarFormulario && puedeEscribir) {
            item { OutlinedTextField(dni, { dni = it }, label = { Text("DNI (8 dígitos)") }, modifier = Modifier.fillMaxWidth()) }
            item { OutlinedTextField(nombres, { nombres = it }, label = { Text("Nombres") }, modifier = Modifier.fillMaxWidth()) }
            item { OutlinedTextField(apellidos, { apellidos = it }, label = { Text("Apellidos") }, modifier = Modifier.fillMaxWidth()) }
            item { OutlinedTextField(email, { email = it }, label = { Text("Correo") }, modifier = Modifier.fillMaxWidth()) }
            item { OutlinedTextField(telefono, { telefono = it }, label = { Text("Teléfono (opcional)") }, modifier = Modifier.fillMaxWidth()) }
            item { OutlinedTextField(direccion, { direccion = it }, label = { Text("Dirección (opcional)") }, modifier = Modifier.fillMaxWidth()) }
            errorLocal?.let { item { Text(it) } }
            item {
                Button(onClick = {
                    if (!dni.matches(Regex("\\d{8}")) || nombres.trim().length < 2 || apellidos.trim().length < 2 ||
                        !email.contains('@') || (telefono.isNotBlank() && !telefono.matches(Regex("\\d{9}")))) {
                        errorLocal = "Revisa DNI, nombres, apellidos, correo y teléfono."
                    } else {
                        vm.guardar(editando?.id, ClienteRequestDto(
                            dni, nombres.trim(), apellidos.trim(), email.trim(),
                            telefono.takeIf { it.isNotBlank() }, direccion.takeIf { it.isNotBlank() }, editando?.estado ?: true
                        )) { mostrarFormulario = false }
                    }
                }, enabled = !vm.cargando) { Text("Guardar") }
                TextButton(onClick = { mostrarFormulario = false }) { Text("Cancelar") }
            }
        }
        items(vm.clientes) { cliente ->
            Card(modifier = Modifier.fillMaxWidth()) {
                Text("${cliente.nombres} ${cliente.apellidos} · ${cliente.dni}")
                Text(cliente.email)
                if (puedeEscribir) {
                    TextButton(onClick = { editar(cliente) }) { Text("Editar") }
                    if (cliente.estado) TextButton(onClick = { confirmarBaja = cliente }) { Text("Dar de baja") }
                }
            }
        }
        if (!vm.cargando && vm.clientes.isEmpty()) item { Text("No hay clientes registrados.") }
    }
}
