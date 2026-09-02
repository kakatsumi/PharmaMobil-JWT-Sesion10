package pe.edu.upeu.pharmamobile.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val AzulPrimario = Color(0xFF1F6FEB)
private val AzulPrimarioOscuro = Color(0xFF90CAF9)
private val Fondo = Color(0xFFF7F9FC)
private val FondoOscuro = Color(0xFF10151C)

private val LightColors = lightColorScheme(
    primary = AzulPrimario,
    secondary = Color(0xFF0E7C66),
    background = Fondo,
    surface = Color.White
)

private val DarkColors = darkColorScheme(
    primary = AzulPrimarioOscuro,
    secondary = Color(0xFF3FB950),
    background = FondoOscuro,
    surface = Color(0xFF1A2029)
)

@Composable
fun PharmaMobileTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColors else LightColors
    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}
