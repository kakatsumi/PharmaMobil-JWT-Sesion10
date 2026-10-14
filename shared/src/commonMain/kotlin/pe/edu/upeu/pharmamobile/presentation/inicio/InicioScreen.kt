package pe.edu.upeu.pharmamobile.presentation.inicio

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.painterResource
import pe.edu.upeu.pharmamobile.ui.theme.PharmaDimens
import pharmamobile.shared.generated.resources.Res
import pharmamobile.shared.generated.resources.pharmamobil_logo
import androidx.compose.foundation.Image

@Composable
fun InicioScreen(
    modifier: Modifier = Modifier,
    totalProductos: Int = 0,
    productosConStockBajo: Int = 0,
    onRegistrarProducto: () -> Unit = {},
    puedeRegistrar: Boolean = true
) {
    Column(
        modifier = modifier.fillMaxSize().padding(PharmaDimens.screenPadding),
        verticalArrangement = Arrangement.spacedBy(PharmaDimens.md)
    ) {
        Image(
            painter = painterResource(Res.drawable.pharmamobil_logo),
            contentDescription = "Logo PharmaMobile",
            modifier = Modifier.height(48.dp)
        )
        Text("Resumen de inventario", style = MaterialTheme.typography.headlineSmall)
        Text(
            "Consulta el estado de tu farmacia de un vistazo.",
            style = MaterialTheme.typography.bodyMedium,
        )
        Column(verticalArrangement = Arrangement.spacedBy(PharmaDimens.sm)) {
            ResumenCard(
                modifier = Modifier.fillMaxWidth(),
                titulo = "Productos",
                valor = totalProductos.toString(),
                icono = Icons.Filled.Inventory2
            )
            ResumenCard(
                modifier = Modifier.fillMaxWidth(),
                titulo = "Stock bajo",
                valor = productosConStockBajo.toString(),
                icono = Icons.Filled.WarningAmber,
                esAlerta = productosConStockBajo > 0
            )
        }
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
        ) {
            Column(modifier = Modifier.padding(PharmaDimens.md)) {
                Text(
                    text = if (totalProductos == 0) "Comienza tu inventario" else "Inventario actualizado",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Spacer(Modifier.height(PharmaDimens.xs))
                Text(
                    text = if (totalProductos == 0) "Registra tu primer producto para visualizar métricas y alertas." else "Revisa los productos con stock bajo antes de realizar nuevos pedidos.",
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Spacer(Modifier.height(PharmaDimens.md))
                if (puedeRegistrar) Button(onClick = onRegistrarProducto) {
                    Icon(Icons.Filled.Add, contentDescription = null)
                    Spacer(Modifier.width(PharmaDimens.xs))
                    Text("Registrar producto")
                }
            }
        }
    }
}

@Composable
private fun ResumenCard(
    titulo: String,
    valor: String,
    icono: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier,
    esAlerta: Boolean = false
) {
    Card(modifier = modifier) {
        Column(modifier = Modifier.padding(PharmaDimens.md)) {
            Icon(
                imageVector = icono,
                contentDescription = null,
                tint = if (esAlerta) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
            )
            Spacer(Modifier.height(PharmaDimens.sm))
            Text(valor, style = MaterialTheme.typography.headlineMedium)
            Text(titulo, style = MaterialTheme.typography.bodyMedium)
        }
    }
}
