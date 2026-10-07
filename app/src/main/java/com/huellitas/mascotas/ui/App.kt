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

/** Ya adentro: las cuatro secciones del menú y las pantallas de detalle y formulario. */
@Composable
private fun MainNav(vm: MainViewModel) {
    val nav = rememberNavController()
    val long = { name: String -> navArgument(name) { type = NavType.LongType } }
    NavHost(nav, startDestination = "pets") {
        composable("pets") { PetsScreen(nav, vm) }
        composable("upcoming") { UpcomingScreen(nav, vm) }
        composable("settings") { SettingsScreen(nav, vm) }
        composable("credits") { CreditsScreen(nav) }
        composable("pet/{id}", arguments = listOf(long("id"))) { entry ->
            PetDetailScreen(nav, vm, entry.arguments?.getLong("id") ?: 0L)
        }
        composable("pet/form/{id}", arguments = listOf(long("id"))) { entry ->
            PetFormScreen(nav, vm, entry.arguments?.getLong("id") ?: 0L)
        }
        composable("vaccine/form/{petId}/{id}", arguments = listOf(long("petId"), long("id"))) { entry ->
            VaccineFormScreen(nav, vm, entry.arguments?.getLong("petId") ?: 0L, entry.arguments?.getLong("id") ?: 0L)
        }
    }
}
