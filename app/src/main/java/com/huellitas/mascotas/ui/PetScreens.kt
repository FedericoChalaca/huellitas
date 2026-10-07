@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)

package com.huellitas.mascotas.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.huellitas.mascotas.MainViewModel
import com.huellitas.mascotas.data.Dates
import com.huellitas.mascotas.data.Pet
import com.huellitas.mascotas.data.SPECIES
import com.huellitas.mascotas.data.Vaccine
import com.huellitas.mascotas.data.speciesEmoji
import com.huellitas.mascotas.data.vaccineStatus
import kotlinx.coroutines.launch

// ---------------------------------------------------------------- Listado

@Composable
fun PetsScreen(nav: NavController, vm: MainViewModel) {
    val pets by vm.pets.collectAsStateWithLifecycle()
    val upcoming by vm.upcoming.collectAsStateWithLifecycle()
    val settings by vm.settings.collectAsStateWithLifecycle()
    val today = remember { Dates.today() }

    TabScaffold(
        nav, "pets", "Mis mascotas",
        fab = {
            FloatingActionButton(onClick = { nav.navigate("pet/form/0") }) {
                Icon(Icons.Filled.Add, contentDescription = "Agregar mascota")
            }
        },
    ) { padding ->
        if (pets.isEmpty()) {
            EmptyState("🐾", "Aún no hay mascotas", "Toca + para agregar la primera.", Modifier.padding(padding))
        } else {
            LazyColumn(
                Modifier.padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                items(pets, key = { it.id }) { pet ->
                    // La lista llega ordenada por fecha: la primera de esta mascota es su próxima vacuna.
                    val next = upcoming.firstOrNull { it.vaccine.petId == pet.id }?.vaccine?.nextDueDay
                    PetCard(pet, vaccineStatus(next, today, settings.warnDays), today) { nav.navigate("pet/${pet.id}") }
                }
            }
        }
    }
}

@Composable
private fun PetCard(pet: Pet, status: com.huellitas.mascotas.data.VaccineStatus, today: Long, onClick: () -> Unit) {
    Card(onClick = onClick, modifier = Modifier.fillMaxWidth()) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            SpeciesBadge(speciesEmoji(pet.species))
            Column(Modifier.weight(1f)) {
                Text(pet.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                val line = listOfNotNull(pet.species, pet.breed.ifBlank { null }, pet.birthDay?.let { Dates.age(it, today) })
                Text(line.joinToString(" · "), style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            StatusChip(status)
        }
    }
}

// ---------------------------------------------------------------- Detalle

@Composable
fun PetDetailScreen(nav: NavController, vm: MainViewModel, petId: Long) {
    val pet by remember(petId) { vm.pet(petId) }.collectAsStateWithLifecycle(initialValue = null)
    val vaccines by remember(petId) { vm.vaccines(petId) }.collectAsStateWithLifecycle(initialValue = emptyList())
    val settings by vm.settings.collectAsStateWithLifecycle()
    val today = remember { Dates.today() }
    var askDeletePet by remember { mutableStateOf(false) }
    var vaccineToDelete by remember { mutableStateOf<Vaccine?>(null) }

    BackScaffold(
        title = pet?.name ?: "Mascota",
        onBack = { nav.popBackStack() },
        actions = {
            IconButton(onClick = { nav.navigate("pet/form/$petId") }) { Icon(Icons.Filled.Edit, contentDescription = "Editar") }
            IconButton(onClick = { askDeletePet = true }) { Icon(Icons.Filled.Delete, contentDescription = "Borrar") }
        },
    ) { padding ->
        val current = pet
        if (current == null) {
            Box(Modifier.padding(padding))
        } else {
            LazyColumn(
                Modifier.padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                item { PetHeader(current, today) }
                item {
                    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Vacunas", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                        FilledTonalButton(onClick = { nav.navigate("vaccine/form/$petId/0") }) {
                            Icon(Icons.Filled.Add, contentDescription = null)
                            Spacer(Modifier.width(6.dp))
                            Text("Registrar")
                        }
                    }
                }
                if (vaccines.isEmpty()) {
                    item { Text("Todavía no hay vacunas registradas.", color = MaterialTheme.colorScheme.onSurfaceVariant) }
                }
                items(vaccines, key = { it.id }) { v ->
                    VaccineRow(
                        v, today, settings.warnDays,
                        onEdit = { nav.navigate("vaccine/form/$petId/${v.id}") },
                        onDelete = { vaccineToDelete = v },
                    )
                }
            }
        }
    }

    if (askDeletePet) {
        ConfirmDialog(
            title = "¿Borrar a ${pet?.name ?: "esta mascota"}?",
            text = "Se borran también sus vacunas. No se puede deshacer.",
            confirmLabel = "Borrar",
            onConfirm = {
                askDeletePet = false
                vm.deletePet(petId)
                nav.popBackStack()
            },
            onDismiss = { askDeletePet = false },
        )
    }
    vaccineToDelete?.let { v ->
        ConfirmDialog(
            title = "¿Borrar la vacuna ${v.name}?",
            text = "No se puede deshacer.",
            confirmLabel = "Borrar",
            onConfirm = {
                vm.deleteVaccine(v.id)
                vaccineToDelete = null
            },
            onDismiss = { vaccineToDelete = null },
        )
    }
}

