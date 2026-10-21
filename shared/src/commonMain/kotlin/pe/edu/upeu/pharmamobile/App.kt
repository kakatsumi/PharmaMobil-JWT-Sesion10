package pe.edu.upeu.pharmamobile

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.automirrored.outlined.ReceiptLong
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.outlined.Group
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Inventory2
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.foundation.layout.Box
import androidx.compose.ui.Alignment
import org.koin.compose.koinInject
import androidx.compose.runtime.LaunchedEffect
import pe.edu.upeu.pharmamobile.presentation.sesion.LoginScreen
import pe.edu.upeu.pharmamobile.presentation.sesion.SesionViewModel
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import pe.edu.upeu.pharmamobile.navigation.Screen
import pe.edu.upeu.pharmamobile.navigation.tituloPantalla
import pe.edu.upeu.pharmamobile.domain.model.Producto
import pe.edu.upeu.pharmamobile.presentation.cliente.ClientesScreen
import pe.edu.upeu.pharmamobile.presentation.inicio.InicioScreen
import pe.edu.upeu.pharmamobile.presentation.pedido.PedidosScreen
import pe.edu.upeu.pharmamobile.presentation.producto.ProductosScreenConTabs
import pe.edu.upeu.pharmamobile.presentation.producto.CatalogoViewModel
import pe.edu.upeu.pharmamobile.ui.theme.PharmaMobileTheme

private data class ItemNav(val pantalla: Screen, val label: String, val iconoLleno: androidx.compose.ui.graphics.vector.ImageVector, val iconoContorno: androidx.compose.ui.graphics.vector.ImageVector)

private val itemsNav = listOf(
    ItemNav(Screen.Inicio, "Inicio", Icons.Filled.Home, Icons.Outlined.Home),
    ItemNav(Screen.Productos, "Productos", Icons.Filled.Inventory2, Icons.Outlined.Inventory2),
    ItemNav(Screen.Clientes, "Clientes", Icons.Filled.Group, Icons.Outlined.Group),
    ItemNav(Screen.Pedidos, "Ventas", Icons.AutoMirrored.Filled.ReceiptLong, Icons.AutoMirrored.Outlined.ReceiptLong)
)

private fun clavePantalla(pantalla: Screen): String = when (pantalla) {
    is Screen.Inicio -> "inicio"
    is Screen.Productos -> "productos"
    is Screen.Clientes -> "clientes"
    is Screen.Pedidos -> "pedidos"
}

private fun pantallaDesdeClave(clave: String): Screen = when (clave) {
    "productos" -> Screen.Productos
    "clientes" -> Screen.Clientes
    "pedidos" -> Screen.Pedidos
    else -> Screen.Inicio
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview
fun App(modoDemostracion: Boolean = false, onConfigurarServidor: (() -> Unit)? = null) {
    var clavePantallaActual by rememberSaveable { mutableStateOf("inicio") }
    var temaOscuro by rememberSaveable { mutableStateOf(false) }
    val catalogoVm: CatalogoViewModel = koinInject()
    val productos = catalogoVm.productos
    val pantallaActual = pantallaDesdeClave(clavePantallaActual)
    val sesionVm: SesionViewModel = koinInject()
    val auth = sesionVm.estado
    LaunchedEffect(Unit) { sesionVm.comprobarSesion() }
    val sesion = auth.sesion

    PharmaMobileTheme(darkTheme = temaOscuro) {
        if (auth.comprobando) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
            return@PharmaMobileTheme
        }
        if (sesion == null) {
            LoginScreen(auth, sesionVm::cambiarUsuario, sesionVm::cambiarClave, sesionVm::iniciarSesion, modoDemostracion, onConfigurarServidor)
            return@PharmaMobileTheme
        }
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(tituloPantalla(pantallaActual)) },
                    actions = {
                        if (onConfigurarServidor != null) androidx.compose.material3.TextButton(onClick = onConfigurarServidor) { Text("Servidor") }
                        Button(onClick = { sesionVm.cerrarSesion(); clavePantallaActual = "inicio" }) { Text("Cerrar sesión") }
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
                    itemsNav.filter { item ->
                        when (item.pantalla) {
                            is Screen.Clientes -> sesion.puede("CLIENTES_LEER")
                            is Screen.Pedidos -> sesion.puede("VENTAS_LEER")
                            else -> true
                        }
                    }.forEach { item ->
                        val seleccionado = pantallaActual == item.pantalla
                        NavigationBarItem(
                            selected = seleccionado,
                            onClick = { clavePantallaActual = clavePantalla(item.pantalla) },
                            icon = { Icon(if (seleccionado) item.iconoLleno else item.iconoContorno, contentDescription = null) },
                            label = { Text(item.label) }
                        )
                    }
                }
            }
        ) { paddingValues ->
            when (pantallaActual) {
                is Screen.Inicio -> InicioScreen(
                    modifier = Modifier.fillMaxSize().padding(paddingValues),
                    totalProductos = productos.size,
                    productosConStockBajo = productos.count { it.stock in 1..5 },
                    onRegistrarProducto = { clavePantallaActual = clavePantalla(Screen.Productos) },
                    puedeRegistrar = sesion.puede("CATALOGO_ESCRIBIR")
                )
                is Screen.Productos -> ProductosScreenConTabs(
                    modifier = Modifier.fillMaxSize().padding(paddingValues),
                    onProductoRegistrado = {},
                    puedeRegistrar = sesion.puede("CATALOGO_ESCRIBIR")
                )
                is Screen.Clientes -> if (sesion.puede("CLIENTES_LEER")) ClientesScreen(modifier = Modifier.fillMaxSize().padding(paddingValues), puedeEscribir = sesion.puede("CLIENTES_ESCRIBIR"))
                is Screen.Pedidos -> if (sesion.puede("VENTAS_LEER")) PedidosScreen(modifier = Modifier.fillMaxSize().padding(paddingValues), puedeRegistrar = sesion.puede("VENTAS_REGISTRAR"))
            }
        }
    }
}
