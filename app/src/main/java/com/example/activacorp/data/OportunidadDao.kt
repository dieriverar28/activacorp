package com.example.activacorp.data

import androidx.room3.Dao
import androidx.room3.Insert
import androidx.room3.Query
import com.example.activacorp.model.Oportunidad
import com.example.activacorp.model.OportunidadConDatos
import kotlinx.coroutines.flow.Flow

// Consulta base: une la oportunidad con su organización y con su responsable (si tiene)
private const val SELECT_CON_DATOS = """
    SELECT o.id AS id,
           o.nombre AS nombre,
           o.tipoEvento AS tipoEvento,
           o.fechaEvento AS fechaEvento,
           o.ubicacion AS ubicacion,
           o.comuna AS comuna,
           o.publicoEsperado AS publicoEsperado,
           o.prioridad AS prioridad,
           o.estado AS estado,
           o.observaciones AS observaciones,
           o.fotoUri AS fotoUri,
           o.organizacionId AS organizacionId,
           org.nombre AS organizacionNombre,
           o.responsableId AS responsableId,
           u.nombre AS responsableNombre
    FROM oportunidades o
    INNER JOIN organizaciones org ON org.id = o.organizacionId
    LEFT JOIN usuarios u ON u.id = o.responsableId
"""

@Dao
interface OportunidadDao {

    @Insert
    suspend fun insertar(oportunidad: Oportunidad): Long

    @Query("SELECT * FROM oportunidades WHERE id = :id")
    suspend fun obtenerPorId(id: Int): Oportunidad?

    @Query(SELECT_CON_DATOS + " ORDER BY o.fechaEvento")
    fun observarTodas(): Flow<List<OportunidadConDatos>>

    @Query(SELECT_CON_DATOS + " WHERE o.responsableId = :usuarioId ORDER BY o.fechaEvento")
    fun observarPorResponsable(usuarioId: Int): Flow<List<OportunidadConDatos>>

    @Query(SELECT_CON_DATOS + " WHERE o.id = :id")
    fun observarPorId(id: Int): Flow<OportunidadConDatos?>

    @Query("UPDATE oportunidades SET estado = :estado WHERE id = :id")
    suspend fun actualizarEstado(id: Int, estado: String)

    @Query("UPDATE oportunidades SET responsableId = :responsableId WHERE id = :id")
    suspend fun actualizarResponsable(id: Int, responsableId: Int)
}
