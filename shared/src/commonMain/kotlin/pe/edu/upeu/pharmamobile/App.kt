package pe.edu.upeu.pharmamobile

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.outlined.Group
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Inventory2
import androidx.compose.material.icons.outlined.ReceiptLong
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import pe.edu.upeu.pharmamobile.navigation.Screen
import pe.edu.upeu.pharmamobile.navigation.tituloPantalla
import pe.edu.upeu.pharmamobile.presentation.cliente.ClientesScreen
import pe.edu.upeu.pharmamobile.presentation.inicio.InicioScreen
import pe.edu.upeu.pharmamobile.presentation.pedido.PedidosScreen
import pe.edu.upeu.pharmamobile.presentation.producto.ProductoScreen
import pe.edu.upeu.pharmamobile.ui.theme.PharmaMobileTheme

private data class ItemNav(val pantalla: Screen, val label: String, val iconoLleno: androidx.compose.ui.graphics.vector.ImageVector, val iconoContorno: androidx.compose.ui.graphics.vector.ImageVector)

private val itemsNav = listOf(
    ItemNav(Screen.Inicio, "Inicio", Icons.Filled.Home, Icons.Outlined.Home),
    ItemNav(Screen.Productos, "Productos", Icons.Filled.Inventory2, Icons.Outlined.Inventory2),
    ItemNav(Screen.Clientes, "Clientes", Icons.Filled.Group, Icons.Outlined.Group),
    ItemNav(Screen.Pedidos, "Pedidos", Icons.Filled.ReceiptLong, Icons.Outlined.ReceiptLong)
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview
fun App() {
    var pantallaActual by remember { mutableStateOf<Screen>(Screen.Inicio) }
    var temaOscuro by remember { mutableStateOf(false) }
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    PharmaMobileTheme(darkTheme = temaOscuro) {
        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                ModalDrawerSheet {
                    Text("PharmaMobile", modifier = Modifier.padding(16.dp))
                    HorizontalDivider()
                    itemsNav.forEach { item ->
                        NavigationDrawerItem(
                            label = { Text(item.label) },
                            icon = { Icon(if (pantallaActual == item.pantalla) item.iconoLleno else item.iconoContorno, contentDescription = null) },
                            selected = pantallaActual == item.pantalla,
                            onClick = { pantallaActual = item.pantalla; scope.launch { drawerState.close() } }
                        )
                    }
                }
            }
        ) {
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = { Text(tituloPantalla(pantallaActual)) },
                        navigationIcon = {
                            IconButton(onClick = { scope.launch { drawerState.open() } }) {
                                Icon(Icons.Filled.Menu, contentDescription = "Abrir menu")
                            }
                        },
                        actions = {
                            IconButton(onClick = { temaOscuro = !temaOscuro }) {
                                Icon(
                                    if (temaOscuro) Icons.Filled.LightMode else Icons.Filled.DarkMode,
                                    contentDescription = if (temaOscuro) "Cambiar a modo claro" else "Cambiar a modo oscuro"
                                )
                            }
                        }
                    )
                },
                bottomBar = {
                    NavigationBar {
                        itemsNav.forEach { item ->
                            val seleccionado = pantallaActual == item.pantalla
                            NavigationBarItem(
                                selected = seleccionado,
                                onClick = { pantallaActual = item.pantalla },
                                icon = { Icon(if (seleccionado) item.iconoLleno else item.iconoContorno, contentDescription = item.label) },
                                label = { Text(item.label) }
                            )
                        }
                    }
                }
            ) { paddingValues ->
                Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
                    when (pantallaActual) {
                        is Screen.Inicio -> InicioScreen()
                        is Screen.Productos -> ProductoScreen()
                        is Screen.Clientes -> ClientesScreen()
                        is Screen.Pedidos -> PedidosScreen()
                    }
                }
            }
        }
    }
}
