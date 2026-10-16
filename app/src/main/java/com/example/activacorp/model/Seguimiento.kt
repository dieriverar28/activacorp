package com.example.activacorp.model

import androidx.room3.Entity
import androidx.room3.ForeignKey
import androidx.room3.Index
import androidx.room3.PrimaryKey

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
