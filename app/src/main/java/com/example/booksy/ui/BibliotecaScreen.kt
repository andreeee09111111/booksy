package com.example.booksy.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.booksy.Book
import com.example.booksy.LibrosViewModel
import com.example.booksy.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BibliotecaScreen(
    onVerLibro: (String) -> Unit,
    viewModel: LibrosViewModel = hiltViewModel()
) {
    val biblioteca by viewModel.biblioteca.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(FondoClaro)
            .padding(16.dp)
    ) {
        Text("Mi Biblioteca", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Text(
            "${biblioteca.size} libros guardados",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray
        )

        Spacer(Modifier.height(20.dp))

        if (biblioteca.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Todavía no has guardado libros", color = Color.Gray)
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(biblioteca, key = { it.id }) { libro ->
                    TarjetaBibliotecaDeslizable(
                        libro = libro,
                        onVerLibro = { onVerLibro(libro.id) },
                        onEliminar = { viewModel.toggleBiblioteca(libro, true) }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TarjetaBibliotecaDeslizable(
    libro: Book,
    onVerLibro: () -> Unit,
    onEliminar: () -> Unit
) {
    val estadoDeslizar = rememberSwipeToDismissBoxState(
        confirmValueChange = { valor ->
            if (valor == SwipeToDismissBoxValue.EndToStart || valor == SwipeToDismissBoxValue.StartToEnd) {
                onEliminar()
                true
            } else false
        }
    )

    SwipeToDismissBox(
        state = estadoDeslizar,
        backgroundContent = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.Red),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_delete),
                    contentDescription = "Eliminar ${libro.titulo} de biblioteca",
                    tint = Color.White
                )
            }
        }
    ) {
        TarjetaLibroConCorazon(
            libro = libro,
            guardado = true,
            onClick = onVerLibro,
            onToggleGuardado = onEliminar
        )
    }
}