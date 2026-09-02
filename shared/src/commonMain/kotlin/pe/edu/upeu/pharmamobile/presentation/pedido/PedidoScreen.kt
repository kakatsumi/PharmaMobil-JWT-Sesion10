package pe.edu.upeu.pharmamobile.presentation.pedido

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun PedidosScreen() {
    Column(modifier = Modifier.fillMaxSize().padding(20.dp)) {
        Text("Gestion de Pedidos", style = MaterialTheme.typography.headlineSmall)
        Text(
            "Modulo demostrativo de control de ordenes y despachos.",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(top = 8.dp)
        )
    }
}
