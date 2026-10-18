package com.example.activacorp.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.activacorp.model.Contacto
import kotlinx.coroutines.flow.Flow

@Dao
interface ContactoDao {

    @Insert
    suspend fun insertar(contacto: Contacto): Long

    @Query("SELECT * FROM contactos WHERE organizacionId = :organizacionId ORDER BY nombre")
    fun observarPorOrganizacion(organizacionId: Int): Flow<List<Contacto>>

    // Sirve para evitar contactos repetidos dentro de una misma organización
    @Query(
        "SELECT COUNT(*) FROM contactos WHERE organizacionId = :organizacionId AND LOWER(correo) = LOWER(:correo)"
    )
    suspend fun contarPorCorreo(organizacionId: Int, correo: String): Int
}
