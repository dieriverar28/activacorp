package com.example.activacorp.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.activacorp.model.Organizacion
import com.example.activacorp.model.OrganizacionResumen
import kotlinx.coroutines.flow.Flow

@Dao
interface OrganizacionDao {

    @Insert
    suspend fun insertar(organizacion: Organizacion): Long

    @Query("SELECT * FROM organizaciones ORDER BY nombre")
    fun observarTodas(): Flow<List<Organizacion>>

    @Query("SELECT * FROM organizaciones WHERE id = :id")
    fun observarPorId(id: Int): Flow<Organizacion?>

    // Sirve para evitar organizaciones duplicadas
    @Query("SELECT COUNT(*) FROM organizaciones WHERE LOWER(nombre) = LOWER(:nombre)")
    suspend fun contarPorNombre(nombre: String): Int

    @Query(
        """
        SELECT org.id AS id,
               org.nombre AS nombre,
               org.tipo AS tipo,
               org.comuna AS comuna,
               COUNT(c.id) AS totalContactos
        FROM organizaciones org
        LEFT JOIN contactos c ON c.organizacionId = org.id
        GROUP BY org.id
        ORDER BY org.nombre
        """
    )
    fun observarResumen(): Flow<List<OrganizacionResumen>>
}
