package com.example.activacorp.data

import androidx.room3.Dao
import androidx.room3.Insert
import androidx.room3.Query
import com.example.activacorp.model.Organizacion
import kotlinx.coroutines.flow.Flow

@Dao
interface OrganizacionDao {

    @Insert
    suspend fun insertar(organizacion: Organizacion): Long

    @Query("SELECT * FROM organizaciones ORDER BY nombre")
    fun observarTodas(): Flow<List<Organizacion>>
}
