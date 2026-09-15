package com.example.booksy.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items as lazyRowItems
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.booksy.Book
import com.example.booksy.LibrosViewModel
import com.example.booksy.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InicioScreen(
    onVerLibro: (String) -> Unit,
    onIrAExplorar: () -> Unit,
    onBuscar: () -> Unit,
    viewModel: LibrosViewModel = hiltViewModel()
) {
    val libros by viewModel.allBooks.collectAsState()
    val biblioteca by viewModel.biblioteca.collectAsState()
    val destacado = libros.firstOrNull()

    val categorias = listOf("Todos") + libros.map { it.categoria }.distinct().filter { it.isNotBlank() }
    var categoriaSeleccionada by remember { mutableStateOf("Todos") }

    val librosFiltrados = if (categoriaSeleccionada == "Todos") libros
    else libros.filter { it.categoria == categoriaSeleccionada }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(FondoClaro)
    ) {
        // Encabezado propio de esta pantalla, con la lupa que abre la búsqueda global
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    "BIENVENIDO DE VUELTA",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.Gray,
                    letterSpacing = 1.sp
                )
                Text("Booksy", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
            }
            IconButton(
                onClick = onBuscar,
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFE6DED4))
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_search),
                    contentDescription = "Buscar libros"
                )
            }
        }

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            // Tarjeta destacada, ocupa las 2 columnas
            if (destacado != null) {
                item(span = { GridItemSpan(2) }) {
                    TarjetaDestacada(libro = destacado, onClick = { onVerLibro(destacado.id) })
                }
            }

            // Chips de categoría, ocupan las 2 columnas
            item(span = { GridItemSpan(2) }) {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    lazyRowItems(categorias) { categoria ->
                        FilterChip(
                            selected = categoriaSeleccionada == categoria,
                            onClick = { categoriaSeleccionada = categoria },
                            label = { Text(categoria) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = TealPrincipal,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }
            }

            // Encabezado de sección, ocupa las 2 columnas
            item(span = { GridItemSpan(2) }) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Lecturas populares", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text("${librosFiltrados.size} libros", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                }
            }

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

@Composable
private fun TarjetaDestacada(libro: Book, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(160.dp)
            .clip(RoundedCornerShape(20.dp))
            .clickable { onClick() }
    ) {
        AsyncImage(
            model = libro.portadaUrl,
            contentDescription = "Portada de ${libro.titulo}",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
                .background(TealPrincipal)
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.35f))
        )
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(16.dp)
        ) {
            if (libro.categoria.isNotBlank()) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .background(Color.White.copy(alpha = 0.25f))
                        .padding(horizontal = 10.dp, vertical = 3.dp)
                ) {
                    Text(libro.categoria, color = Color.White, style = MaterialTheme.typography.labelSmall)
                }
                Spacer(Modifier.height(6.dp))
            }
            Text(libro.titulo, color = Color.White, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Text(libro.autor, color = Color.White.copy(alpha = 0.85f), style = MaterialTheme.typography.bodyMedium)
            Spacer(Modifier.height(4.dp))
            FilaEstrellas(calificacion = libro.calificacion, colorTexto = Color.White)
        }
    }
}

@Composable
fun TarjetaLibroConCorazon(
    libro: Book,
    guardado: Boolean,
    onClick: () -> Unit,
    onToggleGuardado: () -> Unit
) {
    var expandido by remember { mutableStateOf(false) }

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Box {
            AsyncImage(
                model = libro.portadaUrl,
                contentDescription = "Portada de ${libro.titulo}",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
            )
            IconButton(
                onClick = onToggleGuardado,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(6.dp)
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.9f))
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_heart),
                    contentDescription = if (guardado) "Quitar de mi biblioteca" else "Guardar en biblioteca",
                    tint = if (guardado) Color.Red else Color.LightGray,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
        Column(modifier = Modifier.padding(10.dp)) {
            if (libro.categoria.isNotBlank()) {
                Text(libro.categoria.uppercase(), style = MaterialTheme.typography.labelSmall, color = TealPrincipal)
            }
            Text(
                libro.titulo,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                maxLines = if (expandido) Int.MAX_VALUE else 2,
                modifier = Modifier.clickable { expandido = !expandido }
            )
            Text(libro.autor, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
            Spacer(Modifier.height(4.dp))
            FilaEstrellas(calificacion = libro.calificacion, colorTexto = Color.Black)

            if (expandido) {
                Spacer(Modifier.height(6.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_heart),
                        contentDescription = null,
                        tint = if (guardado) Color.Red else Color.LightGray,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        if (guardado) "En tus favoritos" else "No es favorito",
                        style = MaterialTheme.typography.labelSmall,
                        color = if (guardado) Color.Red else Color.Gray
                    )
                }
                if (libro.sinopsis.isNotBlank()) {
                    Spacer(Modifier.height(4.dp))
                    Text(
                        libro.sinopsis,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.DarkGray
                    )
                }
            }
        }
    }
}

@Composable
fun FilaEstrellas(calificacion: Double, colorTexto: Color = Color.Black) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        val llenas = calificacion.toInt().coerceIn(0, 5)
        repeat(5) { index ->
            Icon(
                imageVector = Icons.Filled.Star,
                contentDescription = null,
                tint = if (index < llenas) TealPrincipal else Color.LightGray,
                modifier = Modifier.size(14.dp)
            )
        }
        Spacer(Modifier.width(4.dp))
        Text(calificacion.toString(), style = MaterialTheme.typography.labelSmall, color = colorTexto)
    }
}