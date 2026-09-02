package pe.edu.upeu.pharmamobile

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
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
                    NavigationDrawerItem(
                        label = { Text("Inicio") },
                        selected = pantallaActual is Screen.Inicio,
                        onClick = { pantallaActual = Screen.Inicio; scope.launch { drawerState.close() } }
                    )
                    NavigationDrawerItem(
                        label = { Text("Productos") },
                        selected = pantallaActual is Screen.Productos,
                        onClick = { pantallaActual = Screen.Productos; scope.launch { drawerState.close() } }
                    )
                    NavigationDrawerItem(
                        label = { Text("Clientes") },
                        selected = pantallaActual is Screen.Clientes,
                        onClick = { pantallaActual = Screen.Clientes; scope.launch { drawerState.close() } }
                    )
                    NavigationDrawerItem(
                        label = { Text("Pedidos") },
                        selected = pantallaActual is Screen.Pedidos,
                        onClick = { pantallaActual = Screen.Pedidos; scope.launch { drawerState.close() } }
                    )
                }
            }
        ) {
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = { Text(tituloPantalla(pantallaActual)) },
                        navigationIcon = {
                            IconButton(onClick = { scope.launch { drawerState.open() } }) {
                                Text("\u2630")
                            }
                        },
                        actions = {
                            Row(modifier = Modifier.padding(end = 8.dp)) {
                                Text("Oscuro", modifier = Modifier.padding(top = 14.dp, end = 4.dp))
                                Switch(checked = temaOscuro, onCheckedChange = { temaOscuro = it })
                            }
                        }
                    )
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
