package pe.edu.upeu.pharmamobile.presentation.cliente

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import pe.edu.upeu.pharmamobile.data.repository.ClienteDto
import pe.edu.upeu.pharmamobile.data.repository.ClienteRequestDto
import pe.edu.upeu.pharmamobile.data.repository.ClienteRest
import pe.edu.upeu.pharmamobile.domain.model.ErrorSesion
import pe.edu.upeu.pharmamobile.presentation.sesion.SesionViewModel

class ClienteViewModel(private val api: ClienteRest, private val sesion: SesionViewModel) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    var clientes by mutableStateOf<List<ClienteDto>>(emptyList())
        private set
    var cargando by mutableStateOf(false)
        private set
    var mensaje by mutableStateOf<String?>(null)
        private set

    fun cargar() = ejecutar {
        clientes = api.listar()
        mensaje = null
    }

    fun guardar(id: Long?, datos: ClienteRequestDto, alGuardar: () -> Unit) = ejecutar {
        if (id == null) api.registrar(datos) else api.actualizar(id, datos)
        clientes = api.listar()
        mensaje = "Cliente guardado"
        alGuardar()
    }

    fun eliminar(id: Long) = ejecutar {
        api.eliminar(id)
        clientes = api.listar()
        mensaje = "Cliente dado de baja"
    }

    private fun ejecutar(bloque: suspend () -> Unit) {
        if (cargando) return
        cargando = true
        scope.launch {
            try { bloque() }
            catch (e: CancellationException) { throw e }
            catch (e: ErrorSesion) {
                if (e == ErrorSesion.SesionExpirada) sesion.sesionExpirada()
                else mensaje = e.message
            }
            catch (e: Exception) { mensaje = ErrorSesion.Inesperado.message }
            finally { cargando = false }
        }
    }
}
