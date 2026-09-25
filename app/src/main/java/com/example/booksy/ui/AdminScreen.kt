 package com.example.booksy.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.booksy.R
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.booksy.AnalyticsViewModel
import androidx.compose.runtime.LaunchedEffect

@Composable
fun AdminScreen(
    onGestionarLibros: () -> Unit,
    onAgregarLibro: () -> Unit,
    onVolver: () -> Unit
) {
    val analyticsViewModel: AnalyticsViewModel = hiltViewModel()
    LaunchedEffect(Unit) { analyticsViewModel.registrarPantalla("Admin") }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(themeColor(R.attr.appBackgroundColor))
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onVolver) {
                Icon(Icons.Filled.ArrowBack, contentDescription = "Volver")
            }
            Column {
                Text("Panel de administrador", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Text("Gestiona el catálogo de Booksy", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
            }
        }

        Spacer(Modifier.height(24.dp))

        OpcionAdmin(
            icono = Icons.Filled.Edit,
            titulo = "Gestionar libros",
            descripcion = "Busca, edita o elimina libros del catálogo",
            onClick = onGestionarLibros
        )
        Spacer(Modifier.height(12.dp))
        OpcionAdmin(
            icono = Icons.Filled.Add,
            titulo = "Agregar libro",
            descripcion = "Publica un nuevo libro con portada y sinopsis",
            onClick = onAgregarLibro
        )

        Spacer(Modifier.height(24.dp))

        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFEFEAE3)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(modifier = Modifier.padding(16.dp)) {
                Icon(Icons.Filled.Info, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(20.dp))
                Spacer(Modifier.width(10.dp))
                Column {
                    Text("Administración segura", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                    Text(
                        "Estas opciones solo aparecen para cuentas con rol admin. Firebase también valida el rol antes de permitir cambios.",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }
            }
        }
    }
}

@Composable
private fun OpcionAdmin(
    icono: ImageVector,
    titulo: String,
    descripcion: String,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = Modifier.fillMaxWidth().clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icono, contentDescription = null, tint = themeColor(R.attr.appPrimaryColor), modifier = Modifier.size(24.dp))
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(titulo, style = MaterialTheme.typography.titleMedium)
                Text(descripcion, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
            }
            Icon(Icons.Filled.ChevronRight, contentDescription = null, tint = Color.Gray)
        }
    }
}