package pe.edu.upeu.pharmamobile.presentation.pedido

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ReceiptLong
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import pe.edu.upeu.pharmamobile.ui.components.EmptyState

@Composable
fun PedidosScreen(modifier: Modifier = Modifier) {
    EmptyState(
        icono = Icons.AutoMirrored.Outlined.ReceiptLong,
        titulo = "Pedidos próximamente",
        descripcion = "Aquí podrás crear pedidos, controlar sus estados y gestionar despachos.",
        modifier = modifier
    )
}
