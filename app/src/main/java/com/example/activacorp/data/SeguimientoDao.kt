package com.example.activacorp.data

import androidx.room3.Dao
import androidx.room3.Insert
import androidx.room3.Query
import com.example.activacorp.model.Seguimiento
import com.example.activacorp.model.SeguimientoDetalle
import kotlinx.coroutines.flow.Flow

@Dao
interface SeguimientoDao {

    @Insert
    suspend fun insertar(seguimiento: Seguimiento): Long

    @Query(
        """
        SELECT s.id AS id,
               s.fecha AS fecha,
               s.accion AS accion,
               s.descripcion AS descripcion,
               s.estadoAnterior AS estadoAnterior,
               s.estadoNuevo AS estadoNuevo,
               u.nombre AS usuarioNombre
        FROM seguimientos s
        INNER JOIN usuarios u ON u.id = s.usuarioId
        WHERE s.oportunidadId = :oportunidadId
        ORDER BY s.id DESC
        """
    )
    fun observarPorOportunidad(oportunidadId: Int): Flow<List<SeguimientoDetalle>>
}
