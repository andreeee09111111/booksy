package com.example.booksy.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun GestionarLibrosScreen(onEditarLibro: (String) -> Unit, onAgregarLibro: () -> Unit, onVolver: () -> Unit) {
    Box(Modifier.fillMaxSize().background(FondoClaro), contentAlignment = Alignment.Center) {
        Text("Gestionar libros")
    }
}