package com.example.booksy

data class Book(
    val id: String = "",
    val titulo: String = "",
    val autor: String = "",
    val categoria: String = "",
    val calificacion: Double = 0.0,
    val portadaUrl: String = "",
    val sinopsis: String = ""
)
