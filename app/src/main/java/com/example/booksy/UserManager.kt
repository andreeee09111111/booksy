package com.example.booksy

import java.io.File

// Objeto (singleton) que implementa la interfaz
object UserManager : Autenticable {
    // Colección (lista) para almacenar datos de usuario
    private val listaUsuarios: MutableList<Usuario> = mutableListOf()

    // ===== FUNCIONES CON PARÁMETROS Y RETORNO =====

    // Función 1: Registrar usuario - retorna Boolean
    fun registrarUsuario(usuario: String?, contraseña: String?): Boolean {
        // Null seguro con operador Elvis
        val userSeguro = usuario?.trim() ?: ""
        val passSegura = contraseña?.trim() ?: ""

        if (userSeguro.isEmpty() || passSegura.isEmpty()) {
            return false
        }

        // Verificar si el usuario ya existe usando función de orden superior
        if (existeUsuarioConNombre(userSeguro)) {
            return false
        }

        val nuevoUsuario = Usuario(userSeguro, passSegura)
        listaUsuarios.add(nuevoUsuario)
        return true
    }

    // Función 2: Validar login - retorna Boolean
    fun validarLoginGuardar(usuario: String?, contrasena: String?, archivoTxt: File): Boolean {
        // Manejo de excepciones
        return try {
            val u = usuario?.trim() ?: ""
            val p = contrasena?.trim() ?: ""

            if (u.isEmpty() || p.isEmpty()) {
                return false
            }

            // Buscar usuario usando función de orden superior
            val usuarioEncontrado = listaUsuarios.find { it.nombre == u && it.contraseña == p }

            val encontrado = usuarioEncontrado != null

            if (encontrado) {
                // Guardar datos en archivo
                val nombres = obtenerNombresUsuarios()
                archivoTxt.writeText(nombres.joinToString("\n"))
            }

            encontrado
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    // ===== FUNCIONES DE ORDEN SUPERIOR Y LAMBDAS =====

    // Función de orden superior: filtrar usuarios por criterio
    fun filtrarUsuarios(criterio: (Usuario) -> Boolean): List<Usuario> {
        return listaUsuarios.filter(criterio)
    }

    // Función de orden superior: transformar datos (obtener solo nombres)
    fun obtenerNombresUsuarios(): List<String> {
        return listaUsuarios.map { it.nombre }
    }

    // Función de orden superior: verificar si existe usuario con cierto nombre
    fun existeUsuarioConNombre(nombre: String): Boolean {
        return listaUsuarios.any { it.nombre == nombre }
    }

    // Función con lambda: obtener usuarios por inicial
    fun obtenerUsuariosPorInicial(letra: Char): List<Usuario> {
        return filtrarUsuarios { it.nombre.startsWith(letra.toString(), ignoreCase = true) }
    }

    // Función con lambda: obtener usuarios que contengan cierta palabra
    fun obtenerUsuariosQueContengan(palabra: String): List<Usuario> {
        return filtrarUsuarios { it.nombre.contains(palabra, ignoreCase = true) }
    }

    // Implementación de la interfaz Autenticable
    override fun validarCredenciales(usuario: String, contraseña: String): Boolean {
        return listaUsuarios.any { it.nombre == usuario && it.contraseña == contraseña }
    }
}