package com.example.lienzocraft

enum class EstadoProyecto(val nombre: String) {
    PUBLICADO("PUBLICADO"),
    BORRADOR("BORRADOR"),
    PROGRAMADO("PROGRAMADO")
}

data class Proyecto(
    var id: Int,
    var titulo: String,
    var redesSociales: MutableList<String>,
    var estado: EstadoProyecto,
    var colorHex: String,
    var isGrandeEnTodos: Boolean = false,
    var texto: String = "",
    var fechaEvento: String = "", // Formato: DD/MM/AAAA
    var horaEvento: String = "" // Formato: HH:MM
) {
    val redSocial: String
        get() = if (redesSociales.isEmpty()) "NINGUNA" else redesSociales.joinToString(", ").uppercase()
}