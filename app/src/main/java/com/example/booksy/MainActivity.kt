package com.example.booksy

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.booksy.ui.BooksyNavHost
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // Estado para recordar si el modo alto contraste está activo
            var isColorblindMode by rememberSaveable { mutableStateOf(false) }

            // Cambia el tema nativo según la paleta seleccionada
            val themeResId = if (isColorblindMode) R.style.Theme_Booksy_Colorblind else R.style.Theme_Booksy
            setTheme(themeResId)

            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    BooksyNavHost(
                        isColorblind = isColorblindMode,
                        onThemeChange = { nuevoEstado ->
                            if (isColorblindMode != nuevoEstado) {
                                isColorblindMode = nuevoEstado
                                recreate() // Recrea la actividad para aplicar el tema de themes.xml al instante
                            }
                        }
                    )
                }
            }
        }
    }
}