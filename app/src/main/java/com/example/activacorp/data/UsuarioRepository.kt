package com.example.activacorp.data

import com.example.activacorp.model.Rol
import com.example.activacorp.model.Usuario
import kotlinx.coroutines.flow.Flow

class UsuarioRepository(private val usuarioDao: UsuarioDao) {

    suspend fun iniciarSesion(correo: String, clave: String): Usuario? {
        return usuarioDao.iniciarSesion(correo, clave)
    }

    fun observarEjecutivos(): Flow<List<Usuario>> {
        return usuarioDao.observarPorRol(Rol.EJECUTIVO.name)
    }
}
