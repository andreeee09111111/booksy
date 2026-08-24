package com.example.booksy


interface Autenticable {
    fun validarCredenciales(usuario: String, contraseña: String): Boolean
}


class Usuario(val nombre: String, val contraseña: String) : Autenticable {
    override fun validarCredenciales(usuario: String, contraseña: String): Boolean {
        return this.nombre == usuario && this.contraseña == contraseña
    }

    override fun toString(): String {
        return "$nombre:$contraseña"
    }
}