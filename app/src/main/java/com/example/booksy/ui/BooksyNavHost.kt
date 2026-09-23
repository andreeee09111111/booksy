package com.example.booksy.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.booksy.AuthState
import com.example.booksy.AuthViewModel
import com.example.booksy.Book
import com.example.booksy.LibrosViewModel
import com.example.booksy.R
import kotlinx.coroutines.launch

private data class TabDestino(val iconoRes: Int, val etiqueta: String)

private val tabsPrincipales = listOf(
    TabDestino(R.drawable.ic_home, "Inicio"),
    TabDestino(R.drawable.ic_explorar, "Explorar"),
    TabDestino(R.drawable.ic_biblioteca, "Biblioteca"),
    TabDestino(R.drawable.menu_perfil_svg, "Cuenta")
)

private val rutasConBottomBar = setOf(
    "principal", "admin", "gestionarLibros", "agregarLibro"
)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun BooksyNavHost(
    isColorblind: Boolean = false,
    onThemeChange: (Boolean) -> Unit = {}
) {
    val navController = rememberNavController()
    val authViewModel: AuthViewModel = hiltViewModel()
    val librosViewModel: LibrosViewModel = hiltViewModel()
    val estadoAuth by authViewModel.estado.collectAsState()
    val todosLosLibros by librosViewModel.allBooks.collectAsState()

    val backStackEntry by navController.currentBackStackEntryAsState()
    val rutaActual = backStackEntry?.destination?.route

    val pagerState = rememberPagerState(pageCount = { tabsPrincipales.size })
    val scope = rememberCoroutineScope()
    var mostrarBusqueda by remember { mutableStateOf(false) }

    LaunchedEffect(estadoAuth) {
        kotlinx.coroutines.android.awaitFrame()
        when (estadoAuth) {
            is AuthState.SinSesion -> {
                if (rutaActual != "login") {
                    navController.navigate("login") { popUpTo(0) }
                }
            }
            is AuthState.ConSesion -> {
                if (rutaActual == null || rutaActual == "login") {
                    navController.navigate("principal") { popUpTo(0) }
                }
            }
            AuthState.Cargando -> {}
        }
    }

    val mostrarBottomBar = rutaActual in rutasConBottomBar ||
            rutaActual?.startsWith("detalle") == true ||
            rutaActual?.startsWith("editarLibro") == true

    Scaffold(
        bottomBar = {
            if (mostrarBottomBar) {
                NavigationBar {
                    tabsPrincipales.forEachIndexed { index, tab ->
                        NavigationBarItem(
                            selected = rutaActual == "principal" && pagerState.currentPage == index,
                            onClick = {
                                scope.launch {
                                    if (rutaActual != "principal") {
                                        navController.navigate("principal") {
                                            popUpTo("principal") { inclusive = true }
                                        }
                                    }
                                    pagerState.animateScrollToPage(index)
                                }
                            },
                            icon = {
                                Icon(
                                    painter = painterResource(id = tab.iconoRes),
                                    contentDescription = tab.etiqueta
                                )
                            },
                            label = { Text(tab.etiqueta) }
                        )
                    }
                }
            }
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {

            NavHost(navController = navController, startDestination = "cargando") {

                composable("cargando") {
                    val backgroundColor = themeColor(R.attr.appBackgroundColor)
                    val primaryColor = themeColor(R.attr.appPrimaryColor)

                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(backgroundColor),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            color = primaryColor
                        )
                    }
                }

                composable("login") {
                    LoginScreen(viewModel = authViewModel)
                }

                composable("principal") {
                    HorizontalPager(
                        state = pagerState,
                        modifier = Modifier.fillMaxSize()
                    ) { pagina ->
                        when (pagina) {
                            0 -> InicioScreen(
                                onVerLibro = { id -> navController.navigate("detalle/$id") },
                                onIrAExplorar = { scope.launch { pagerState.animateScrollToPage(1) } },
                                onBuscar = { mostrarBusqueda = true }
                            )
                            1 -> ExplorarScreen(onVerLibro = { id -> navController.navigate("detalle/$id") })
                            2 -> BibliotecaScreen(onVerLibro = { id -> navController.navigate("detalle/$id") })
                            3 -> CuentaScreen(
                                onIrAAdmin = { navController.navigate("admin") },
                                onIrABiblioteca = { scope.launch { pagerState.animateScrollToPage(2) } },
                                onCerrarSesion = { authViewModel.cerrarSesion() },
                                isColorblind = isColorblind,
                                onThemeChange = onThemeChange
                            )
                        }
                    }
                }

                composable(
                    "detalle/{libroId}",
                    arguments = listOf(navArgument("libroId") { type = NavType.StringType })
                ) { backStack ->
                    val libroId = backStack.arguments?.getString("libroId") ?: ""
                    DetalleLibroScreen(libroId = libroId, onVolver = { navController.popBackStack() })
                }

                composable("admin") {
                    AdminScreen(
                        onGestionarLibros = { navController.navigate("gestionarLibros") },
                        onAgregarLibro = { navController.navigate("agregarLibro") },
                        onVolver = { navController.popBackStack() }
                    )
                }

                composable("gestionarLibros") {
                    GestionarLibrosScreen(
                        onEditarLibro = { libroId -> navController.navigate("editarLibro/$libroId") },
                        onAgregarLibro = { navController.navigate("agregarLibro") },
                        onVolver = { navController.popBackStack() }
                    )
                }

                composable("agregarLibro") {
                    AgregarLibroScreen(onGuardado = { navController.popBackStack() })
                }

                composable(
                    "editarLibro/{libroId}",
                    arguments = listOf(navArgument("libroId") { type = NavType.StringType })
                ) { backStack ->
                    val libroId = backStack.arguments?.getString("libroId") ?: ""
                    EditarLibroScreen(
                        libroId = libroId,
                        onGuardado = { navController.popBackStack() },
                        onEliminado = { navController.popBackStack("gestionarLibros", inclusive = false) }
                    )
                }
            }

            if (mostrarBusqueda) {
                BusquedaGlobalOverlay(
                    libros = todosLosLibros,
                    onCerrar = { mostrarBusqueda = false },
                    onSeleccionar = { id ->
                        mostrarBusqueda = false
                        navController.navigate("detalle/$id")
                    }
                )
            }
        }
    }
}

@Composable
private fun BusquedaGlobalOverlay(
    libros: List<Book>,
    onCerrar: () -> Unit,
    onSeleccionar: (String) -> Unit
) {
    var texto by remember { mutableStateOf("") }
    val backgroundColor = themeColor(R.attr.appBackgroundColor)

    val resultados = if (texto.isBlank()) emptyList() else libros.filter {
        it.titulo.contains(texto, ignoreCase = true) ||
                it.autor.contains(texto, ignoreCase = true) ||
                it.categoria.contains(texto, ignoreCase = true)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onCerrar) {
                Icon(Icons.Filled.ArrowBack, contentDescription = "Cerrar búsqueda")
            }
            OutlinedTextField(
                value = texto,
                onValueChange = { texto = it },
                placeholder = { Text("Buscar por título, autor o género...") },
                singleLine = true,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(Modifier.height(12.dp))

        LazyColumn {
            items(resultados, key = { it.id }) { libro ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp)
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(libro.titulo, style = MaterialTheme.typography.titleMedium)
                            Text(libro.autor, style = MaterialTheme.typography.bodySmall)
                        }
                        IconButton(onClick = { onSeleccionar(libro.id) }) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_search),
                                contentDescription = "Ver ${libro.titulo}"
                            )
                        }
                    }
                }
            }
        }
    }
}