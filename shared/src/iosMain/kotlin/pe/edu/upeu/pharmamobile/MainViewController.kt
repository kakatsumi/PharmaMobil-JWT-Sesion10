package pe.edu.upeu.pharmamobile

import androidx.compose.ui.window.ComposeUIViewController
import io.ktor.client.engine.darwin.Darwin
import org.koin.core.context.GlobalContext
import pe.edu.upeu.pharmamobile.data.local.AlmacenSeguroIos
import pe.edu.upeu.pharmamobile.di.iniciarDependencias

fun MainViewController() = ComposeUIViewController {
    if (GlobalContext.getOrNull() == null) {
        iniciarDependencias(AlmacenSeguroIos(), Darwin.create(), "http://localhost:8080/", true)
    }
    App()
}
