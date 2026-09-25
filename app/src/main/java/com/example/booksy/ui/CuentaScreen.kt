package com.example.booksy.ui

import android.util.TypedValue
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.booksy.AuthState
import com.example.booksy.AuthViewModel
import com.example.booksy.R
import com.example.booksy.AnalyticsViewModel

// Colores específicos para los botones de selección
val TealOscuro = Color(0xFF074A4C)
val TealMedio = Color(0xFF0E797C)
val AzulCobalto = Color(0xFF1A3B5C)
val AmbarCalido = Color(0xFFD97706)

@Composable
fun CuentaScreen(
    onIrAAdmin: () -> Unit,
    onIrABiblioteca: () -> Unit,
    onCerrarSesion: () -> Unit,
    isColorblind: Boolean = false,
    onThemeChange: (Boolean) -> Unit = {},
    viewModel: AuthViewModel = hiltViewModel()
) {
    val analyticsViewModel: AnalyticsViewModel = hiltViewModel()
    LaunchedEffect(Unit) { analyticsViewModel.registrarPantalla("Biblioteca") }
    val estado by viewModel.estado.collectAsState()
    val usuario = (estado as? AuthState.ConSesion)?.usuario
    val esAdmin = usuario?.rol == "admin"
    val esAltoContraste = usuario?.tema == "alto_contraste"

    // Lectura dinámica de colores desde el tema XML activo
    val backgroundColor = themeColor(R.attr.appBackgroundColor)
    val textColor = themeColor(R.attr.appTextColor)
    val primaryColor = themeColor(R.attr.appPrimaryColor)
    val cardBgColor = themeColor(R.attr.appCardBgColor)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            "Cuenta",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = textColor
        )
        Spacer(Modifier.height(16.dp))

        // Tarjeta de perfil
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = cardBgColor),
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
                        .background(primaryColor.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        (usuario?.nombre?.firstOrNull() ?: 'U').uppercaseChar().toString(),
                        color = primaryColor,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(Modifier.width(12.dp))
                Column {
                    Text(
                        usuario?.nombre ?: "",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium,
                        color = textColor
                    )
                    if (esAdmin) {
                        Spacer(Modifier.height(4.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(50))
                                .background(primaryColor.copy(alpha = 0.2f))
                                .padding(horizontal = 10.dp, vertical = 3.dp)
                        ) {
                            Text("Administrador", style = MaterialTheme.typography.labelSmall, color = primaryColor)
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(24.dp))
        Text("MI CUENTA", style = MaterialTheme.typography.labelMedium, color = textColor.copy(alpha = 0.6f))
        Spacer(Modifier.height(8.dp))

        FilaCuenta(
            icono = { Icon(painter = painterResource(id = R.drawable.ic_heart), contentDescription = null, tint = primaryColor) },
            titulo = "Mi biblioteca",
            subtitulo = "Tus libros guardados",
            onClick = onIrABiblioteca,
            cardBg = cardBgColor,
            textColor = textColor
        )

        if (esAdmin) {
            Spacer(Modifier.height(16.dp))
            Text("ADMINISTRACIÓN", style = MaterialTheme.typography.labelMedium, color = textColor.copy(alpha = 0.6f))
            Spacer(Modifier.height(8.dp))
            FilaCuenta(
                icono = { Icon(Icons.Filled.Person, contentDescription = null, tint = primaryColor) },
                titulo = "Panel de administrador",
                subtitulo = "Agregar, editar y eliminar libros",
                onClick = onIrAAdmin,
                cardBg = cardBgColor,
                textColor = textColor
            )
        }

        // --- SECCIÓN PALETA DE COLOR ---
        Spacer(Modifier.height(24.dp))
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = cardBgColor),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "PALETA DE COLOR",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = textColor.copy(alpha = 0.6f)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(24.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Círculo 1: Teal (Normal)
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.clickable { viewModel.cambiarTema("normal") }
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(TealOscuro)
                                .border(
                                    width = if (!esAltoContraste) 3.dp else 0.dp,
                                    color = if (!esAltoContraste) TealMedio else Color.Transparent,
                                    shape = CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            if (!esAltoContraste) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Seleccionado",
                                    tint = Color.White
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = "Teal", fontSize = 12.sp, color = textColor)
                    }

                    // Círculo 2: Alto Contraste (Colorblind)
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.clickable { viewModel.cambiarTema("alto_contraste") }
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(AzulCobalto)
                                .border(
                                    width = if (esAltoContraste) 3.dp else 0.dp,
                                    color = if (esAltoContraste) AmbarCalido else Color.Transparent,
                                    shape = CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            if (esAltoContraste) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Seleccionado",
                                    tint = Color.White
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = "Alto contraste", fontSize = 12.sp, color = textColor)
                    }
                }
            }
        }

        Spacer(Modifier.height(32.dp))

        OutlinedButton(
            onClick = onCerrarSesion,
            shape = RoundedCornerShape(50),
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
        ) {
            Text("Cerrar sesión", color = primaryColor)
        }
    }
}

@Composable
private fun FilaCuenta(
    icono: @Composable () -> Unit,
    titulo: String,
    subtitulo: String,
    onClick: () -> Unit,
    cardBg: Color,
    textColor: Color
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = cardBg),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            icono()
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(titulo, style = MaterialTheme.typography.titleMedium, color = textColor)
                Text(subtitulo, style = MaterialTheme.typography.bodySmall, color = textColor.copy(alpha = 0.6f))
            }
            Icon(Icons.Filled.ChevronRight, contentDescription = null, tint = textColor.copy(alpha = 0.6f))
        }
    }
}

// Función auxiliar para consultar el atributo del tema XML actual
@Composable
fun themeColor(attrResId: Int): Color {
    val context = LocalContext.current
    val typedValue = TypedValue()
    context.theme.resolveAttribute(attrResId, typedValue, true)
    return Color(typedValue.data)
}