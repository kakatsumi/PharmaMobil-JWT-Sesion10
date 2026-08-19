package pe.edu.upeu.pharmamobile.domain.model

data class Cliente(
    val id: Long,
    val nombre: String,
    val correo: String,
    val telefono: String? = null
) {
    init {
        require(nombre.isNotBlank()) { "El nombre no puede estar vacio" }
        require(correo.contains("@")) { "El correo no es valido" }
        if (telefono != null) {
            require(telefono.length in 7..15 && telefono.all { it.isDigit() }) {
                "El telefono debe tener entre 7 y 15 digitos"
            }
        }
    }
}

/** Operador Elvis: devuelve un valor por defecto seguro cuando el telefono es nulo. */
fun verTel(cliente: Cliente): String = cliente.telefono ?: "No registrado"
