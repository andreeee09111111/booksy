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

        val etUsuario = findViewById<EditText>(R.id.Usuario)
        val etPassword = findViewById<EditText>(R.id.Password)
        val btnLogin = findViewById<Button>(R.id.Login)
        val btnRegister = findViewById<Button>(R.id.Register)

        val archivoUsuariosTxt = File(filesDir, "usuarios_registrados.txt")
        // ¡CRÍTICO! Inicializar el archivo antes de usarlo
        UserManager.inicializarArchivo(archivoUsuariosTxt)

        btnRegister.setOnClickListener {
            val user = etUsuario.text.toString()
            val pass = etPassword.text.toString()

            if (UserManager.registrarUsuario(user, pass)) {
                Toast.makeText(this, "Usuario registrado correctamente", Toast.LENGTH_SHORT).show()
                // Usar función de orden superior
                Toast.makeText(this, "Usuarios con A: ${UserManager.obtenerUsuariosPorInicial('a').size}", Toast.LENGTH_LONG).show()

                etUsuario.text.clear()
                etPassword.text.clear()
            } else {
                Toast.makeText(this, "Usuario inválido o ya existe", Toast.LENGTH_SHORT).show()
            }
        }

        btnLogin.setOnClickListener {
            val user = etUsuario.text.toString()
            val pass = etPassword.text.toString()

            if (UserManager.validarLoginGuardar(user, pass, archivoUsuariosTxt)) {
                Toast.makeText(this, "¡Bienvenido! Datos guardados", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            } else {
                Toast.makeText(this, "Credenciales incorrectas", Toast.LENGTH_SHORT).show()
            }
        }
    }
}