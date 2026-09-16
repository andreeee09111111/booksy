package com.example.booksy

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LibrosViewModel @Inject constructor(
    private val repository: BookRepository,
    private val auth: FirebaseAuth
) : ViewModel() {

    private val usuarioActual = callbackFlow<String?> {

        val listener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            trySend(firebaseAuth.currentUser?.uid)
        }

        auth.addAuthStateListener(listener)

        awaitClose {
            auth.removeAuthStateListener(listener)
        }
    }

    val allBooks: StateFlow<List<Book>> =
        usuarioActual
            .flatMapLatest { uid ->
                if (uid != null) {
                    repository.obtenerLibros()
                } else {
                    flowOf(emptyList())
                }
            }
            .catch { error ->
                emit(emptyList())
            }
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5000),
                emptyList()
            )

    val biblioteca: StateFlow<List<Book>> =
        usuarioActual
            .flatMapLatest { uid ->
                if (uid != null) {
                    repository.obtenerBiblioteca(uid)
                } else {
                    flowOf(emptyList())
                }
            }
            .catch {
                emit(emptyList())
            }
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5000),
                emptyList()
            )

    private val _guardando = MutableStateFlow(false)
    val guardando: StateFlow<Boolean> = _guardando.asStateFlow()

    fun obtenerLibroPorId(
        id: String,
        onResultado: (Book?) -> Unit
    ) {
        viewModelScope.launch {
            onResultado(repository.obtenerLibroPorId(id))
        }
    }

    fun agregarLibro(
        titulo: String,
        autor: String,
        categoria: String,
        calificacion: Double,
        sinopsis: String,
        portadaUrl: String,
        onListo: () -> Unit
    ) {
        viewModelScope.launch {
            try {
                _guardando.value = true

                repository.agregarLibro(
                    Book(
                        titulo = titulo,
                        autor = autor,
                        categoria = categoria,
                        calificacion = calificacion,
                        sinopsis = sinopsis,
                        portadaUrl = portadaUrl
                    )
                )

                onListo()

            } finally {
                _guardando.value = false
            }
        }
    }

    fun actualizarLibro(
        libro: Book,
        onListo: () -> Unit
    ) {
        viewModelScope.launch {
            try {
                _guardando.value = true

                repository.actualizarLibro(libro)

                onListo()

            } catch (e: Exception) {
                println("❌ ERROR AL ACTUALIZAR LIBRO: ${e.message}")
                e.printStackTrace()
            } finally {
                _guardando.value = false
            }
        }
    }

    fun eliminarLibro(
        libroId: String,
        onListo: () -> Unit
    ) {
        viewModelScope.launch {
            try {
                repository.eliminarLibro(libroId)
                onListo()
            } catch (e: Exception) {
                // Aquí posteriormente podemos mostrar un mensaje de error
            }
        }
    }

    fun toggleBiblioteca(
        libro: Book,
        yaGuardado: Boolean
    ) {
        val uid = auth.currentUser?.uid ?: return

        viewModelScope.launch {
            try {
                if (yaGuardado) {
                    repository.quitarDeBiblioteca(uid, libro.id)
                } else {
                    repository.guardarEnBiblioteca(uid, libro)
                }
            } catch (e: Exception) {
                // Aquí posteriormente podemos mostrar un mensaje de error
            }
        }
    }
}