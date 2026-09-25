package com.example.booksy.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.booksy.Book
import com.example.booksy.LibrosViewModel
import com.example.booksy.R
import com.example.booksy.AnalyticsViewModel

@Composable
fun DetalleLibroScreen(
    libroId: String,
    onVolver: () -> Unit,
    viewModel: LibrosViewModel = hiltViewModel()
) {
    val analyticsViewModel: AnalyticsViewModel = hiltViewModel()
    LaunchedEffect(libroId) { analyticsViewModel.registrarPantalla("Detalle_$libroId") }
    val biblioteca by viewModel.biblioteca.collectAsState()
    var libro by remember { mutableStateOf<Book?>(null) }
    var cargando by remember { mutableStateOf(true) }
    var mostrarZoom by remember { mutableStateOf(false) }

    LaunchedEffect(libroId) {
        cargando = true
        viewModel.obtenerLibroPorId(libroId) { resultado ->
            libro = resultado
            cargando = false
        }
    }


    Box(Modifier.fillMaxSize().background(themeColor(R.attr.appBackgroundColor))) {
        when {
            cargando -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = themeColor(R.attr.appPrimaryColor))
                }
            }
            libro == null -> {
                Column(
                    Modifier.fillMaxSize().padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text("No se pudo encontrar este libro", color = Color.Gray)
                    Spacer(Modifier.height(12.dp))
                    TextButton(onClick = onVolver) { Text("Volver") }
                }
            }
            else -> {
                val libroActual = libro!!
                val yaGuardado = biblioteca.any { it.id == libroActual.id }

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                ) {
                    // Portada: toca la imagen para verla en pantalla completa con zoom
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(320.dp)
                    ) {
                        AsyncImage(
                            model = libroActual.portadaUrl,
                            contentDescription = "Portada de ${libroActual.titulo}. Toca para ampliar",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxSize()
                                .background(themeColor(R.attr.appPrimaryColor))
                                .clickable { mostrarZoom = true }
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color.Black.copy(alpha = 0.15f))
                        )
                        IconButton(
                            onClick = onVolver,
                            modifier = Modifier
                                .padding(12.dp)
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(Color.Black.copy(alpha = 0.35f))
                                .align(Alignment.TopStart)
                        ) {
                            Icon(Icons.Filled.ArrowBack, contentDescription = "Volver", tint = Color.White)
                        }
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .padding(10.dp)
                                .clip(RoundedCornerShape(50))
                                .background(Color.Black.copy(alpha = 0.45f))
                                .padding(horizontal = 10.dp, vertical = 5.dp)
                        ) {
                            Text("Toca para hacer zoom", color = Color.White, style = MaterialTheme.typography.labelSmall)
                        }
                    }

                    Column(modifier = Modifier.padding(20.dp)) {
                        if (libroActual.categoria.isNotBlank()) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(50))
                                    .background(themeColor(R.attr.appPrimaryColor).copy(alpha = 0.12f))
                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    libroActual.categoria.uppercase(),
                                    color = themeColor(R.attr.appPrimaryColor),
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Spacer(Modifier.height(8.dp))
                        }

                        Row(verticalAlignment = Alignment.Top) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    libroActual.titulo,
                                    style = MaterialTheme.typography.headlineSmall,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    libroActual.autor,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color.Gray
                                )
                            }
                            IconButton(
                                onClick = { viewModel.toggleBiblioteca(libroActual, yaGuardado) },
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(CircleShape)
                                    .background(Color.White)
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_heart),
                                    contentDescription = if (yaGuardado) "Quitar de mi biblioteca" else "Guardar en biblioteca",
                                    tint = if (yaGuardado) Color.Red else Color.LightGray
                                )
                            }
                        }

                        Spacer(Modifier.height(10.dp))
                        FilaEstrellas(calificacion = libroActual.calificacion, colorTexto = Color.Black)

                        if (libroActual.sinopsis.isNotBlank()) {
                            Spacer(Modifier.height(18.dp))
                            Text(
                                "Sinopsis",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(Modifier.height(6.dp))
                            Text(
                                libroActual.sinopsis,
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.DarkGray
                            )
                        }

                        Spacer(Modifier.height(24.dp))
                    }
                }

                if (mostrarZoom) {
                    VisorImagenConZoom(
                        urlImagen = libroActual.portadaUrl,
                        descripcion = "Portada de ${libroActual.titulo}",
                        onCerrar = { mostrarZoom = false }
                    )
                }
            }
        }
    }
}

/**
 * zoom con pellizco arrastrar la imagen ampliada
 */
@Composable
private fun VisorImagenConZoom(
    urlImagen: String,
    descripcion: String,
    onCerrar: () -> Unit
) {
    Dialog(
        onDismissRequest = onCerrar,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        var escala by remember { mutableStateOf(1f) }
        var desplazamientoX by remember { mutableStateOf(0f) }
        var desplazamientoY by remember { mutableStateOf(0f) }

        fun restablecer() {
            escala = 1f
            desplazamientoX = 0f
            desplazamientoY = 0f
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
        ) {
            AsyncImage(
                model = urlImagen,
                contentDescription = descripcion,
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer(
                        scaleX = escala,
                        scaleY = escala,
                        translationX = desplazamientoX,
                        translationY = desplazamientoY
                    )
                    .pointerInput(Unit) {
                        detectTransformGestures { _, pan, zoom, _ ->
                            val nuevaEscala = (escala * zoom).coerceIn(1f, 5f)
                            escala = nuevaEscala
                            if (nuevaEscala > 1f) {
                                desplazamientoX += pan.x
                                desplazamientoY += pan.y
                            } else {
                                desplazamientoX = 0f
                                desplazamientoY = 0f
                            }
                        }
                    }
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onDoubleTap = {
                                if (escala > 1f) {
                                    restablecer()
                                } else {
                                    escala = 2.5f
                                }
                            }
                        )
                    }
            )


            IconButton(
                onClick = onCerrar,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(16.dp)
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.15f))
            ) {
                Icon(Icons.Filled.Close, contentDescription = "Cerrar", tint = Color.White)
            }

            Text(
                "Pellizca para hacer zoom · doble toque para restablecer",
                color = Color.White.copy(alpha = 0.8f),
                style = MaterialTheme.typography.labelSmall,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(20.dp)
            )
        }
    }
}