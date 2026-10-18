package com.example.activacorp.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "seguimientos",
    foreignKeys = [
        ForeignKey(
            entity = Oportunidad::class,
            parentColumns = ["id"],
            childColumns = ["oportunidadId"]
        ),
        ForeignKey(
            entity = Usuario::class,
            parentColumns = ["id"],
            childColumns = ["usuarioId"]
        )
    ],
    indices = [
        Index("oportunidadId"),
        Index("usuarioId")
    ]
)
data class Seguimiento(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val oportunidadId: Int,
    val usuarioId: Int,
    // Formato AAAA-MM-DD HH:mm
    val fecha: String,
    // REGISTRO, CAMBIO_ESTADO o ASIGNACION
    val accion: String,
    val descripcion: String,
    val estadoAnterior: String? = null,
    val estadoNuevo: String? = null,
    val proximaFecha: String? = null
)
