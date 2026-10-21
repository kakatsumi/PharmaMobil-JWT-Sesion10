package pe.edu.upeu.pharmamobile.presentation.pedido

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import pe.edu.upeu.pharmamobile.data.repository.VentaDto
import pe.edu.upeu.pharmamobile.data.repository.VentaRequestDto
import pe.edu.upeu.pharmamobile.data.repository.VentaRest
import pe.edu.upeu.pharmamobile.domain.model.ErrorSesion
import pe.edu.upeu.pharmamobile.presentation.sesion.SesionViewModel

class VentaViewModel(private val api: VentaRest, private val sesion: SesionViewModel) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    var ventas by mutableStateOf<List<VentaDto>>(emptyList())
        private set
    var cargando by mutableStateOf(false)
        private set
    var mensaje by mutableStateOf<String?>(null)
        private set

    fun cargar() = ejecutar { ventas = api.listar(); mensaje = null }
    fun registrar(datos: VentaRequestDto, alGuardar: () -> Unit) = ejecutar {
        api.registrar(datos)
        ventas = api.listar()
        mensaje = "Venta registrada"
        alGuardar()
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
