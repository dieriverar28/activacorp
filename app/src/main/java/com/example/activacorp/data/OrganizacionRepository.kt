package com.example.activacorp.data

import com.example.activacorp.model.Organizacion
import kotlinx.coroutines.flow.Flow

class OrganizacionRepository(private val organizacionDao: OrganizacionDao) {

    fun observarTodas(): Flow<List<Organizacion>> {
        return organizacionDao.observarTodas()
    }
}
