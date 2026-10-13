package com.example.activacorp.data

import com.example.activacorp.model.Oportunidad
import kotlinx.coroutines.flow.Flow

// DEFINIMOS EL CÓMO LO VAMOS A HACER
class OportunidadRepository(private val oportunidadDao: OportunidadDao) {

    suspend fun guardar(oportunidad: Oportunidad) {
        oportunidadDao.insertar(oportunidad)
    }

    fun obtenerOportunidades(): Flow<List<Oportunidad>> {
        return oportunidadDao.obtenerOportunidades()
    }
}
