package com.example.activacorp.data

import android.content.Context
import androidx.room3.Database
import androidx.room3.Room
import androidx.room3.RoomDatabase
import com.example.activacorp.model.Oportunidad

@Database(
    entities = [Oportunidad::class],
    version = 1
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun oportunidadDao(): OportunidadDao

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
