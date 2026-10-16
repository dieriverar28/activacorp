package com.example.activacorp.data

import androidx.room3.Dao
import androidx.room3.Insert
import androidx.room3.Query
import com.example.activacorp.model.Contacto
import kotlinx.coroutines.flow.Flow

@Dao
interface ContactoDao {

    @Insert
    suspend fun insertar(contacto: Contacto): Long

    @Query("SELECT * FROM contactos WHERE organizacionId = :organizacionId ORDER BY nombre")
    fun observarPorOrganizacion(organizacionId: Int): Flow<List<Contacto>>
}
