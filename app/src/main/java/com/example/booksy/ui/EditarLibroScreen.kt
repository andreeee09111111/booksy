package com.example.booksy.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun EditarLibroScreen(libroId: String, onGuardado: () -> Unit, onEliminado: () -> Unit) {
    Box(Modifier.fillMaxSize().background(FondoClaro), contentAlignment = Alignment.Center) {
        Text("Editar libro - Próximamente")
    }
}