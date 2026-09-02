package pe.edu.upeu.pharmamobile.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColors = lightColorScheme(
    primary = PharmaPrimary,
    onPrimary = PharmaSurface,
    secondary = PharmaPrimaryDark,
    background = PharmaBackground,
    onBackground = PharmaTextPrimary,
    surface = PharmaSurface,
    onSurface = PharmaTextPrimary,
    error = PharmaError
)

private val DarkColors = darkColorScheme(
    primary = PharmaPrimaryOscuro,
    onPrimary = PharmaBackgroundOscuro,
    secondary = PharmaSecundarioOscuro,
    background = PharmaBackgroundOscuro,
    onBackground = PharmaTextPrimaryOscuro,
    surface = PharmaSurfaceOscuro,
    onSurface = PharmaTextPrimaryOscuro,
    error = PharmaError
)

@Composable
fun PharmaMobileTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        typography = PharmaTypography,
        content = content
    )
}
