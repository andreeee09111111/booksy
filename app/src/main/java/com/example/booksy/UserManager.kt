package com.example.booksy

import java.io.File
import java.security.MessageDigest

object UserManager : Autenticable {
    private val listaUsuarios: MutableList<Usuario> = mutableListOf()

    // ARCHIVO PERSISTENTE (Singleton)
    private lateinit var archivoTxt: File

    // Inicialización: carga datos del archivo al abrir la app
    fun inicializarArchivo(archivo: File) {
        archivoTxt = archivo
        if (!archivo.exists()) archivo.createNewFile()
        cargarUsuariosDesdeArchivo()
    }

    private fun cargarUsuariosDesdeArchivo() {
        listaUsuarios.clear()
        archivoTxt.readLines().forEach { linea ->
            if (linea.contains(":")) {
                val (nombre, hashPass) = linea.split(":")
                listaUsuarios.add(Usuario(nombre, hashPass))
            }
        }
    }

    private fun hashPassword(password: String): String {
        val bytes = MessageDigest.getInstance("SHA-256").digest(password.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }

    fun registrarUsuario(usuario: String?, contraseña: String?): Boolean {
        val userSeguro = usuario?.trim() ?: ""
        val passSegura = contraseña?.trim() ?: ""
        if (userSeguro.isEmpty() || passSegura.isEmpty()) return false
        if (existeUsuarioConNombre(userSeguro)) return false

        // Guardamos el HASH, no la contraseña
        listaUsuarios.add(Usuario(userSeguro, hashPassword(passSegura)))

        // Persistir en TXT
        val contenido = listaUsuarios.joinToString("\n") { "${it.nombre}:${it.contraseña}" }
        archivoTxt.writeText(contenido)
        return true
    }

    fun validarLoginGuardar(usuario: String?, contrasena: String?, archivoTxt: File): Boolean {
        return try {
            val u = usuario?.trim() ?: ""
            val p = contrasena?.trim() ?: ""
            if (u.isEmpty() || p.isEmpty()) return false

            val hashInput = hashPassword(p)
            val usuarioEncontrado = listaUsuarios.any { it.nombre == u && it.contraseña == hashInput }
            if (usuarioEncontrado) {
                // Guardamos la lista actualizada
                archivoTxt.writeText(listaUsuarios.joinToString("\n") { it.toString() })
            }
            usuarioEncontrado
        } catch (e: Exception) {
            false
        }
    }

    // Funciones de Orden Superior (sin cambios)
    fun obtenerNombresUsuarios(): List<String> = listaUsuarios.map { it.nombre }
    fun existeUsuarioConNombre(nombre: String): Boolean = listaUsuarios.any { it.nombre == nombre }
    fun obtenerUsuariosPorInicial(letra: Char): List<Usuario> = filtrarUsuarios { it.nombre.startsWith(letra.toString(), true) }
    fun filtrarUsuarios(criterio: (Usuario) -> Boolean): List<Usuario> = listaUsuarios.filter(criterio)

    override fun validarCredenciales(usuario: String, contraseña: String): Boolean {
        return listaUsuarios.any { it.nombre == usuario && it.contraseña == hashPassword(contraseña) }
    }
}