package com.example.activacorp.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.activacorp.model.Contacto
import com.example.activacorp.model.Oportunidad
import com.example.activacorp.model.Organizacion
import com.example.activacorp.model.Seguimiento
import com.example.activacorp.model.Usuario

@Database(
    entities = [
        Usuario::class,
        Organizacion::class,
        Contacto::class,
        Oportunidad::class,
        Seguimiento::class
    ],
    version = 1
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun usuarioDao(): UsuarioDao
    abstract fun organizacionDao(): OrganizacionDao
    abstract fun contactoDao(): ContactoDao
    abstract fun oportunidadDao(): OportunidadDao
    abstract fun seguimientoDao(): SeguimientoDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {

            return INSTANCE ?: synchronized(this) {

                Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "activacorp.db"
                )
                    .build()
                    .also {
                        INSTANCE = it
                    }
            }
        }
    }
}
