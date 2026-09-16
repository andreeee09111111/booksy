package com.example.booksy.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.booksy.Book
import com.example.booksy.LibrosViewModel
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.imePadding

private val categoriasEditar = listOf("Fantasía", "Realismo mágico", "No ficción", "Misterio", "Clásico", "Romance", "Juvenil")
private val AmarilloEstrella = Color(0xFFFFC107)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditarLibroScreen(
    libroId: String,
    onGuardado: () -> Unit,
    onEliminado: () -> Unit,
    viewModel: LibrosViewModel = hiltViewModel()
) {
    var libroOriginal by remember { mutableStateOf<Book?>(null) }
    var titulo by remember { mutableStateOf("") }
    var autor by remember { mutableStateOf("") }
    var categoria by remember { mutableStateOf(categoriasEditar.first()) }
    var expandidoCategoria by remember { mutableStateOf(false) }
    var calificacion by remember { mutableStateOf(0) }
    var sinopsis by remember { mutableStateOf("") }
    var portadaUrl by remember { mutableStateOf("") }
    var mostrarConfirmarEliminar by remember { mutableStateOf(false) }
    val guardando by viewModel.guardando.collectAsState()

    LaunchedEffect(libroId) {
        viewModel.obtenerLibroPorId(libroId) { libro ->
            if (libro != null) {
                libroOriginal = libro
                titulo = libro.titulo
                autor = libro.autor
                categoria = libro.categoria.ifBlank { categoriasEditar.first() }
                calificacion = libro.calificacion.toInt()
                sinopsis = libro.sinopsis
                portadaUrl = libro.portadaUrl
            }
        }
    }

    val original = libroOriginal
    if (original == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(FondoClaro)
            .verticalScroll(rememberScrollState())
            .imePadding()
            .padding(16.dp)
    ) {
        Text("Editar libro", style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(16.dp))

        OutlinedTextField(value = titulo, onValueChange = { titulo = it }, label = { Text("Título") }, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(12.dp))
        OutlinedTextField(value = autor, onValueChange = { autor = it }, label = { Text("Autor") }, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(12.dp))

        ExposedDropdownMenuBox(expanded = expandidoCategoria, onExpandedChange = { expandidoCategoria = it }) {
            OutlinedTextField(
                value = categoria, onValueChange = {}, readOnly = true,
                label = { Text("Categoría") },
                modifier = Modifier.fillMaxWidth().menuAnchor()
            )
            ExposedDropdownMenu(expanded = expandidoCategoria, onDismissRequest = { expandidoCategoria = false }) {
                categoriasEditar.forEach { opcion ->
                    DropdownMenuItem(text = { Text(opcion) }, onClick = {
                        categoria = opcion
                        expandidoCategoria = false
                    })
                }
            }
        }

        Spacer(Modifier.height(12.dp))
        Text("Calificación")
        Row {
            for (estrella in 1..5) {
                IconButton(onClick = { calificacion = estrella }) {
                    Icon(
                        imageVector = Icons.Filled.Star,
                        contentDescription = "$estrella estrellas",
                        tint = if (estrella <= calificacion) AmarilloEstrella else Color.LightGray
                    )
                }
            }
        }

        Spacer(Modifier.height(12.dp))
        OutlinedTextField(
            value = portadaUrl, onValueChange = { portadaUrl = it },
            label = { Text("Link de la portada") }, singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(12.dp))
        OutlinedTextField(
            value = sinopsis, onValueChange = { sinopsis = it },
            label = { Text("Sinopsis") }, minLines = 4,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(24.dp))

        Button(
            onClick = {
                viewModel.actualizarLibro(
                    libro = original.copy(
                        titulo = titulo, autor = autor, categoria = categoria,
                        calificacion = calificacion.toDouble(), sinopsis = sinopsis, portadaUrl = portadaUrl
                    ),
                    onListo = onGuardado
                )
            },
            enabled = titulo.isNotBlank() && autor.isNotBlank() && !guardando,
            colors = ButtonDefaults.buttonColors(containerColor = TealPrincipal),
            modifier = Modifier.fillMaxWidth()
        ) {
            if (guardando) CircularProgressIndicator(modifier = Modifier.size(18.dp), color = Color.White)
            else Text("Guardar cambios")
        }

        Spacer(Modifier.height(8.dp))

        OutlinedButton(
            onClick = { mostrarConfirmarEliminar = true },
            colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error),
            modifier = Modifier.fillMaxWidth()
        ) { Text("Eliminar libro") }
    }

    if (mostrarConfirmarEliminar) {
        AlertDialog(
            onDismissRequest = { mostrarConfirmarEliminar = false },
            title = { Text("¿Eliminar libro?") },
            text = { Text("Esta acción no se puede deshacer.") },
            confirmButton = {
                TextButton(onClick = { viewModel.eliminarLibro(libroId, onEliminado) }) {
                    Text("Eliminar", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { mostrarConfirmarEliminar = false }) { Text("Cancelar") }
            }
        )
    }
}