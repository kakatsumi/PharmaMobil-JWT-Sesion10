package pe.edu.upeu.pharmamobile.domain.model

sealed class ErrorSesion(message: String) : Exception(message) {
    data object CredencialesInvalidas : ErrorSesion("Credenciales incorrectas")
    data object SesionExpirada : ErrorSesion("La sesión expiró. Inicia sesión nuevamente")
    data object SinPermiso : ErrorSesion("No tienes permiso para esta operación")
    data object SinConexion : ErrorSesion("No se pudo conectar con el servidor")
    data object SolicitudInvalida : ErrorSesion("La solicitud es inválida")
    data object Inesperado : ErrorSesion("Ocurrió un error inesperado")
}
