package pe.edu.upeu.pharmamobile.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import pe.edu.upeu.pharmamobile.ui.theme.PharmaDimens
import pe.edu.upeu.pharmamobile.ui.theme.PharmaSuccess
import pe.edu.upeu.pharmamobile.ui.theme.PharmaWarning
import kotlin.math.round

@Composable
fun ProductCard(
    nombre: String,
    descripcion: String,
    precio: Double,
    stock: Int,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        onClick = onClick,
        shape = RoundedCornerShape(PharmaDimens.cardRadius),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(PharmaDimens.md)) {
            Text(text = nombre, style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(PharmaDimens.xs))
            Text(
                text = descripcion,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(PharmaDimens.sm))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = if (stock <= 5) "Stock bajo: $stock" else "Stock: $stock",
                    color = if (stock <= 5) PharmaWarning else PharmaSuccess
                )
                Text(text = "S/ ${formatearPrecio(precio)}", style = MaterialTheme.typography.titleMedium)
            }
        }
    }
}

private fun formatearPrecio(precio: Double): String {
    val centavosTotal = round(precio * 100).toLong()
    val entero = centavosTotal / 100
    val centavos = (centavosTotal % 100).let { if (it < 0) -it else it }
    return "$entero.${centavos.toString().padStart(2, '0')}"
}
