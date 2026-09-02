package pe.edu.upeu.pharmamobile.presentation.cliente

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ClientesScreen() {
    Column(modifier = Modifier.fillMaxSize().padding(20.dp)) {
        Text("Gestion de Clientes", style = MaterialTheme.typography.headlineSmall)
        Text(
            "Modulo demostrativo. El CRUD completo se implementara en sesiones posteriores.",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(top = 8.dp)
        )
    }
}
