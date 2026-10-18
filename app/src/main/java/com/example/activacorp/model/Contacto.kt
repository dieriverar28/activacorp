package com.example.activacorp.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "contactos",
    foreignKeys = [
        ForeignKey(
            entity = Organizacion::class,
            parentColumns = ["id"],
            childColumns = ["organizacionId"]
        )
    ],
    indices = [Index("organizacionId")]
)
data class Contacto(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val organizacionId: Int,
    val nombre: String,
    val cargo: String,
    val correo: String,
    val telefono: String
)
