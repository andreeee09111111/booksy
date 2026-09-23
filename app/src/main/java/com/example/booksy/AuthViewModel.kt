package com.example.booksy

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class AuthState {
    object Cargando : AuthState()
    object SinSesion : AuthState()
    data class ConSesion(val usuario: Usuario) : AuthState()
}

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val auth: FirebaseAuth
) : ViewModel() {
    private val _estado = MutableStateFlow<AuthState>(AuthState.Cargando)
    val estado: StateFlow<AuthState> = _estado.asStateFlow()

    private val _mensajeError = MutableStateFlow<String?>(null)
    val mensajeError: StateFlow<String?> = _mensajeError.asStateFlow()

    init { revisarSesion() }

    fun revisarSesion() {
        viewModelScope.launch {
            if (auth.currentUser == null) {
                _estado.value = AuthState.SinSesion
            } else  {
                val usuario = authRepository.obtenerUsuarioActual()
                _estado.value = if (usuario != null) AuthState.ConSesion(usuario) else AuthState.SinSesion
            }
        }
    }

    fun iniciarSesion(nombreUsuario: String, contrasena: String) {
        viewModelScope.launch {
            _mensajeError.value = null
            val resultado = authRepository.iniciarSesion(nombreUsuario, contrasena)
            if (resultado.isSuccess) revisarSesion()
            else _mensajeError.value = "Usuario o contraseña incorrectos"
        }
    }

    fun registrar(nombreUsuario: String, contrasena: String) {
        viewModelScope.launch {
            _mensajeError.value = null
            val resultado = authRepository.registrar(nombreUsuario, contrasena)
            if (resultado.isSuccess) revisarSesion()
            else _mensajeError.value = resultado.exceptionOrNull()?.localizedMessage ?: "No se pudo resgistrar"
        }
    }

    fun cerrarSesion() {
        authRepository.cerrarSesion()
        _estado.value = AuthState.SinSesion
    }

    fun iniciarSesionConGoogle(context: android.content.Context) {
        viewModelScope.launch {
            _mensajeError.value = null
            val resultado = authRepository.iniciarSesionConGoogle(context)
            if (resultado.isSuccess) revisarSesion()
            else _mensajeError.value = resultado.exceptionOrNull()?.localizedMessage ?: "No se pudo iniciar sesión con Google"
        }
    }

}