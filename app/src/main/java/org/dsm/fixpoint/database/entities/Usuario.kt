package org.dsm.fixpoint.database.entities
data class Usuario(
    val idUsuario: String, // Auto-generado para PRIMARY KEY
    val nombre: String,
    val tipo: String, // "jefe", "tecnico", "comun"
    val area : String? = null
)








































