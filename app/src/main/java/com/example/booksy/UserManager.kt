package com.example.booksy

import java.io.File

object UserManager {

    private val listaUsuarios: MutableList<String> = mutableListOf()


    fun registrarUsuario(usuario: String?, contraseña: String?): Boolean {
        val userSeguro = usuario?.trim() ?: ""
        val passSegura = contraseña?.trim() ?: ""

        if (userSeguro.isEmpty() || passSegura.isEmpty()) {
            return false
        }

        val registro = "$userSeguro:$passSegura"
        listaUsuarios.add(registro)
        return true
    }


    fun validarLoginGuardar(usuario: String?, contrasena: String?, archivoTxt: File): Boolean {
        return try {
            val u = usuario?.trim() ?: ""
            val p = contrasena?.trim() ?: ""

            if (u.isEmpty() || p.isEmpty()) {
                return false
            }

            val encontrado = listaUsuarios.contains("$u:$p")

            if (encontrado) {
                archivoTxt.writeText(listaUsuarios.joinToString("\n"))
            }

            encontrado
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}