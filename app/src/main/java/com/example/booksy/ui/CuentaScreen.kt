package com.example.booksy.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.booksy.AuthState
import com.example.booksy.AuthViewModel
import com.example.booksy.R

@Composable
fun CuentaScreen(
    onIrAAdmin: () -> Unit,
    onIrABiblioteca: () -> Unit,
    onCerrarSesion: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val estado by viewModel.estado.collectAsState()
    val usuario = (estado as? AuthState.ConSesion)?.usuario
    val esAdmin = usuario?.rol == "admin"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(FondoClaro)
            .padding(16.dp)
    ) {
        Text("Cuenta", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(16.dp))

        // Tarjeta de perfil
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFDDEDE9)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        (usuario?.nombre?.firstOrNull() ?: 'U').uppercaseChar().toString(),
                        color = TealPrincipal,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(Modifier.width(12.dp))
                Column {
                    Text(usuario?.nombre ?: "", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                    if (esAdmin) {
                        Spacer(Modifier.height(4.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(50))
                                .background(Color(0xFFDDEDE9))
                                .padding(horizontal = 10.dp, vertical = 3.dp)
                        ) {
                            Text("Administrador", style = MaterialTheme.typography.labelSmall, color = TealPrincipal)
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(24.dp))
        Text("MI CUENTA", style = MaterialTheme.typography.labelMedium, color = Color.Gray)
        Spacer(Modifier.height(8.dp))

        FilaCuenta(
            icono = { Icon(painter = painterResource(id = R.drawable.ic_heart), contentDescription = null, tint = TealPrincipal) },
            titulo = "Mi biblioteca",
            subtitulo = "Tus libros guardados",
            onClick = onIrABiblioteca
        )

        if (esAdmin) {
            Spacer(Modifier.height(24.dp))
            Text("ADMINISTRACIÓN", style = MaterialTheme.typography.labelMedium, color = Color.Gray)
            Spacer(Modifier.height(8.dp))
            FilaCuenta(
                icono = { Icon(Icons.Filled.Person, contentDescription = null, tint = TealPrincipal) },
                titulo = "Panel de administrador",
                subtitulo = "Agregar, editar y eliminar libros",
                onClick = onIrAAdmin
            )
        }

        Spacer(Modifier.weight(1f))

        OutlinedButton(
            onClick = onCerrarSesion,
            shape = RoundedCornerShape(50),
            modifier = Modifier.fillMaxWidth().height(52.dp)
        ) { Text("Cerrar sesión") }
    }
}

@Composable
private fun FilaCuenta(
    icono: @Composable () -> Unit,
    titulo: String,
    subtitulo: String,
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
            icono()
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(titulo, style = MaterialTheme.typography.titleMedium)
                Text(subtitulo, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
            }
            Icon(Icons.Filled.ChevronRight, contentDescription = null, tint = Color.Gray)
        }
    }
}