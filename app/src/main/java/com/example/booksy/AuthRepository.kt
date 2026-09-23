package com.example.booksy

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import android.content.Context
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.GoogleAuthProvider
import com.example.booksy.R
import java.security.MessageDigest
import java.util.UUID

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

    suspend fun iniciarSesionConGoogle(context: Context): Result<Unit> {
        return try {
            val credentialManager = CredentialManager.create(context)
            val nonce = generarNonce()

            val googleIdOption = GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(false)
                .setServerClientId(context.getString(R.string.default_web_client_id))
                .setAutoSelectEnabled(false)
                .setNonce(nonce)
                .build()

            val request = GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build()

            val response = credentialManager.getCredential(request = request, context = context)
            val credential = response.credential

            if (credential.type != GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                return Result.failure(Exception("Tipo de credencial no soportado"))
            }

            val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
            val authCredential = GoogleAuthProvider.getCredential(googleIdTokenCredential.idToken, null)
            val resultado = auth.signInWithCredential(authCredential).await()
            val uid = resultado.user?.uid ?: throw Exception("Usuario inválido")

            val doc = firestore.collection("usuarios").document(uid).get().await()
            if (!doc.exists()) {
                val nombre = googleIdTokenCredential.displayName ?: resultado.user?.email ?: "Usuario"
                val usuario = Usuario(uid = uid, nombre = nombre, rol = "usuario")
                firestore.collection("usuarios").document(uid).set(usuario).await()
            }

            Result.success(Unit)
        } catch (e: GetCredentialCancellationException) {
            Result.failure(Exception("Inicio de sesión cancelado"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun generarNonce(): String {
        val rawNonce = UUID.randomUUID().toString()
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(rawNonce.toByteArray())
        return digest.fold("") { str, it -> str + "%02x".format(it) }
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