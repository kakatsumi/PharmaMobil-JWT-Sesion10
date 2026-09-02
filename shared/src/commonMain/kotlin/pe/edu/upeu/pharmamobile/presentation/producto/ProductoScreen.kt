package pe.edu.upeu.pharmamobile.presentation.producto

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import pe.edu.upeu.pharmamobile.domain.model.Producto

@Composable
@Preview
fun ProductoScreen() {
    // --- Estados del formulario (remember + mutableStateOf) ---
    var nombre by remember { mutableStateOf("") }
    var precio by remember { mutableStateOf("") }
    var stock by remember { mutableStateOf("") }
    var mensaje by remember { mutableStateOf("") }
    var esExito by remember { mutableStateOf(false) }
    // Controla si el usuario ya intento registrar (evita mostrar errores al inicio)
    var intentoRegistrar by remember { mutableStateOf(false) }

    // --- Validacion por campo con conversion segura (*OrNull) ---
    val errorNombre: String? = if (nombre.isBlank()) "El nombre es obligatorio." else null

    val precioNum: Double? = precio.trim().toDoubleOrNull()
    val errorPrecio: String? = when {
        precioNum == null -> "Ingrese un precio numerico."
        precioNum <= 0.0 -> "El precio debe ser mayor que cero."
        else -> null
    }

    val stockNum: Int? = stock.trim().toIntOrNull()
    val errorStock: String? = when {
        stockNum == null -> "Ingrese un stock entero."
        stockNum < 0 -> "El stock no puede ser negativo."
        else -> null
    }

    val formularioValido = errorNombre == null && errorPrecio == null && errorStock == null

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .safeContentPadding()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = "PharmaMobile",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
        )
        Text(
            text = "Registro de Producto",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(Modifier.height(20.dp))

        Card(
            modifier = Modifier.fillMaxWidth().widthIn(max = 480.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                OutlinedTextField(
                    value = nombre,
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
                    onValueChange = { precio = it },
                    label = { Text("Precio") },
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
                Button(
                    onClick = {
                        intentoRegistrar = true
                        if (formularioValido) {
                            val producto = Producto(
                                id = 0,
                                nombre = nombre.trim(),
                                precio = precioNum!!,
                                stock = stockNum!!,
                            )
                            mensaje = "Producto registrado correctamente: ${producto.nombre}"
                            esExito = true
                            // Limpieza automatica del formulario
                            nombre = ""
                            precio = ""
                            stock = ""
                            intentoRegistrar = false
                        } else {
                            mensaje = "Corrija los campos marcados en rojo."
                            esExito = false
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text("REGISTRAR")
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        if (mensaje.isNotEmpty()) {
            Surface(
                color = if (esExito) MaterialTheme.colorScheme.primaryContainer
                        else MaterialTheme.colorScheme.errorContainer,
                shape = MaterialTheme.shapes.medium,
                modifier = Modifier.fillMaxWidth().widthIn(max = 480.dp),
            ) {
                Text(
                    text = mensaje,
                    color = if (esExito) MaterialTheme.colorScheme.onPrimaryContainer
                            else MaterialTheme.colorScheme.onErrorContainer,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth().padding(14.dp),
                )
            }
        }
    }
}
