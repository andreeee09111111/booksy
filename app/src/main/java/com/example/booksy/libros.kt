package com.example.booksy

import android.R
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "libros")
data class Book(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val autor: String,
    val esFavorito: Boolean = false,
    val descripcion: String?,
    val categoria: String,
    val calificacion: String
)