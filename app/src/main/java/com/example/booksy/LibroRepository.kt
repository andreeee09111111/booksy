package com.example.booksy

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class BookRepository(
    private val firestore: FirebaseFirestore
) {
    private val librosRef = firestore.collection("libros")
    private fun bibliotecaRef(uid: String) = firestore.collection("usuarios").document(uid).collection("biblioteca")

    fun obtenerLibros(): Flow<List<Book>> = callbackFlow {
        val listener = librosRef.addSnapshotListener { snapshot, error ->
            if (error != null) {
                println("Error firestore libros: ${error.message}")
                trySend(emptyList())
                return@addSnapshotListener
            }
            val libros = snapshot?.documents?.mapNotNull { doc ->
                doc.toObject(Book::class.java)?.copy(id = doc.id)
            } ?: emptyList()
            println("Libros recibidos: ${libros.size}")
            trySend(libros)
        }
        awaitClose { listener.remove() }
    }

    suspend fun obtenerLibroPorId(id: String): Book? {
        return try {
            val doc = librosRef.document(id).get().await()
            doc.toObject(Book::class.java)?.copy(id = doc.id)
        } catch (e: Exception) {
            null
        }
    }

    suspend fun agregarLibro(libro: Book) {
        val doc = librosRef.document()
        doc.set(libro.copy(id = doc.id)).await()
    }

    suspend fun actualizarLibro(libro: Book) {
        librosRef.document(libro.id).set(libro).await()
    }

    suspend fun eliminarLibro(libroId: String) {
        librosRef.document(libroId).delete().await()
    }

    fun obtenerBiblioteca(uid: String): Flow<List<Book>> = callbackFlow {
        val listener = bibliotecaRef(uid).addSnapshotListener { snapshot, error ->
            if (error != null) {
                println("Error firestore libros: ${error.message}")
                trySend(emptyList())
                return@addSnapshotListener
            }
            val libros = snapshot?.documents?.mapNotNull { doc ->
                doc.toObject(Book::class.java)?.copy(id = doc.id)
            } ?: emptyList()
            println("Libros recibidos: ${libros.size}")
            trySend(libros)
        }
        awaitClose { listener.remove() }
    }

    suspend fun guardarEnBiblioteca(uid: String, libro: Book) {
        bibliotecaRef(uid).document(libro.id).set(libro).await()
    }

    suspend fun quitarDeBiblioteca(uid: String, libroId: String) {
        bibliotecaRef(uid).document(libroId).delete().await()
    }
}