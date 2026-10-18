package com.example.activacorp.data

import com.example.activacorp.model.Organizacion
import com.example.activacorp.model.OrganizacionResumen
import kotlinx.coroutines.flow.Flow

class OrganizacionRepository(private val organizacionDao: OrganizacionDao) {

    fun observarTodas(): Flow<List<Organizacion>> {
        return organizacionDao.observarTodas()
    }

    fun observarResumen(): Flow<List<OrganizacionResumen>> {
        return organizacionDao.observarResumen()
    }

    fun observarPorId(id: Int): Flow<Organizacion?> {
        return organizacionDao.observarPorId(id)
    }

    suspend fun existeNombre(nombre: String): Boolean {
        return organizacionDao.contarPorNombre(nombre) > 0
    }

    suspend fun crear(organizacion: Organizacion) {
        organizacionDao.insertar(organizacion)
    }
}
