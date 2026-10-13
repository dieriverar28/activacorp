package com.example.activacorp.model

import androidx.room3.Entity
import androidx.room3.PrimaryKey

@Entity(tableName = "oportunidades")
data class Oportunidad(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val nombre: String,
    val organizacion: String,
    val comuna: String
)
