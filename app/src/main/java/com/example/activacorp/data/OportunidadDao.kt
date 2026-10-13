package com.example.activacorp.data

import androidx.room3.Dao
import androidx.room3.Insert
import androidx.room3.Query
import com.example.activacorp.model.Oportunidad
import kotlinx.coroutines.flow.Flow

// Configuramos el QUÉ VAMOS A HACER
@Dao
interface OportunidadDao {

    @Insert
    suspend fun insertar(oportunidad: Oportunidad)

    @Query("SELECT * FROM oportunidades")
    fun obtenerOportunidades(): Flow<List<Oportunidad>>
}
