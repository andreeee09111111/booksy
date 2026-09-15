package com.example.booksy.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.booksy.LibrosViewModel

private val categorias = listOf("Fantasía", "Realismo mágico", "No ficción", "Misterio", "Clásico", "Romance", "Juvenil")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AgregarLibroScreen(
    onGuardado: () -> Unit,
    viewModel: LibrosViewModel = hiltViewModel()
) {
    var titulo by remember { mutableStateOf("") }
    var autor by remember { mutableStateOf("") }
    var categoria by remember { mutableStateOf(categorias.first()) }
    var expandidoCategoria by remember { mutableStateOf(false) }
    var calificacion by remember { mutableStateOf(0) }
    var sinopsis by remember { mutableStateOf("") }
    var portadaUrl by remember { mutableStateOf("") }
    val guardando by viewModel.guardando.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(FondoClaro)
            .padding(16.dp)
    ) {
        Text("Agregar libro", style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(16.dp))

        OutlinedTextField(
            value = titulo, onValueChange = { titulo = it },
            label = { Text("Título") }, modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(12.dp))
        OutlinedTextField(
            value = autor, onValueChange = { autor = it },
            label = { Text("Autor") }, modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(12.dp))

        ExposedDropdownMenuBox(
            expanded = expandidoCategoria,
            onExpandedChange = { expandidoCategoria = it }
        ) {
            OutlinedTextField(
                value = categoria,
                onValueChange = {},
                readOnly = true,
                label = { Text("Categoría") },
                modifier = Modifier.fillMaxWidth().menuAnchor()
            )
            ExposedDropdownMenu(
                expanded = expandidoCategoria,
                onDismissRequest = { expandidoCategoria = false }
            ) {
                categorias.forEach { opcion ->
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
                        tint = if (estrella <= calificacion) TealPrincipal else Color.LightGray
                    )
                }
            }
        }

        Spacer(Modifier.height(12.dp))
        OutlinedTextField(
            value = portadaUrl,
            onValueChange = { portadaUrl = it },
            label = { Text("Link de la portada") },
            placeholder = { Text("https://...") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(12.dp))
        OutlinedTextField(
            value = sinopsis,
            onValueChange = { sinopsis = it },
            label = { Text("Sinopsis") },
            placeholder = { Text("Escribe una descripción del libro...") },
            minLines = 4,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(24.dp))

        Button(
            onClick = {
                viewModel.agregarLibro(
                    titulo = titulo,
                    autor = autor,
                    categoria = categoria,
                    calificacion = calificacion.toDouble(),
                    sinopsis = sinopsis,
                    portadaUrl = portadaUrl,
                    onListo = onGuardado
                )
            },
            enabled = titulo.isNotBlank() && autor.isNotBlank() && !guardando,
            colors = ButtonDefaults.buttonColors(containerColor = TealPrincipal),
            modifier = Modifier.fillMaxWidth()
        ) {
            if (guardando) {
                CircularProgressIndicator(modifier = Modifier.size(18.dp), color = Color.White)
            } else {
                Text("Guardar libro")
            }
        }
    }
}