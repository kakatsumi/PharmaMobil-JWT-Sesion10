package pe.edu.upeu.pharmamobile.presentation.producto

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import pe.edu.upeu.pharmamobile.domain.model.Categoria
import pe.edu.upeu.pharmamobile.domain.repository.ProductoRepository
import pe.edu.upeu.pharmamobile.domain.model.Producto
import pe.edu.upeu.pharmamobile.domain.model.ErrorSesion
import pe.edu.upeu.pharmamobile.presentation.sesion.SesionViewModel

class CatalogoViewModel(private val repo: ProductoRepository, private val sesion: SesionViewModel) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    var productos by mutableStateOf<List<Producto>>(emptyList())
        private set
    var categorias by mutableStateOf<List<Categoria>>(emptyList())
        private set
    var exito by mutableStateOf(false)
        private set
    var mensaje by mutableStateOf<String?>(null)
        private set
    var cargando by mutableStateOf(false)
        private set

    init {
        scope.launch { repo.observar().collect { productos = it } }
        scope.launch { repo.observarCategorias().collect { categorias = it } }
    }

    fun limpiarMensaje() { mensaje = null; exito = false }

    fun registrar(producto: Producto, categoriaId: Long, onExito: () -> Unit = {}) {
        if (cargando) return
        limpiarMensaje()
        cargando = true
        scope.launch {
            try {
                repo.registrar(producto.copy(categoriaId = categoriaId))
                onExito()
                mensaje = "Producto guardado. Comprueba la sincronización con PharmaSoft."
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

    fun actualizar(producto: Producto, categoriaId: Long, onExito: () -> Unit = {}) {
        if (cargando) return
        limpiarMensaje()
        cargando = true
        scope.launch {
            try {
                repo.actualizar(producto.copy(categoriaId = categoriaId))
                onExito()
                mensaje = "Producto actualizado"
                exito = true
            } catch (e: CancellationException) { throw e }
            catch (e: ErrorSesion) {
                if (e == ErrorSesion.SesionExpirada) sesion.sesionExpirada() else mensaje = e.message
            } catch (e: Exception) { mensaje = ErrorSesion.Inesperado.message }
            finally { cargando = false }
        }
    }

    fun eliminar(id: Long) {
        if (cargando) return
        limpiarMensaje()
        cargando = true
        scope.launch {
            try {
                repo.eliminar(id)
                mensaje = "Producto dado de baja"
                exito = true
            } catch (e: CancellationException) { throw e }
            catch (e: ErrorSesion) {
                if (e == ErrorSesion.SesionExpirada) sesion.sesionExpirada() else mensaje = e.message
            } catch (e: Exception) { mensaje = ErrorSesion.Inesperado.message }
            finally { cargando = false }
        }
    }

    fun cargar() {
        if (cargando) return
        limpiarMensaje()
        cargando = true
        scope.launch {
            try { repo.sincronizar(); mensaje = "Catálogo sincronizado"; exito = true }
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
