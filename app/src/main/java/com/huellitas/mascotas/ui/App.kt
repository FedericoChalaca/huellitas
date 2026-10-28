package com.huellitas.mascotas.ui

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.Crossfade
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.huellitas.mascotas.MainViewModel
import com.huellitas.mascotas.Session
import com.huellitas.mascotas.ui.citas.CitaDetalleScreen
import com.huellitas.mascotas.ui.citas.CitasScreen
import com.huellitas.mascotas.ui.citas.PedirCitaScreen
import com.huellitas.mascotas.ui.desparasitacion.DesparasitacionFormScreen
import com.huellitas.mascotas.ui.mas.AcercaScreen
import com.huellitas.mascotas.ui.mas.MasScreen
import com.huellitas.mascotas.ui.medicamentos.CotizacionScreen
import com.huellitas.mascotas.ui.medicamentos.HistorialCotizacionesScreen
import com.huellitas.mascotas.ui.medicamentos.MedicamentosScreen
import com.huellitas.mascotas.ui.pendientes.PendientesScreen
import com.huellitas.mascotas.ui.seguro.AfiliarScreen
import com.huellitas.mascotas.ui.seguro.PlanesScreen
import com.huellitas.mascotas.ui.seguro.PolizaScreen
import com.huellitas.mascotas.ui.seguro.SeguroScreen
import com.huellitas.mascotas.ui.urgencias.PrimerosAuxiliosScreen
import com.huellitas.mascotas.ui.urgencias.UrgenciasScreen

@Composable
fun HuellitasRoot(vm: MainViewModel = viewModel()) {
    val settings by vm.settings.collectAsStateWithLifecycle()
    val session by vm.session.collectAsStateWithLifecycle()
    HuellitasTheme(settings.theme) {
        Surface(Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
            // Entrar y salir de la cuenta se funde en vez de cambiar de golpe.
            val etapa = when (session) {
                Session.Loading -> 0
                Session.LoggedOut -> 1
                is Session.LoggedIn -> 2
            }
            Crossfade(targetState = etapa, animationSpec = tween(400), label = "sesion") { e ->
                when (e) {
                    0 -> Splash()
                    1 -> AuthNav(vm)
                    else -> MainNav(vm)
                }
            }
        }
    }
}

@Composable
private fun Splash() {
    // El logo entra con un rebote y el nombre aparece detrás.
    val logo = remember { Animatable(0.4f) }
    LaunchedEffect(Unit) { logo.animateTo(1f, spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow)) }
    val sobre = colorSobreEncabezado()
    Column(
        Modifier.fillMaxSize().background(encabezadoBrush()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Box(
            Modifier.size(120.dp).graphicsLayer {
                scaleX = logo.value
                scaleY = logo.value
            }.background(sobre.copy(alpha = 0.18f), CircleShape),
            contentAlignment = Alignment.Center,
        ) { Text("🐾", fontSize = 60.sp) }
        Spacer(Modifier.height(20.dp))
        Text("Huellitas", style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold, color = sobre, modifier = Modifier.aparecer(2))
        Text("Control de mascotas y seguro", style = MaterialTheme.typography.bodyLarge, color = sobre.copy(alpha = 0.85f), modifier = Modifier.aparecer(4))
    }
}

// ---------------------------------------------------------------- Transiciones entre pantallas

private val RUTAS_TABS = Tabs.map { it.route }.toSet()

private fun NavBackStackEntry.esTab() = destination.route in RUTAS_TABS

private const val DURACION = 320

// Entre pestañas: fundido corto. Hacia una pantalla interna: entra deslizando desde la derecha
// y la de atrás se corre un poco; al volver, al revés.
private fun AnimatedContentTransitionScope<NavBackStackEntry>.entrar(): EnterTransition =
    if (initialState.esTab() && targetState.esTab()) fadeIn(tween(220))
    else slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Start, tween(DURACION)) + fadeIn(tween(DURACION))

private fun AnimatedContentTransitionScope<NavBackStackEntry>.salir(): ExitTransition =
    if (initialState.esTab() && targetState.esTab()) fadeOut(tween(160))
    else slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Start, tween(DURACION), targetOffset = { it / 4 }) + fadeOut(tween(DURACION))

private fun AnimatedContentTransitionScope<NavBackStackEntry>.volverEntrar(): EnterTransition =
    if (initialState.esTab() && targetState.esTab()) fadeIn(tween(220))
    else slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.End, tween(DURACION), initialOffset = { it / 4 }) + fadeIn(tween(DURACION))

