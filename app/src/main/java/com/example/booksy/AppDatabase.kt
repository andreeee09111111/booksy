package com.example.booksy

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.first

@Database(entities = [Book::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun librosDao(): LibrosDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "libros_database_v2"
                )
                    .fallbackToDestructiveMigrationOnDowngrade() // Solo borra si bajas de versión
                    .build()

                INSTANCE = instance

                // 🔥 Insertar datos si la BD está vacía (SE EJECUTA CADA VEZ QUE SE ABRE LA APP)
                CoroutineScope(Dispatchers.IO).launch {
                    val dao = instance.librosDao()
                    val librosActuales = dao.getAllLibros().first()

                    if (librosActuales.isEmpty()) {
                        insertarLibrosIniciales(dao)
                    }
                }

                instance
            }
        }

        // Función auxiliar para insertar los libros por defecto
        private suspend fun insertarLibrosIniciales(dao: LibrosDao) {
            dao.insertLibro(
                Book(
                    titulo = "El nombre del viento",
                    autor = "Patrick Rothfuss",
                    esFavorito = false,
                    categoria = "Fantasía",
                    calificacion = 4.8f,
                    portada = null
                )
            )
            dao.insertLibro(
                Book(
                    titulo = "El señor de los anillos",
                    autor = "J.R.R. Tolkien",
                    esFavorito = true,
                    categoria = "Fantasía",
                    calificacion = 4.9f,
                    portada = null
                )
            )
            dao.insertLibro(
                Book(
                    titulo = "Cien años de soledad",
                    autor = "Gabriel García Márquez",
                    esFavorito = false,
                    categoria = "Realismo mágico",
                    calificacion = 4.7f,
                    portada = null
                )
            )
        }
    }
}