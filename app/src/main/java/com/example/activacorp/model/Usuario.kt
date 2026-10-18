package com.example.activacorp.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "usuarios")
data class Usuario(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val nombre: String,
    val correo: String,
    val clave: String,
    // Se guarda el nombre del enum Rol (EJECUTIVO o SUPERVISOR)
    val rol: String
)

fun Usuario.obtenerRol(): Rol {
    return Rol.valueOf(rol)
}
