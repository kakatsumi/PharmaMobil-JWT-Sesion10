package pe.edu.upeu.pharmamobile.presentation.cliente

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Group
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import pe.edu.upeu.pharmamobile.ui.components.EmptyState

@Composable
fun ClientesScreen(modifier: Modifier = Modifier) {
    EmptyState(
        icono = Icons.Outlined.Group,
        titulo = "Clientes próximamente",
        descripcion = "Aquí podrás registrar clientes y consultar su historial de compras.",
        modifier = modifier
    )
}
