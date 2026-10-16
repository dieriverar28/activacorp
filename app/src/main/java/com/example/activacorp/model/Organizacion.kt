package com.example.activacorp.model

import androidx.room3.Entity
import androidx.room3.PrimaryKey

@Entity(tableName = "organizaciones")
data class Organizacion(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val nombre: String,
    val tipo: String,
    val comuna: String
)
