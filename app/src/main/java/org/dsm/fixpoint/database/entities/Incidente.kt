package org.dsm.fixpoint.database.entities


data class Incidente(
    val codigo: String, // Auto-generado
    val nombreUsuario: String,
    val areaUsuario: String,
    val descripcion: String,
    val estado: String, // "Sin atender", "Asignado", "Pendiente", "Solucionado"
    val codigoTecnico: String?, // Puede ser nulo
    val nombreEquipo: String // Added new field for equipment code
)