package com.example.booksy

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class AuthRepository(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) {
    val uidActual: String? get() = auth.currentUser?.uid

    private fun nombreACorreoFalso(nombreUsuario: String): String {
        val correo = nombreUsuario
            .trim()
            .lowercase()
            .replace("á", "a").replace("é", "e").replace("í", "i").replace("ú", "u").replace("ó", "o").replace("ñ", "n").replace(" ", "_")
            return "$correo@booksy.app"
    }

    suspend fun registrar(nombreUsuario: String, contrasena: String): Result<Unit> {
        return try {
            val nombreLimpio = nombreUsuario.trim()
            if (nombreLimpio.isEmpty() || contrasena.isEmpty()) {
                return Result.failure(Exception("Usuario y contresañe no pueden estar vacíos."))
            }
            val correoFalso = nombreACorreoFalso(nombreLimpio)
            val resultado = auth.createUserWithEmailAndPassword(correoFalso, contrasena).await()
            val uid = resultado.user?.uid ?: throw Exception("No se pudo crear el usuario.")

            val usuario = Usuario(uid = uid, nombre = nombreLimpio, rol = "usuario")
            firestore.collection("usuarios").document(uid).set(usuario).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun iniciarSesion(nombreUsuario: String, contrasena: String): Result<Unit> {
        return try {
            val correoFalso = nombreACorreoFalso(nombreUsuario.trim())
            auth.signInWithEmailAndPassword (correoFalso, contrasena).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun cerrarSesion() {
        auth.signOut()
    }

    suspend fun obtenerUsuarioActual(): Usuario? {
        val uid = uidActual ?: return null

        return try {
            val documento = firestore
                .collection("usuarios")
                .document(uid)
                .get()
                .await()

            println("🔥 UID ACTUAL: $uid")
            println("🔥 DOCUMENTO EXISTE: ${documento.exists()}")
            println("🔥 DATOS USUARIO: ${documento.data}")

            documento.toObject(Usuario::class.java)

        } catch (e: Exception) {
            println("🔥 ERROR OBTENIENDO USUARIO: ${e.message}")
            e.printStackTrace()
            null
        }
    }
}