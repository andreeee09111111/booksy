package com.example.booksy.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.booksy.AuthViewModel
import com.example.booksy.R
import com.example.booksy.AnalyticsViewModel


@Composable
fun LoginScreen(
    viewModel: AuthViewModel
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val analyticsViewModel: AnalyticsViewModel = hiltViewModel()
    LaunchedEffect(Unit) { analyticsViewModel.registrarPantalla("Login") }
    var usuario by remember { mutableStateOf("") }
    var contrasena by remember { mutableStateOf("") }
    var mostrarContrasena by remember { mutableStateOf(false) }
    val error by viewModel.mensajeError.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(themeColor(R.attr.appBackgroundColor))
            .padding(horizontal = 32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "BOOKSY",
            fontFamily = FontFamily.Serif,
            fontWeight = FontWeight.Bold,
            fontSize = 32.sp,
            letterSpacing = 2.sp,
            color = Color(0xFF2B2B2B),
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.height(48.dp))

        TextField(
            value = usuario,
            onValueChange = { usuario = it },
            label = { Text("Usuario") },
            singleLine = true,
            colors = TextFieldDefaults.colors(
                unfocusedContainerColor = Color.Transparent,
                focusedContainerColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Gray,
                focusedIndicatorColor = themeColor(R.attr.appPrimaryColor)
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(16.dp))

        TextField(
            value = contrasena,
            onValueChange = { contrasena = it },
            label = { Text("Contraseña") },
            singleLine = true,
            visualTransformation = if (mostrarContrasena) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            trailingIcon = {
                IconButton(onClick = { mostrarContrasena = !mostrarContrasena }) {
                    Icon(
                        painter = painterResource(
                            id = if (mostrarContrasena) R.drawable.ic_ojo_abierto else R.drawable.ic_ojo_cerrado
                        ),
                        contentDescription = if (mostrarContrasena) "Ocultar contraseña" else "Mostrar contraseña"
                    )
                }
            },
            colors = TextFieldDefaults.colors(
                unfocusedContainerColor = Color.Transparent,
                focusedContainerColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Gray,
                focusedIndicatorColor = themeColor(R.attr.appPrimaryColor)
            ),
            modifier = Modifier.fillMaxWidth()
        )

        if (error != null) {
            Spacer(Modifier.height(8.dp))
            Text(error ?: "", color = Color.Red, style = MaterialTheme.typography.bodySmall)
        }

        Spacer(Modifier.height(32.dp))

        Button(
            onClick = { viewModel.iniciarSesion(usuario, contrasena) },
            enabled = usuario.isNotBlank() && contrasena.isNotBlank(),
            shape = RoundedCornerShape(50),
            colors = ButtonDefaults.buttonColors(containerColor = themeColor(R.attr.appPrimaryColor)),
            modifier = Modifier.fillMaxWidth().height(52.dp)
        ) { Text("Ingresar", color = Color.White) }

        Spacer(Modifier.height(12.dp))

        Button(
            onClick = { viewModel.registrar(usuario, contrasena) },
            enabled = usuario.isNotBlank() && contrasena.isNotBlank(),
            shape = RoundedCornerShape(50),
            colors = ButtonDefaults.buttonColors(containerColor = themeColor(R.attr.appPrimaryColor)),
            modifier = Modifier.fillMaxWidth().height(52.dp)
        ) { Text("Registrar", color = Color.White) }

        Spacer(Modifier.height(16.dp))

        OutlinedButton(
            onClick = { viewModel.iniciarSesionConGoogle(context) },
            shape = RoundedCornerShape(50),
            modifier = Modifier.fillMaxWidth().height(52.dp)
        ) { Text("Iniciar sesión con Google") }
    }
}