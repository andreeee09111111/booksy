package com.example.booksy

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.io.File

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val etUsuario = findViewById<EditText>(R.id.etUsuario)
        val etPassword = findViewById<EditText>(R.id.etPassword)
        val btnLogin = findViewById<Button>(R.id.btnLogin)
        val btnRegister = findViewById<Button>(R.id.btnRegister)

        // archivo .txt
        val archivoUsuariosTxt = File(filesDir, "usuarios_registrados.txt")

        // Registrar
        btnRegister.setOnClickListener {
            val user = etUsuario.text.toString()
            val pass = etPassword.text.toString()

            val registrado = UserManager.registrarUsuario(user, pass)

            if (registrado) {
                Toast.makeText(this, "Usuario agregado a la colección", Toast.LENGTH_SHORT).show()
                etUsuario.text.clear()
                etPassword.text.clear()
            } else {
                Toast.makeText(this, "Ingresa un usuario y contraseña válidos", Toast.LENGTH_SHORT).show()
            }
        }

        // Ingresar
        btnLogin.setOnClickListener {
            val user = etUsuario.text.toString()
            val pass = etPassword.text.toString()

            // parámetros
            val loginExitoso = UserManager.validarLoginGuardar(user, pass, archivoUsuariosTxt)

            if (loginExitoso) {
                Toast.makeText(this, "¡Bienvenido! Datos guardados en TXT", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this, "Credenciales incorrectas o usuario no registrado", Toast.LENGTH_SHORT).show()
            }
        }
    }
}