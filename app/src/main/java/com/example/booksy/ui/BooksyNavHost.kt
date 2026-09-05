
package com.example.booksy.ui

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

@Composable
fun BooksyNavHost() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "lista") {

        composable("lista") {
            ListaLibrosScreen(
                onAgregarClick = { navController.navigate("formulario") }
            )
        }

        composable("formulario") {
            AgregarLibroScreen(
                onGuardado = { navController.popBackStack() }
            )
        }
    }
}