@Composable
private fun PetHeader(pet: Pet, today: Long) {
    Card(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                SpeciesBadge(speciesEmoji(pet.species), size = 72)
                Column {
                    Text(pet.name, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                    Text(
                        listOfNotNull(pet.species, pet.breed.ifBlank { null }).joinToString(" · "),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
            pet.birthDay?.let { Text("Nació el ${Dates.format(it)} (${Dates.age(it, today)})") }
            if (pet.notes.isNotBlank()) Text(pet.notes, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun VaccineRow(v: Vaccine, today: Long, warnDays: Int, onEdit: () -> Unit, onDelete: () -> Unit) {
    Card(onClick = onEdit, modifier = Modifier.fillMaxWidth()) {
        Row(Modifier.padding(start = 16.dp, top = 12.dp, bottom = 12.dp, end = 4.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(v.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text("Aplicada: ${Dates.format(v.appliedDay)}", style = MaterialTheme.typography.bodyMedium)
                v.nextDueDay?.let {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Próxima: ${Dates.format(it)}", style = MaterialTheme.typography.bodyMedium)
                        StatusChip(vaccineStatus(it, today, warnDays))
                    }
                }
                if (v.notes.isNotBlank()) Text(v.notes, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            IconButton(onClick = onDelete) { Icon(Icons.Filled.Delete, contentDescription = "Borrar la vacuna") }
        }
    }
}

// ---------------------------------------------------------------- Formularios

@Composable
fun PetFormScreen(nav: NavController, vm: MainViewModel, petId: Long) {
    var name by remember { mutableStateOf("") }
    var species by remember { mutableStateOf(SPECIES.first()) }
    var breed by remember { mutableStateOf("") }
    var birth by remember { mutableStateOf<Long?>(null) }
    var notes by remember { mutableStateOf("") }
    var loaded by remember { mutableStateOf(petId == 0L) }
    var saving by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(petId) {
        if (petId != 0L) {
            vm.getPet(petId)?.let {
                name = it.name
                species = it.species
                breed = it.breed
                birth = it.birthDay
                notes = it.notes
            }
            loaded = true
        }
    }

    BackScaffold(if (petId == 0L) "Nueva mascota" else "Editar mascota", onBack = { nav.popBackStack() }) { padding ->
        Column(
            Modifier.padding(padding).verticalScroll(rememberScrollState()).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            OutlinedTextField(name, { name = it }, label = { Text("Nombre") }, singleLine = true, modifier = Modifier.fillMaxWidth())
            Text("Especie", style = MaterialTheme.typography.labelLarge)
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                SPECIES.forEach { s ->
                    FilterChip(selected = species == s, onClick = { species = s }, label = { Text("${speciesEmoji(s)} $s") })
                }
            }
            OutlinedTextField(breed, { breed = it }, label = { Text("Raza (opcional)") }, singleLine = true, modifier = Modifier.fillMaxWidth())
            DateField("Fecha de nacimiento (opcional)", birth, { birth = it }, clearable = true)
            OutlinedTextField(
                notes, { notes = it },
                label = { Text("Notas (alergias, veterinario…)") },
                minLines = 3,
                modifier = Modifier.fillMaxWidth(),
            )
            Button(
                onClick = {
                    saving = true
                    scope.launch {
                        val saved = vm.savePet(
                            Pet(id = petId, ownerId = 0, name = name.trim(), species = species, breed = breed.trim(), birthDay = birth, notes = notes.trim()),
                        )
                        saving = false
                        if (saved != null) nav.popBackStack()
                    }
                },
                enabled = loaded && !saving && name.isNotBlank(),
                modifier = Modifier.fillMaxWidth(),
            ) { Text("Guardar") }
        }
    }
}

private val COMMON_VACCINES = listOf("Rabia", "Triple", "Parvovirus", "Moquillo", "Antiparasitario")

@Composable
fun VaccineFormScreen(nav: NavController, vm: MainViewModel, petId: Long, vaccineId: Long) {
    var name by remember { mutableStateOf("") }
    var applied by remember { mutableStateOf<Long?>(Dates.today()) }
    var next by remember { mutableStateOf<Long?>(null) }
    var notes by remember { mutableStateOf("") }
    var loaded by remember { mutableStateOf(vaccineId == 0L) }
    var error by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(vaccineId) {
        if (vaccineId != 0L) {
            vm.getVaccine(vaccineId)?.let {
                name = it.name
                applied = it.appliedDay
                next = it.nextDueDay
                notes = it.notes
            }
            loaded = true
        }
    }

    fun submit() {
        val appliedDay = applied ?: return
        val nextDay = next
        if (nextDay != null && nextDay < appliedDay) {
            error = "La próxima dosis debe ser después de la aplicada."
            return
        }
        scope.launch {
            vm.saveVaccine(Vaccine(id = vaccineId, petId = petId, name = name.trim(), appliedDay = appliedDay, nextDueDay = nextDay, notes = notes.trim()))
            nav.popBackStack()
        }
    }

    BackScaffold(if (vaccineId == 0L) "Registrar vacuna" else "Editar vacuna", onBack = { nav.popBackStack() }) { padding ->
        Column(
            Modifier.padding(padding).verticalScroll(rememberScrollState()).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            OutlinedTextField(name, { name = it }, label = { Text("Vacuna") }, singleLine = true, modifier = Modifier.fillMaxWidth())
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                COMMON_VACCINES.forEach { s -> SuggestionChip(onClick = { name = s }, label = { Text(s) }) }
            }
            DateField("Fecha de aplicación", applied, { applied = it ?: applied; error = null })
            DateField("Próxima dosis (opcional)", next, { next = it; error = null }, clearable = true)
            Text("Calcular la próxima dosis desde la aplicación:", style = MaterialTheme.typography.labelLarge)
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("En 1 mes" to 1L, "En 6 meses" to 6L, "En 1 año" to 12L).forEach { (label, months) ->
                    AssistChip(onClick = { applied?.let { next = Dates.addMonths(it, months); error = null } }, label = { Text(label) })
                }
            }
            OutlinedTextField(notes, { notes = it }, label = { Text("Notas (lote, veterinario…)") }, minLines = 2, modifier = Modifier.fillMaxWidth())
            error?.let { Text(it, color = MaterialTheme.colorScheme.error) }
            Button(
                onClick = { submit() },
                enabled = loaded && name.isNotBlank(),
                modifier = Modifier.fillMaxWidth(),
            ) { Text("Guardar") }
        }
    }
}
