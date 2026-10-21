package pe.edu.upeu.pharmamobile.navigation

sealed class Screen {
    data object Inicio : Screen()
    data object Productos : Screen()
    data object Clientes : Screen()
    data object Pedidos : Screen()
}

fun tituloPantalla(pantalla: Screen): String = when (pantalla) {
    is Screen.Inicio -> "Inicio"
    is Screen.Productos -> "Productos"
    is Screen.Clientes -> "Clientes"
    is Screen.Pedidos -> "Ventas"
}
