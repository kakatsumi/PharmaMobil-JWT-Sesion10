package pe.edu.upeu.pharmamobile.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColors = lightColorScheme(
    primary = PharmaPrimary,
    onPrimary = PharmaSurface,
    secondary = PharmaPrimaryDark,
    onSecondary = PharmaSurface,
    primaryContainer = PharmaPrimaryContainer,
    onPrimaryContainer = PharmaOnPrimaryContainer,
    secondaryContainer = PharmaSecondaryContainer,
    onSecondaryContainer = PharmaOnSecondaryContainer,
    background = PharmaBackground,
    onBackground = PharmaTextPrimary,
    surface = PharmaSurface,
    onSurface = PharmaTextPrimary,
    surfaceVariant = PharmaSurfaceVariant,
    onSurfaceVariant = PharmaTextSecondary,
    outline = PharmaOutline,
    error = PharmaError,
    onError = PharmaSurface,
    errorContainer = PharmaErrorContainer,
    onErrorContainer = PharmaOnErrorContainer
)

private val DarkColors = darkColorScheme(
    primary = PharmaPrimaryOscuro,
    onPrimary = PharmaBackgroundOscuro,
    secondary = PharmaSecundarioOscuro,
    onSecondary = PharmaBackgroundOscuro,
    primaryContainer = PharmaPrimaryContainerOscuro,
    onPrimaryContainer = PharmaOnPrimaryContainerOscuro,
    secondaryContainer = PharmaSecondaryContainerOscuro,
    onSecondaryContainer = PharmaOnSecondaryContainerOscuro,
    background = PharmaBackgroundOscuro,
    onBackground = PharmaTextPrimaryOscuro,
    surface = PharmaSurfaceOscuro,
    onSurface = PharmaTextPrimaryOscuro,
    surfaceVariant = PharmaSurfaceVariantOscuro,
    onSurfaceVariant = PharmaTextSecondaryOscuro,
    outline = PharmaOutlineOscuro,
    error = PharmaErrorOscuro,
    onError = PharmaBackgroundOscuro,
    errorContainer = PharmaErrorContainerOscuro,
    onErrorContainer = PharmaOnErrorContainerOscuro
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
