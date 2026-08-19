package pe.edu.upeu.pharmamobile.domain.model

data class Cliente(
    val id: Long,
    val nombre: String,
    val correo: String,
    val telefono: String
) {
    init {
        require(nombre.isNotBlank()) { "El nombre no puede estar vacio" }
        require(correo.contains("@")) { "El correo no es valido" }
        require(telefono.length in 7..15 && telefono.all { it.isDigit() }) {
            "El telefono debe tener entre 7 y 15 digitos"
        }
    }
}
