package com.example.activacorp.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.activacorp.model.Usuario
import kotlinx.coroutines.flow.Flow

@Dao
interface UsuarioDao {

    @Insert
    suspend fun insertar(usuario: Usuario): Long

    @Query("SELECT COUNT(*) FROM usuarios")
    suspend fun contar(): Int

    @Query("SELECT * FROM usuarios WHERE correo = :correo AND clave = :clave LIMIT 1")
    suspend fun iniciarSesion(correo: String, clave: String): Usuario?

    @Query("SELECT * FROM usuarios WHERE rol = :rol ORDER BY nombre")
    fun observarPorRol(rol: String): Flow<List<Usuario>>
}
