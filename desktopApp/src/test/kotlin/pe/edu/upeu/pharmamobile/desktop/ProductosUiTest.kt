package pe.edu.upeu.pharmamobile.desktop

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.graphics.toAwtImage
import androidx.compose.material3.Surface
import java.io.File
import javax.imageio.ImageIO
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import org.junit.Rule
import org.junit.Test
import org.junit.Assert.assertEquals
import org.koin.core.context.stopKoin
import pe.edu.upeu.pharmamobile.data.local.AlmacenSeguro
import pe.edu.upeu.pharmamobile.di.iniciarDependencias
import pe.edu.upeu.pharmamobile.presentation.producto.ProductosScreenConTabs
import pe.edu.upeu.pharmamobile.ui.theme.PharmaMobileTheme

class ProductosUiTest {
    @get:Rule val compose = createComposeRule()

    private fun captura(nombre: String) {
        val archivo = File("build/reports/ui/$nombre.png")
        archivo.parentFile.mkdirs()
        ImageIO.write(compose.onNodeWithTag("phone").captureToImage().toAwtImage(), "png", archivo)
    }

    @Test fun smallPhoneCanScrollValidateRegisterAndReturnToCatalog() {
        iniciarDependencias(object : AlmacenSeguro {
            override fun guardarToken(token: String) {}
            override fun leerToken(): String? = null
            override fun borrarToken() {}
        }, motorDemostracion(), "http://localhost:8080/", false)
        var registros = 0
        try {
            compose.setContent {
                PharmaMobileTheme {
                    Surface(Modifier.size(360.dp, 480.dp).testTag("phone")) {
                        ProductosScreenConTabs(onProductoRegistrado = { registros++ })
                    }
                }
            }
            compose.waitUntil(5000) { compose.onAllNodesWithText("Paracetamol 500 mg").fetchSemanticsNodes().isNotEmpty() }
            compose.onNodeWithText("Registrar producto").performClick()
            captura("formulario-inicio")
            compose.onNodeWithText("Inventario de ejemplo").assertDoesNotExist()
            compose.onNodeWithTag("phone").performTouchInput { swipeUp() }
            compose.onNodeWithText("Registrar producto").performScrollTo().performClick()
            compose.onNodeWithText("Selecciona una categoría.").assertExists()
            compose.onNodeWithText("Registrar producto").performScrollTo().assertIsDisplayed()
            captura("formulario-desplazado")
            assertEquals(0, registros)
            compose.onNodeWithText("Nombre del producto").performScrollTo().performTextInput("Producto de prueba")
            compose.onNodeWithText("Precio (S/)").performScrollTo().performTextInput("12,50")
            compose.onNodeWithText("Stock").performScrollTo().performTextInput("6")
            compose.onNodeWithText("Seleccionar categoría").performScrollTo().performClick()
            compose.onNodeWithText("Medicamentos").performClick()
            compose.onNodeWithText("Registrar producto").performScrollTo().performClick()
            compose.waitUntil(5000) { registros == 1 }
            compose.onNodeWithText("Nuevo producto").assertDoesNotExist()
            repeat(3) {
                if (compose.onAllNodesWithText("Producto de prueba").fetchSemanticsNodes().isEmpty())
                    compose.onNodeWithTag("phone").performTouchInput { swipeUp() }
            }
            compose.onNodeWithText("Producto de prueba").performScrollTo().assertIsDisplayed()
            captura("catalogo-registrado")
            repeat(3) { compose.onNodeWithTag("phone").performTouchInput { swipeDown() } }
            compose.onNodeWithText("Catálogo guardado").assertExists()
            // Return through the form, then revisit the catalog: the demo server retains the new product.
            compose.onNodeWithText("Registrar producto").performScrollTo().performClick()
            compose.onNodeWithText("← Volver al catálogo").performClick()
            repeat(3) {
                if (compose.onAllNodesWithText("Producto de prueba").fetchSemanticsNodes().isEmpty())
                    compose.onNodeWithTag("phone").performTouchInput { swipeUp() }
            }
            compose.onNodeWithText("Producto de prueba").assertExists()
            repeat(3) { compose.onNodeWithTag("phone").performTouchInput { swipeDown() } }
            compose.onNodeWithText("Sincronizar").performScrollTo().performClick()
            repeat(3) { compose.onNodeWithTag("phone").performTouchInput { swipeUp() } }
            compose.waitUntil(5000) { compose.onAllNodesWithText("Producto de prueba").fetchSemanticsNodes().isNotEmpty() }
            assertEquals(1, registros)
        } finally { stopKoin() }
    }
}
