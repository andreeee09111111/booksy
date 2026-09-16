package com.example.booksy.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.booksy.Book
import com.example.booksy.LibrosViewModel
import com.example.booksy.R

@Composable
fun GestionarLibrosScreen(
    onEditarLibro: (String) -> Unit,
    onAgregarLibro: () -> Unit,
    onVolver: () -> Unit,
    viewModel: LibrosViewModel = hiltViewModel()
) {
    val libros by viewModel.allBooks.collectAsState()
    var busqueda by remember { mutableStateOf("") }
    var libroAEliminar by remember { mutableStateOf<Book?>(null) }

    val librosFiltrados = if (busqueda.isBlank()) libros else libros.filter {
        it.titulo.contains(busqueda, ignoreCase = true) ||
                it.autor.contains(busqueda, ignoreCase = true) ||
                it.categoria.contains(busqueda, ignoreCase = true)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(FondoClaro)
            .padding(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onVolver) {
                    Icon(Icons.Filled.ArrowBack, contentDescription = "Volver")
                }
                Text("Gestionar libros", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            }
            Button(
                onClick = onAgregarLibro,
                shape = RoundedCornerShape(50),
                colors = ButtonDefaults.buttonColors(containerColor = TealPrincipal)
            ) {
                Icon(Icons.Filled.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(4.dp))
                Text("Agregar")
            }
        }

        Text(
            "${libros.size} libros en el catálogo",
            style = MaterialTheme.typography.bodySmall,
            color = Color.Gray,
            modifier = Modifier.padding(start = 48.dp)
        )

        Spacer(Modifier.height(16.dp))

        OutlinedTextField(
            value = busqueda,
            onValueChange = { busqueda = it },
            placeholder = { Text("Buscar por título, autor o categoría...") },
            leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
            shape = RoundedCornerShape(16.dp),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(16.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(librosFiltrados, key = { it.id }) { libro ->
                Card(shape = RoundedCornerShape(16.dp), modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            if (libro.categoria.isNotBlank()) {
                                Text(libro.categoria.uppercase(), style = MaterialTheme.typography.labelSmall, color = TealPrincipal)
                            }
                            Text(libro.titulo, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            Text(libro.autor, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                            FilaEstrellas(calificacion = libro.calificacion)
                        }
                        IconButton(onClick = { onEditarLibro(libro.id) }) {
                            Icon(Icons.Filled.Edit, contentDescription = "Editar ${libro.titulo}", tint = TealPrincipal)
                        }
                        IconButton(onClick = { libroAEliminar = libro }) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_delete),
                                contentDescription = "Eliminar ${libro.titulo}",
                                tint = Color(0xFFE74C3C)
                            )
                        }
                    }
                }
            }
        }
    }

    libroAEliminar?.let { libro ->
        AlertDialog(
            onDismissRequest = { libroAEliminar = null },
            title = { Text("¿Eliminar libro?") },
            text = { Text("Esta acción no se puede deshacer. \"${libro.titulo}\" se eliminará del catálogo de forma permanente.") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.eliminarLibro(libro.id) {}
                    libroAEliminar = null
                }) { Text("Eliminar", color = MaterialTheme.colorScheme.error) }
            },
            dismissButton = {
                TextButton(onClick = { libroAEliminar = null }) { Text("Cancelar") }
            }
        )
    }
}