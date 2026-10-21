package pe.edu.upeu.pharmamobile.data.local

import pe.edu.upeu.pharmamobile.domain.model.Sesion

interface AlmacenSeguro {
    fun guardarToken(token: String)
    fun leerToken(): String?
    fun borrarToken()
    fun guardarSesionLocal(sesion: Sesion, duracionSegundos: Long) {}
    fun leerSesionLocal(): Sesion? = null
}
