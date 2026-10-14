package pe.edu.upeu.pharmamobile.presentation.producto

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import pe.edu.upeu.pharmamobile.data.repository.ProductoCatalogoRest
import pe.edu.upeu.pharmamobile.data.repository.CategoriaDto
import pe.edu.upeu.pharmamobile.domain.model.Producto
import pe.edu.upeu.pharmamobile.domain.model.ErrorSesion
import pe.edu.upeu.pharmamobile.presentation.sesion.SesionViewModel

class CatalogoViewModel(private val repo: ProductoCatalogoRest, private val sesion: SesionViewModel) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    var productos by mutableStateOf<List<String>>(emptyList())
        private set
    var categorias by mutableStateOf<List<CategoriaDto>>(emptyList())
        private set
    var exito by mutableStateOf(false)
        private set
    var mensaje by mutableStateOf<String?>(null)
        private set
    var cargando by mutableStateOf(false)
        private set

    fun registrar(producto: Producto, categoriaId: Long, onExito: () -> Unit = {}) {
        if (cargando) return
        cargando = true
        scope.launch {
            try {
                repo.registrar(producto, categoriaId)
                productos = repo.listar()
                onExito()
                mensaje = "Producto registrado en PharmaSoft"
                exito = true
            } catch (e: CancellationException) { throw e }
              catch (e: ErrorSesion) {
                  exito = false
                  if (e == ErrorSesion.SesionExpirada) sesion.sesionExpirada()
                  else mensaje = e.message
              } catch (e: Exception) { exito = false; mensaje = ErrorSesion.SinConexion.message }
            finally { cargando = false }
        }
    }

    fun cargar() {
        if (cargando) return
        cargando = true
        scope.launch {
            try { productos = repo.listar(); categorias = repo.categorias(); mensaje = null }
            catch (e: CancellationException) { throw e }
            catch (e: ErrorSesion) {
                when (e) {
                    ErrorSesion.SesionExpirada -> sesion.sesionExpirada()
                    ErrorSesion.SinPermiso -> mensaje = e.message
                    else -> mensaje = e.message
                }
            } catch (e: Exception) { exito = false; mensaje = ErrorSesion.SinConexion.message }
            finally { cargando = false }
        }
    }
}
