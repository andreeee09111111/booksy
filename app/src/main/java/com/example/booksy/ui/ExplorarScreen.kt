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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.booksy.LibrosViewModel
import com.example.booksy.R

@Composable
fun ExplorarScreen(
    onVerLibro: (String) -> Unit,
    viewModel: LibrosViewModel = hiltViewModel()
) {
    val libros by viewModel.allBooks.collectAsState()
    val biblioteca by viewModel.biblioteca.collectAsState()
    var busqueda by remember { mutableStateOf("") }

    val librosFiltrados = if (busqueda.isBlank()) {
        libros
    } else {
        libros.filter {
            it.titulo.contains(busqueda, ignoreCase = true) ||
                    it.autor.contains(busqueda, ignoreCase = true) ||
                    it.categoria.contains(busqueda, ignoreCase = true)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(FondoClaro)
            .padding(16.dp)
    ) {
        Text("Explorar", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)

        Spacer(Modifier.height(16.dp))

        OutlinedTextField(
            value = busqueda,
            onValueChange = { busqueda = it },
            placeholder = { Text("Buscar por título, autor o género...") },
            leadingIcon = {
                Icon(painter = painterResource(id = R.drawable.ic_search), contentDescription = null)
            },
            shape = RoundedCornerShape(16.dp),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = Color.LightGray,
                focusedBorderColor = TealPrincipal
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(16.dp))
        Text(
            "Todos los libros disponibles",
            style = MaterialTheme.typography.bodySmall,
            color = Color.Gray
        )
        Spacer(Modifier.height(8.dp))

        if (librosFiltrados.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No se encontraron libros")
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(librosFiltrados, key = { it.id }) { libro ->
                    val yaGuardado = biblioteca.any { it.id == libro.id }
                    TarjetaLibroConCorazon(
                        libro = libro,
                        guardado = yaGuardado,
                        onClick = { onVerLibro(libro.id) },
                        onToggleGuardado = { viewModel.toggleBiblioteca(libro, yaGuardado) }
                    )
                }
            }
        }
    }
}