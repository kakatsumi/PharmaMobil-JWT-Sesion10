package pe.edu.upeu.pharmamobile.presentation.producto

import androidx.compose.foundation.background
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import pe.edu.upeu.pharmamobile.domain.model.Producto
import pe.edu.upeu.pharmamobile.data.repository.CategoriaDto
import pe.edu.upeu.pharmamobile.ui.theme.PharmaDimens

@Composable
@Preview
fun ProductoScreen(
    modifier: Modifier = Modifier,
    productoInicial: Producto? = null,
    categorias: List<CategoriaDto> = emptyList(),
    cargando: Boolean = false,
    mensajeServidor: String? = null,
    onVolver: () -> Unit = {},
    onProductoRegistrado: (Producto, Long, () -> Unit) -> Unit = { _, _, _ -> }
) {
    var nombre by rememberSaveable(productoInicial?.id) { mutableStateOf(productoInicial?.nombre.orEmpty()) }
    var precio by rememberSaveable(productoInicial?.id) { mutableStateOf(productoInicial?.precio?.toString().orEmpty()) }
    var stock by rememberSaveable(productoInicial?.id) { mutableStateOf(productoInicial?.stock?.toString().orEmpty()) }
    var categoriaId by rememberSaveable(productoInicial?.id) { mutableStateOf(productoInicial?.categoriaId?.toString().orEmpty()) }
    var mensaje by rememberSaveable { mutableStateOf("") }
    var esExito by rememberSaveable { mutableStateOf(false) }
    var intentoRegistrar by rememberSaveable { mutableStateOf(false) }
    var elegirCategoria by rememberSaveable { mutableStateOf(false) }

    // --- Validacion por campo con conversion segura (*OrNull) ---
    val errorNombre: String? = if (nombre.isBlank()) "El nombre es obligatorio." else null

    val precioNum: Double? = precio.trim().replace(',', '.').toDoubleOrNull()
    val errorPrecio: String? = when {
        precioNum == null -> "Ingrese un precio numerico."
        !precioNum.isFinite() || precioNum <= 0.0 -> "El precio debe ser mayor que cero y finito."
        else -> null
    }

    val stockNum: Int? = stock.trim().toIntOrNull()
    val errorStock: String? = when {
        stockNum == null -> "Ingrese un stock entero."
        stockNum < 0 -> "El stock no puede ser negativo."
        else -> null
    }

    val categoriaNum = categoriaId.toLongOrNull()
    val categoria = categorias.firstOrNull { it.id == categoriaNum && it.estado }
    val formularioValido = errorNombre == null && errorPrecio == null && errorStock == null && categoria != null

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .imePadding()
            .verticalScroll(rememberScrollState())
            .padding(PharmaDimens.screenPadding),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.spacedBy(PharmaDimens.md),
    ) {
        TextButton(onClick = onVolver, enabled = !cargando) { Text("← Volver al catálogo") }
        Text(
            text = if (productoInicial == null) "Nuevo producto" else "Editar producto",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
        )
        Text(
            text = "Registra los datos para mantener actualizado el inventario.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        Card(
            modifier = Modifier.fillMaxWidth().widthIn(max = 560.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        ) {
            Column(
                modifier = Modifier.padding(PharmaDimens.md),
                verticalArrangement = Arrangement.spacedBy(PharmaDimens.sm),
            ) {
                OutlinedTextField(
                    value = nombre,
                    enabled = !cargando,
                    onValueChange = { nombre = it },
                    label = { Text("Nombre del producto") },
                    singleLine = true,
                    isError = intentoRegistrar && errorNombre != null,
                    supportingText = {
                        if (intentoRegistrar && errorNombre != null) Text(errorNombre)
                    },
                    modifier = Modifier.fillMaxWidth(),
                )
                OutlinedTextField(
                    value = precio,
                    enabled = !cargando,
                    onValueChange = { valor ->
                        precio = valor.filter { it.isDigit() || it == '.' || it == ',' }
                    },
                    label = { Text("Precio (S/)") },
                    placeholder = { Text("Ej. 12,50") },
                    singleLine = true,
                    isError = intentoRegistrar && errorPrecio != null,
                    supportingText = {
                        if (intentoRegistrar && errorPrecio != null) Text(errorPrecio)
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth(),
                )
                OutlinedTextField(
                    value = stock,
                    enabled = !cargando,
                    onValueChange = { stock = it },
                    label = { Text("Stock") },
                    singleLine = true,
                    isError = intentoRegistrar && errorStock != null,
                    supportingText = {
                        if (intentoRegistrar && errorStock != null) Text(errorStock)
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                )
                Text("Categoría", style = MaterialTheme.typography.labelLarge)
                Box {
                    OutlinedButton(onClick = { elegirCategoria = true }, enabled = !cargando && categorias.any { it.estado }, modifier = Modifier.fillMaxWidth()) {
                        Text(categoria?.nombre ?: "Seleccionar categoría")
                    }
                    DropdownMenu(expanded = elegirCategoria, onDismissRequest = { elegirCategoria = false }) {
                        categorias.filter { it.estado }.forEach { opcion ->
                            DropdownMenuItem(text = { Text(opcion.nombre) }, onClick = {
                                categoriaId = opcion.id.toString()
                                elegirCategoria = false
                            })
                        }
                    }
                }
                if (intentoRegistrar && categoria == null) Text("Selecciona una categoría.", color = MaterialTheme.colorScheme.error)
                if (!cargando && categorias.none { it.estado }) Text("No hay categorías disponibles. Vuelve al catálogo para recargar.")
                Button(
                    onClick = {
                        intentoRegistrar = true
                        if (formularioValido) {
                            val producto = Producto(
                                id = productoInicial?.id ?: 0,
                                nombre = nombre.trim(),
                                precio = precioNum!!,
                                stock = stockNum!!,
                            )
                            onProductoRegistrado(producto, categoria.id) {
                                nombre = ""
                                precio = ""
                                stock = ""
                                categoriaId = ""
                                intentoRegistrar = false
                            }
                            mensaje = ""
                        } else {
                            mensaje = "Corrija los campos marcados en rojo."
                            esExito = false
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !cargando,
                ) {
                    Text(if (cargando) "Guardando…" else if (productoInicial == null) "Registrar producto" else "Guardar cambios")
                }
            }
        }

        mensajeServidor?.let { Text(it, color = MaterialTheme.colorScheme.error) }
        if (mensaje.isNotEmpty()) {
            Surface(
                color = if (esExito) MaterialTheme.colorScheme.primaryContainer
                        else MaterialTheme.colorScheme.errorContainer,
                shape = MaterialTheme.shapes.medium,
                modifier = Modifier.fillMaxWidth().widthIn(max = 560.dp),
            ) {
                Text(
                    text = mensaje,
                    color = if (esExito) MaterialTheme.colorScheme.onPrimaryContainer
                            else MaterialTheme.colorScheme.onErrorContainer,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth().padding(PharmaDimens.sm),
                )
            }
        }
    }
}
