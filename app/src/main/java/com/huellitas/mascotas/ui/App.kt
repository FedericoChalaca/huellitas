package com.huellitas.mascotas.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
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
            when (session) {
                Session.Loading -> Splash()
                Session.LoggedOut -> AuthNav(vm)
                is Session.LoggedIn -> MainNav(vm)
            }
        }
    }
}

@Composable
private fun Splash() {
    Column(Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
        Text("🐾", fontSize = 64.sp)
        Text("Huellitas", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
    }
}

/** Antes de entrar: login y registro. */
@Composable
private fun AuthNav(vm: MainViewModel) {
    val nav = rememberNavController()
    NavHost(nav, startDestination = "login") {
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
    NavHost(nav, startDestination = "pets") {
        // Pestañas
        composable("pets") { PetsScreen(nav, vm) }
        composable("pendientes") { PendientesScreen(nav, vm) }
        composable("citas") { CitasScreen(nav) }
        composable("seguro") { SeguroScreen(nav) }
        composable("mas") { MasScreen(nav) }

        // Más
        composable("settings") { SettingsScreen(nav, vm) }
        composable("credits") { CreditsScreen(nav) }

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
