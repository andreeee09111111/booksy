package com.example.booksy.ui

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.booksy.Book
import com.example.booksy.LibrosViewModel

private val TealPrincipal = Color(0xFF135A58)
private val FondoClaro = Color(0xFFF5EFEB)

@Composable
fun ListaLibrosScreen(
    onAgregarClick: () -> Unit,
    viewModel: LibrosViewModel = hiltViewModel()
) {
    val books by viewModel.allBooks.collectAsState(initial = emptyList())
    var busqueda by remember { mutableStateOf("") }

    val librosFiltrados = if (busqueda.isBlank()) {
        books
    } else {
        books.filter {
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
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Explorar", style = MaterialTheme.typography.headlineSmall)

            Button(
                onClick = onAgregarClick,
                colors = ButtonDefaults.buttonColors(containerColor = TealPrincipal),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.semantics { contentDescription = "Agregar nuevo libro" }
            ) {
                Icon(Icons.Filled.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Agregar libro")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = busqueda,
            onValueChange = { busqueda = it },
            placeholder = { Text("Buscar por título, autor o género...") },
            leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .semantics { contentDescription = "Campo de búsqueda de libros" },
            singleLine = true
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Todos los libros disponibles",
            style = MaterialTheme.typography.bodySmall,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(8.dp))

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
                items(librosFiltrados, key = { it.id }) { book ->
                    LibroCardExpandible(
                        book = book,
                        onFavoriteClick = { viewModel.toggleFavorite(book) }
                    )
                }
            }
        }
    }
}

@Composable
fun LibroCardExpandible(book: Book, onFavoriteClick: () -> Unit) {
    var expandido by remember { mutableStateOf(false) }

    // Animación de entrada/expansión con animateDpAsState
    val imagenAltura by animateDpAsState(
        targetValue = if (expandido) 200.dp else 140.dp,
        label = "alturaImagen"
    )

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize()
            .clickable { expandido = !expandido }
            .semantics {
                contentDescription = "Libro ${book.titulo} de ${book.autor}, calificación ${book.calificacion}. Toca para " +
                        if (expandido) "colapsar" else "expandir"
            }
    ) {
        Box {
            AsyncImage(
                model = book.portada,
                contentDescription = "Portada de ${book.titulo}",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(imagenAltura)
            )
            IconButton(
                onClick = onFavoriteClick,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(4.dp)
                    .background(Color.Black.copy(alpha = 0.3f), shape = RoundedCornerShape(50))
            ) {
                Icon(
                    imageVector = if (book.esFavorito) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                    contentDescription = if (book.esFavorito) "Quitar de favoritos" else "Marcar como favorito",
                    tint = if (book.esFavorito) Color.Red else Color.White
                )
            }
        }
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = book.categoria.uppercase(),
                style = MaterialTheme.typography.labelSmall,
                color = TealPrincipal
            )
            Text(text = book.titulo, style = MaterialTheme.typography.titleMedium, maxLines = if (expandido) Int.MAX_VALUE else 2)
            Text(text = book.autor, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = "★ ${book.calificacion}")

            if (expandido) {
                Spacer(modifier = Modifier.height(8.dp))
                Divider()
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = if (book.esFavorito) "❤️ En tus favoritos" else "🤍 No es favorito",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}