package pe.edu.upeu.pharmamobile.presentation.sesion

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import pe.edu.upeu.pharmamobile.domain.model.ErrorSesion
import pe.edu.upeu.pharmamobile.domain.model.Sesion
import pe.edu.upeu.pharmamobile.domain.usecase.CerrarSesionUseCase
import pe.edu.upeu.pharmamobile.domain.usecase.IniciarSesionUseCase
import pe.edu.upeu.pharmamobile.domain.usecase.ObtenerSesionGuardadaUseCase

data class SesionUiState(
    val usuario: String = "",
    val clave: String = "",
    val cargando: Boolean = false,
    val comprobando: Boolean = true,
    val error: String? = null,
    val sesion: Sesion? = null
)

class SesionViewModel(
    private val login: IniciarSesionUseCase,
    private val restaurar: ObtenerSesionGuardadaUseCase,
    private val logout: CerrarSesionUseCase
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    var estado by mutableStateOf(SesionUiState())
        private set

    fun cambiarUsuario(valor: String) { estado = estado.copy(usuario = valor, error = null) }
    fun cambiarClave(valor: String) { estado = estado.copy(clave = valor, error = null) }

    fun comprobarSesion() {
        if (!estado.comprobando) return
        scope.launch {
            try { estado = estado.copy(sesion = restaurar(), comprobando = false) }
            catch (e: CancellationException) { throw e }
            catch (e: ErrorSesion) { estado = estado.copy(comprobando = false, error = e.message) }
            catch (e: Exception) { estado = estado.copy(comprobando = false, error = ErrorSesion.Inesperado.message) }
        }
    }

    fun iniciarSesion() {
        if (estado.cargando) return
        val usuario = estado.usuario.trim()
        val clave = estado.clave
        if (usuario.isBlank() || clave.isBlank()) {
            estado = estado.copy(error = "Ingresa usuario y contraseña")
            return
        }
        estado = estado.copy(cargando = true, error = null)
        scope.launch {
            try {
                val sesion = login(usuario, clave)
                estado = estado.copy(sesion = sesion, clave = "", cargando = false)
            } catch (e: CancellationException) { throw e }
              catch (e: ErrorSesion) { estado = estado.copy(clave = "", cargando = false, error = e.message) }
              catch (e: Exception) { estado = estado.copy(clave = "", cargando = false, error = ErrorSesion.Inesperado.message) }
        }
    }

    fun cerrarSesion(aviso: String? = null) {
        scope.launch {
            try { logout() }
            finally { estado = SesionUiState(comprobando = false, error = aviso) }
        }
    }

    fun sesionExpirada() = cerrarSesion(ErrorSesion.SesionExpirada.message)
    fun sinPermiso() { estado = estado.copy(error = ErrorSesion.SinPermiso.message) }
    fun limpiarError() { estado = estado.copy(error = null) }
}
