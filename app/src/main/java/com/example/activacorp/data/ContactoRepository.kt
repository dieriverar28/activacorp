package com.example.activacorp.data

import com.example.activacorp.model.Contacto
import kotlinx.coroutines.flow.Flow

class ContactoRepository(private val contactoDao: ContactoDao) {

    fun observarPorOrganizacion(organizacionId: Int): Flow<List<Contacto>> {
        return contactoDao.observarPorOrganizacion(organizacionId)
    }

    suspend fun existeCorreo(organizacionId: Int, correo: String): Boolean {
        return contactoDao.contarPorCorreo(organizacionId, correo) > 0
    }

    suspend fun crear(contacto: Contacto) {
        contactoDao.insertar(contacto)
    }
}
