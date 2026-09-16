package com.example.booksy.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.booksy.LibrosViewModel
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.imePadding

private const val LIMITE_SINOPSIS = 1200
private val AmarilloEstrella = Color(0xFFFFC107)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AgregarLibroScreen(
    onGuardado: () -> Unit,
    viewModel: LibrosViewModel = hiltViewModel()
) {
    var titulo by remember { mutableStateOf("") }
    var autor by remember { mutableStateOf("") }
    var categoria by remember { mutableStateOf("") }
    var calificacion by remember { mutableStateOf(0) }
    var sinopsis by remember { mutableStateOf("") }
    var portadaUrl by remember { mutableStateOf("") }
    val guardando by viewModel.guardando.collectAsState()

    val formularioValido = titulo.isNotBlank() && autor.isNotBlank() && categoria.isNotBlank() && !guardando

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(FondoClaro)
            .verticalScroll(rememberScrollState())
            .imePadding()
            .padding(16.dp)
    ) {
        Text("Agregar libro", style = MaterialTheme.typography.titleLarge)
        Text("Añade un nuevo libro al catálogo", style = MaterialTheme.typography.bodySmall, color = Color.Gray)

        Spacer(Modifier.height(16.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .background(Color(0xFFE6DED4), shape = RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center
        ) {
            if (portadaUrl.isNotBlank()) {
                AsyncImage(
                    model = portadaUrl,
                    contentDescription = "Vista previa de portada",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                Text("Sin portada", color = Color.Gray)
            }
        }

        Spacer(Modifier.height(12.dp))
        OutlinedTextField(
            value = portadaUrl,
            onValueChange = { portadaUrl = it },
            label = { Text("URL de la portada") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        Text(
            "Pega el enlace directo HTTPS de la imagen. Puedes dejarlo vacío.",
            style = MaterialTheme.typography.labelSmall,
            color = Color.Gray
        )

        Spacer(Modifier.height(12.dp))
        OutlinedTextField(value = titulo, onValueChange = { titulo = it }, label = { Text("Título") }, modifier = Modifier.fillMaxWidth())

        Spacer(Modifier.height(12.dp))
        OutlinedTextField(value = autor, onValueChange = { autor = it }, label = { Text("Autor") }, modifier = Modifier.fillMaxWidth())

        Spacer(Modifier.height(12.dp))
        OutlinedTextField(value = categoria, onValueChange = { categoria = it }, label = { Text("Categoría") }, modifier = Modifier.fillMaxWidth())

        Spacer(Modifier.height(12.dp))
        Text("Calificación (0 a 5)")
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
            value = sinopsis,
            onValueChange = { if (it.length <= LIMITE_SINOPSIS) sinopsis = it },
            label = { Text("Sinopsis") },
            minLines = 4,
            modifier = Modifier.fillMaxWidth()
        )
        Text(
            "${sinopsis.length}/$LIMITE_SINOPSIS",
            style = MaterialTheme.typography.labelSmall,
            color = Color.Gray,
            textAlign = TextAlign.End,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(20.dp))

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
            enabled = formularioValido,
            shape = RoundedCornerShape(50),
            colors = ButtonDefaults.buttonColors(containerColor = TealPrincipal),
            modifier = Modifier.fillMaxWidth().height(52.dp)
        ) {
            if (guardando) CircularProgressIndicator(modifier = Modifier.size(18.dp), color = Color.White)
            else Text("Guardar libro")
        }
    }
}