private fun AnimatedContentTransitionScope<NavBackStackEntry>.volverSalir(): ExitTransition =
    if (initialState.esTab() && targetState.esTab()) fadeOut(tween(160))
    else slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.End, tween(DURACION)) + fadeOut(tween(DURACION))

/** Antes de entrar: login y registro. */
@Composable
private fun AuthNav(vm: MainViewModel) {
    val nav = rememberNavController()
    NavHost(
        nav, startDestination = "login",
        enterTransition = { entrar() },
        exitTransition = { salir() },
        popEnterTransition = { volverEntrar() },
        popExitTransition = { volverSalir() },
    ) {
        composable("login") { LoginScreen(vm, onRegister = { nav.navigate("register") }) }
        composable("register") { RegisterScreen(vm, onBack = { nav.popBackStack() }) }
    }
}

/**
 * Ya adentro. Las cinco pestañas de abajo (pets, pendientes, citas, seguro, mas) y, aparte,
 * las pantallas a las que se llega desde ellas. Cada pantalla nueva se registra aquí con su ruta.
 */
@Composable
private fun MainNav(vm: MainViewModel) {
    val nav = rememberNavController()
    val long = { name: String -> navArgument(name) { type = NavType.LongType } }
    NavHost(
        nav, startDestination = "pets",
        enterTransition = { entrar() },
        exitTransition = { salir() },
        popEnterTransition = { volverEntrar() },
        popExitTransition = { volverSalir() },
    ) {
        // Pestañas
        composable("pets") { PetsScreen(nav, vm) }
        composable("pendientes") { PendientesScreen(nav, vm) }
        composable("citas") { CitasScreen(nav) }
        composable("seguro") { SeguroScreen(nav) }
        composable("mas") { MasScreen(nav) }

        // Más
        composable("settings") { SettingsScreen(nav, vm) }
        composable("credits") { CreditsScreen(nav) }
        composable("acerca") { AcercaScreen(nav) }

        // Mascotas, vacunas y desparasitaciones
        composable("pet/{id}", arguments = listOf(long("id"))) { entry ->
            PetDetailScreen(nav, vm, entry.arguments?.getLong("id") ?: 0L)
        }
        composable("pet/form/{id}", arguments = listOf(long("id"))) { entry ->
            PetFormScreen(nav, vm, entry.arguments?.getLong("id") ?: 0L)
        }
        composable("vaccine/form/{petId}/{id}", arguments = listOf(long("petId"), long("id"))) { entry ->
            VaccineFormScreen(nav, vm, entry.arguments?.getLong("petId") ?: 0L, entry.arguments?.getLong("id") ?: 0L)
        }
        composable("deworming/form/{petId}/{id}", arguments = listOf(long("petId"), long("id"))) { entry ->
            DesparasitacionFormScreen(nav, entry.arguments?.getLong("petId") ?: 0L, entry.arguments?.getLong("id") ?: 0L)
        }

        // Citas: petId = 0 si no hay mascota elegida; motivo = "vacunacion", "desparasitacion"… o "-"
        composable(
            "cita/pedir/{petId}/{motivo}",
            arguments = listOf(long("petId"), navArgument("motivo") { type = NavType.StringType }),
        ) { entry ->
            PedirCitaScreen(nav, entry.arguments?.getLong("petId") ?: 0L, entry.arguments?.getString("motivo") ?: "-")
        }
        composable("cita/{id}", arguments = listOf(long("id"))) { entry ->
            CitaDetalleScreen(nav, entry.arguments?.getLong("id") ?: 0L)
        }

        // Seguro
        composable("seguro/poliza/{petId}", arguments = listOf(long("petId"))) { entry ->
            PolizaScreen(nav, entry.arguments?.getLong("petId") ?: 0L)
        }
        composable("seguro/planes") { PlanesScreen(nav) }
        composable("seguro/afiliar/{petId}", arguments = listOf(long("petId"))) { entry ->
            AfiliarScreen(nav, entry.arguments?.getLong("petId") ?: 0L)
        }

        // Urgencias
        composable("urgencias") { UrgenciasScreen(nav) }
        composable("urgencias/auxilios") { PrimerosAuxiliosScreen(nav) }

        // Medicamentos y cotización
        composable("medicamentos") { MedicamentosScreen(nav) }
        composable("cotizacion") { CotizacionScreen(nav) }
        composable("cotizaciones") { HistorialCotizacionesScreen(nav) }
    }
}
