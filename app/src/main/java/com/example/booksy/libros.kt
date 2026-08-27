package com.example.booksy

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "libros")
data class Libro(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val titulo: String,
    val autor: String,
    val esFavorito: Boolean = false,
    val descripcion: String?,
    val calificacion: String,
    val categoria: String
)