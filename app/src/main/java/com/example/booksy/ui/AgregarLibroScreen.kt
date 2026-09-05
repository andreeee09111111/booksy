package com.example.booksy.ui

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.booksy.Book
import com.example.booksy.LibrosViewModel
import com.example.booksy.R

private val VerdeBotones = Color(0xFF135A58)

@Composable
fun AgregarLibroScreen(
    onGuardado: () -> Unit,
    viewModel: LibrosViewModel = hiltViewModel()
) {
    val context = LocalContext.current

    var titulo by remember { mutableStateOf("") }
    var autor by remember { mutableStateOf("") }
    var categoria by remember { mutableStateOf("") }
    var calificacion by remember { mutableStateOf("") }
    var portadaUri by remember { mutableStateOf<Uri?>(null) }

    // ===== Estados de error para validación en tiempo real =====
    var errorTitulo by remember { mutableStateOf<String?>(null) }
    var errorAutor by remember { mutableStateOf<String?>(null) }
    var errorCalificacion by remember { mutableStateOf<String?>(null) }
    var formularioValido by remember { mutableStateOf(false) }

    // ===== Validación reactiva: se ejecuta cada vez que cambian los campos =====
    LaunchedEffect(titulo) {
        errorTitulo = if (titulo.isNotEmpty() && titulo.trim().isEmpty()) {
            "El título no puede ser solo espacios"
        } else null
    }

    LaunchedEffect(autor) {
        errorAutor = if (autor.isNotEmpty() && autor.trim().isEmpty()) {
            "El autor no puede ser solo espacios"
        } else null
    }

    LaunchedEffect(calificacion) {
        errorCalificacion = when {
            calificacion.isEmpty() -> null
            calificacion.toFloatOrNull() == null -> "Debe ser un número"
            (calificacion.toFloatOrNull() ?: -1f) !in 0f..5f -> "Debe estar entre 0 y 5"
            else -> null
        }
    }

    LaunchedEffect(titulo, autor, errorTitulo, errorAutor, errorCalificacion) {
        formularioValido = titulo.isNotBlank() &&
                autor.isNotBlank() &&
                errorTitulo == null &&
                errorAutor == null &&
                errorCalificacion == null
    }

    val seleccionarImagenLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        uri?.let {
            try {
                context.contentResolver.takePersistableUriPermission(
                    it,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
            } catch (e: SecurityException) { }
            portadaUri = it
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Agregar libro", style = MaterialTheme.typography.headlineSmall)

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp)
                .align(Alignment.CenterHorizontally)
                .semantics { contentDescription = "Vista previa de la portada del libro" },
            contentAlignment = Alignment.Center
        ) {
            AsyncImage(
                model = portadaUri ?: R.drawable.portada_destacado,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }

        Button(
            onClick = { seleccionarImagenLauncher.launch(arrayOf("image/*")) },
            colors = ButtonDefaults.buttonColors(containerColor = VerdeBotones),
            modifier = Modifier.semantics { contentDescription = "Seleccionar imagen de portada" }
        ) {
            Text("Seleccionar portada")
        }

        OutlinedTextField(
            value = titulo,
            onValueChange = { titulo = it },
            label = { Text("Título") },
            isError = errorTitulo != null,
            supportingText = { errorTitulo?.let { Text(it) } },
            modifier = Modifier
                .fillMaxWidth()
                .semantics { contentDescription = "Campo de título del libro" }
        )
        OutlinedTextField(
            value = autor,
            onValueChange = { autor = it },
            label = { Text("Autor") },
            isError = errorAutor != null,
            supportingText = { errorAutor?.let { Text(it) } },
            modifier = Modifier
                .fillMaxWidth()
                .semantics { contentDescription = "Campo de autor del libro" }
        )
        OutlinedTextField(
            value = categoria,
            onValueChange = { categoria = it },
            label = { Text("Categoría") },
            modifier = Modifier
                .fillMaxWidth()
                .semantics { contentDescription = "Campo de categoría del libro" }
        )
        OutlinedTextField(
            value = calificacion,
            onValueChange = { calificacion = it },
            label = { Text("Calificación (0 a 5)") },
            isError = errorCalificacion != null,
            supportingText = { errorCalificacion?.let { Text(it) } },
            modifier = Modifier
                .fillMaxWidth()
                .semantics { contentDescription = "Campo de calificación del libro, de cero a cinco" }
        )

        Button(
            onClick = {
                val nuevoLibro = Book(
                    titulo = titulo.trim(),
                    autor = autor.trim(),
                    categoria = categoria.trim().ifEmpty { "General" },
                    calificacion = (calificacion.toFloatOrNull() ?: 0f).coerceIn(0f, 5f),
                    esFavorito = false,
                    portada = portadaUri?.toString()
                )
                viewModel.insert(nuevoLibro)
                onGuardado()
            },
            enabled = formularioValido,
            colors = ButtonDefaults.buttonColors(containerColor = VerdeBotones),
            modifier = Modifier
                .fillMaxWidth()
                .semantics { contentDescription = "Guardar nuevo libro" }
        ) {
            Text("Guardar")
        }
    }
}