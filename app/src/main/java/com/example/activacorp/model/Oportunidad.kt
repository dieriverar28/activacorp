package com.example.activacorp.model

import androidx.room3.Entity
import androidx.room3.ForeignKey
import androidx.room3.Index
import androidx.room3.PrimaryKey

@Entity(
    tableName = "oportunidades",
    foreignKeys = [
        ForeignKey(
            entity = Organizacion::class,
            parentColumns = ["id"],
            childColumns = ["organizacionId"]
        ),
        ForeignKey(
            entity = Contacto::class,
            parentColumns = ["id"],
            childColumns = ["contactoId"]
        ),
        ForeignKey(
            entity = Usuario::class,
            parentColumns = ["id"],
            childColumns = ["responsableId"]
        ),
        ForeignKey(
            entity = Usuario::class,
            parentColumns = ["id"],
            childColumns = ["creadoPorId"]
        )
    ],
    indices = [
        Index("organizacionId"),
        Index("contactoId"),
        Index("responsableId"),
        Index("creadoPorId")
    ]
)
data class Oportunidad(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val nombre: String,
    val tipoEvento: String,
    // Formato AAAA-MM-DD
    val fechaEvento: String,
    val ubicacion: String,
    val comuna: String,
    val publicoEsperado: Int,
    // Se guarda el nombre del enum Prioridad
    val prioridad: String,
    // Se guarda el nombre del enum EstadoOportunidad
    val estado: String,
    val observaciones: String = "",
    val organizacionId: Int,
    val contactoId: Int? = null,
    // Ejecutivo responsable (null mientras no esté asignada)
    val responsableId: Int? = null,
    val creadoPorId: Int,
    val fechaCreacion: String,
    // URI de la foto del evento guardada en el almacenamiento interno (opcional)
    val fotoUri: String? = null
)
