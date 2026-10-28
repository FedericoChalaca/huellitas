@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)

package com.huellitas.mascotas.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Surface
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.huellitas.mascotas.MainViewModel
import com.huellitas.mascotas.data.Dates
import com.huellitas.mascotas.data.Pet
import com.huellitas.mascotas.data.SPECIES
import com.huellitas.mascotas.data.Vaccine
import com.huellitas.mascotas.data.VaccineStatus
import com.huellitas.mascotas.data.speciesEmoji
import com.huellitas.mascotas.data.vaccineStatus
import com.huellitas.mascotas.ui.desparasitacion.DesparasitacionesSection
import kotlinx.coroutines.launch

// ---------------------------------------------------------------- Listado

@Composable
fun PetsScreen(nav: NavController, vm: MainViewModel) {
    val pets by vm.pets.collectAsStateWithLifecycle()
    val upcoming by vm.upcoming.collectAsStateWithLifecycle()
    val settings by vm.settings.collectAsStateWithLifecycle()
    val today = remember { Dates.today() }
    val estados = upcoming.map { vaccineStatus(it.vaccine.nextDueDay, today, settings.warnDays) }

    TabScaffold(
        nav, "pets", "Mis mascotas",
        subtitle = when (pets.size) {
            0 -> "Empieza agregando a tu primera mascota"
            1 -> "1 mascota en casa"
            else -> "${pets.size} mascotas en casa"
        },
        fab = { BotonFlotante("Agregar", Icons.Filled.Add) { nav.navigate("pet/form/0") } },
    ) { padding ->
        if (pets.isEmpty()) {
            EmptyState("🐶", "Aún no hay mascotas", "Toca «Agregar» para registrar la primera y llevar sus vacunas al día.", Modifier.padding(padding))
        } else {
            LazyColumn(
                Modifier.padding(padding),
                // El espacio de abajo evita que el botón tape la última mascota.
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 96.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                item(key = "resumen") {
                    ResumenVacunas(
                        vencidas = estados.count { it == VaccineStatus.OVERDUE },
                        pronto = estados.count { it == VaccineStatus.SOON },
                        alDia = estados.count { it == VaccineStatus.OK },
                        modifier = Modifier.aparecer(),
                    )
                }
                itemsIndexed(pets, key = { _, pet -> pet.id }) { i, pet ->
                    // La lista llega ordenada por fecha: la primera de esta mascota es su próxima vacuna.
                    val next = upcoming.firstOrNull { it.vaccine.petId == pet.id }?.vaccine?.nextDueDay
                    PetCard(
                        pet, vaccineStatus(next, today, settings.warnDays), today,
                        modifier = Modifier.animateItem().aparecer(i + 1),
                    ) { nav.navigate("pet/${pet.id}") }
                }
            }
        }
    }
}

/** Tres contadores de vacunas: vencidas, pronto y al día. */
@Composable
private fun ResumenVacunas(vencidas: Int, pronto: Int, alDia: Int, modifier: Modifier = Modifier) {
    val scheme = MaterialTheme.colorScheme
    Row(modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        Contador("Vencidas", vencidas, scheme.errorContainer, scheme.onErrorContainer, Modifier.weight(1f))
        Contador("Pronto", pronto, scheme.secondaryContainer, scheme.onSecondaryContainer, Modifier.weight(1f))
        Contador("Al día", alDia, scheme.primaryContainer, scheme.onPrimaryContainer, Modifier.weight(1f))
    }
}

@Composable
private fun Contador(etiqueta: String, valor: Int, fondo: Color, letra: Color, modifier: Modifier) {
    Surface(modifier, color = fondo, contentColor = letra, shape = RoundedCornerShape(18.dp)) {
        Column(Modifier.padding(vertical = 12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            // El número cambia con un pequeño salto cuando se registra o vence una vacuna.
            AnimatedContent(
                targetState = valor,
                transitionSpec = { (scaleIn(spring(dampingRatio = Spring.DampingRatioMediumBouncy)) + fadeIn()) togetherWith fadeOut() },
                label = "contador",
            ) { n -> Text("$n", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold) }
            Text(etiqueta, style = MaterialTheme.typography.labelMedium)
        }
    }
}

@Composable
private fun PetCard(pet: Pet, status: VaccineStatus, today: Long, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Tarjeta(onClick = onClick, modifier = modifier.fillMaxWidth()) {
        Row(Modifier.height(IntrinsicSize.Min)) {
            // Rayita de color a la izquierda: se ve el estado de la mascota de un vistazo.
            Box(Modifier.width(6.dp).fillMaxHeight().background(colorDeEstado(status)))
            Row(
                Modifier.weight(1f).padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                SpeciesBadge(speciesEmoji(pet.species))
                Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(pet.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    val line = listOfNotNull(pet.species, pet.breed.ifBlank { null }, pet.birthDay?.let { Dates.age(it, today) })
                    Text(line.joinToString(" · "), style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                StatusChip(status)
            }
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
            Box(Modifier.padding(padding).fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
        } else {
            LazyColumn(
                Modifier.padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                item(key = "cabecera") { PetHeader(current, today, Modifier.aparecer()) }
                item(key = "cita") {
                    FilledTonalButton(
                        onClick = { nav.navigate("cita/pedir/$petId/-") },
                        modifier = Modifier.fillMaxWidth().height(50.dp).aparecer(1),
                        shape = RoundedCornerShape(16.dp),
                    ) {
                        Icon(Icons.Filled.DateRange, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text("Pedir cita para ${current.name}", fontWeight = FontWeight.Bold)
                    }
                }
                item(key = "tituloVacunas") {
                    SeccionTitulo("Vacunas", Modifier.padding(top = 8.dp).aparecer(2)) {
                        FilledTonalButton(onClick = { nav.navigate("vaccine/form/$petId/0") }) {
                            Icon(Icons.Filled.Add, contentDescription = null)
                            Spacer(Modifier.width(6.dp))
                            Text("Registrar")
                        }
                    }
                }
                if (vaccines.isEmpty()) {
                    item(key = "sinVacunas") {
                        VacioEnLinea("💉", "Todavía no hay vacunas registradas.", Modifier.aparecer(3))
                    }
                }
                itemsIndexed(vaccines, key = { _, v -> v.id }) { i, v ->
                    VaccineRow(
                        v, today, settings.warnDays,
                        modifier = Modifier.animateItem().aparecer(i + 3),
                        onEdit = { nav.navigate("vaccine/form/$petId/${v.id}") },
                        onDelete = { vaccineToDelete = v },
                    )
                }
                // Desparasitaciones: la dibuja Nicolás (N-04) en su propio archivo.
                item(key = "desparasitaciones") { DesparasitacionesSection(nav, petId) }
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

/** Para listas cortas dentro de un detalle: un recuadro punteado suave con dibujo y texto. */
@Composable
fun VacioEnLinea(emoji: String, texto: String, modifier: Modifier = Modifier) {
    Surface(
        modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
        shape = RoundedCornerShape(18.dp),
    ) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(14.dp)) {
            Text(emoji, fontSize = 28.sp, modifier = Modifier.flotar(3f))
            Text(texto, color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Composable
private fun PetHeader(pet: Pet, today: Long, modifier: Modifier = Modifier) {
    val sobre = colorSobreEncabezado()
    Box(
        modifier.fillMaxWidth().background(encabezadoBrush(), RoundedCornerShape(24.dp)),
    ) {
        HuellasDeAdorno(Modifier.matchParentSize())
        Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Box(
                    Modifier.size(84.dp).background(sobre.copy(alpha = 0.2f), CircleShape),
                    contentAlignment = Alignment.Center,
                ) { Text(speciesEmoji(pet.species), fontSize = 44.sp, modifier = Modifier.flotar(3f)) }
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(pet.name, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = sobre)
                    Text(
                        listOfNotNull(pet.species, pet.breed.ifBlank { null }).joinToString(" · "),
                        color = sobre.copy(alpha = 0.85f),
                    )
                }
            }
            pet.birthDay?.let {
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    PildoraClara("🎂 ${Dates.format(it)}")
                    PildoraClara("⏳ ${Dates.age(it, today)}")
                }
            }
            if (pet.notes.isNotBlank()) {
                Surface(color = sobre.copy(alpha = 0.14f), contentColor = sobre, shape = RoundedCornerShape(14.dp)) {
                    Row(Modifier.padding(12.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Icon(Icons.Filled.Info, contentDescription = null, modifier = Modifier.size(18.dp))
                        Text(pet.notes, style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        }
    }
}

/** Píldora translúcida para datos sobre el encabezado verde. */
@Composable
private fun PildoraClara(texto: String) {
    val sobre = colorSobreEncabezado()
    Surface(color = sobre.copy(alpha = 0.18f), contentColor = sobre, shape = RoundedCornerShape(50)) {
        Text(texto, style = MaterialTheme.typography.labelLarge, modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp))
    }
}

@Composable
private fun VaccineRow(v: Vaccine, today: Long, warnDays: Int, modifier: Modifier = Modifier, onEdit: () -> Unit, onDelete: () -> Unit) {
    val status = vaccineStatus(v.nextDueDay, today, warnDays)
    Tarjeta(onClick = onEdit, modifier = modifier.fillMaxWidth()) {
        Row(Modifier.height(IntrinsicSize.Min)) {
            Box(Modifier.width(6.dp).fillMaxHeight().background(colorDeEstado(status)))
            Row(
                Modifier.weight(1f).padding(start = 14.dp, top = 12.dp, bottom = 12.dp, end = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                SpeciesBadge("💉", size = 44)
                Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(v.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text("Aplicada: ${Dates.format(v.appliedDay)}", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    v.nextDueDay?.let {
                        Text("Próxima: ${Dates.format(it)} · ${textoDias(it, today)}", style = MaterialTheme.typography.bodyMedium)
                        StatusChip(status)
                    }
                    if (v.notes.isNotBlank()) Text(v.notes, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                IconButton(onClick = onDelete) {
                    Icon(Icons.Filled.Delete, contentDescription = "Borrar la vacuna", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    }
}

// ---------------------------------------------------------------- Formularios

/** Dibujo grande arriba de los formularios; cambia con un rebote cuando cambia [emoji]. */
@Composable
fun CabeceraFormulario(emoji: String, texto: String) {
    Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        AnimatedContent(
            targetState = emoji,
            transitionSpec = { (scaleIn(spring(dampingRatio = Spring.DampingRatioMediumBouncy), initialScale = 0.5f) + fadeIn()) togetherWith fadeOut() },
            label = "dibujoFormulario",
        ) { e -> Ilustracion(e, size = 120) }
        Text(
            texto,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
fun PetFormScreen(nav: NavController, vm: MainViewModel, petId: Long) {
    var name by remember { mutableStateOf("") }
    var species by remember { mutableStateOf(SPECIES.first()) }
    var breed by remember { mutableStateOf("") }
    var birth by remember { mutableStateOf<Long?>(null) }
    var notes by remember { mutableStateOf("") }
    var loaded by remember { mutableStateOf(petId == 0L) }
    var saving by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }
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
            CabeceraFormulario(speciesEmoji(species), if (name.isBlank()) "¿Cómo se llama?" else "¡Hola, ${name.trim()}!")
            OutlinedTextField(
                name, { name = it },
                label = { Text("Nombre") },
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth(),
            )
            Text("Especie", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                SPECIES.forEach { s ->
                    FilterChip(
                        selected = species == s,
                        onClick = { species = s },
                        label = { Text("${speciesEmoji(s)} $s") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primary,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
                        ),
                    )
                }
            }
            OutlinedTextField(
                breed, { breed = it },
                label = { Text("Raza (opcional)") },
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth(),
            )
            DateField("Fecha de nacimiento (opcional)", birth, { birth = it }, clearable = true)
            OutlinedTextField(
                notes, { notes = it },
                label = { Text("Notas (alergias, veterinario…)") },
                minLines = 3,
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth(),
            )
            MensajeError(error)
            BotonPrincipal(
                "Guardar",
                onClick = {
                    if (saving) return@BotonPrincipal
                    saving = true
                    scope.launch {
                        val saved = vm.savePet(
                            Pet(id = petId, ownerId = 0, name = name.trim(), species = species, breed = breed.trim(), birthDay = birth, notes = notes.trim()),
                        )
                        saving = false
                        if (saved != null) nav.popBackStack() else error = "No se pudo guardar. Vuelve a intentarlo."
                    }
                },
                enabled = loaded && !saving && name.isNotBlank(),
                cargando = saving,
                textoCargando = "Guardando…",
            )
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
    var saving by remember { mutableStateOf(false) }
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
        if (saving) return
        val appliedDay = applied ?: return
        val nextDay = next
        if (nextDay != null && nextDay < appliedDay) {
            error = "La próxima dosis debe ser después de la aplicada."
            return
        }
        saving = true
        scope.launch {
            val saved = vm.saveVaccine(
                Vaccine(
                    id = vaccineId,
                    petId = petId,
                    name = name.trim(),
                    appliedDay = appliedDay,
                    nextDueDay = nextDay,
                    notes = notes.trim(),
                ),
            )
            saving = false
            if (saved) nav.popBackStack() else error = "No se pudo guardar. Vuelve a intentarlo."
        }
    }

    BackScaffold(if (vaccineId == 0L) "Registrar vacuna" else "Editar vacuna", onBack = { nav.popBackStack() }) { padding ->
        Column(
            Modifier.padding(padding).verticalScroll(rememberScrollState()).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            CabeceraFormulario("💉", "Anota la vacuna y te avisamos cuando toque la próxima.")
            OutlinedTextField(
                name, { name = it },
                label = { Text("Vacuna") },
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth(),
            )
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                COMMON_VACCINES.forEach { s -> SuggestionChip(onClick = { name = s }, label = { Text(s) }) }
            }
            DateField("Fecha de aplicación", applied, { applied = it ?: applied; error = null })
            DateField("Próxima dosis (opcional)", next, { next = it; error = null }, clearable = true)
            Text("Calcular la próxima dosis desde la aplicación:", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("En 1 mes" to 1L, "En 6 meses" to 6L, "En 1 año" to 12L).forEach { (label, months) ->
                    AssistChip(
                        onClick = { applied?.let { next = Dates.addMonths(it, months); error = null } },
                        label = { Text(label) },
                        leadingIcon = { Icon(Icons.Filled.DateRange, contentDescription = null, modifier = Modifier.size(AssistChipDefaults.IconSize)) },
                    )
                }
            }
            OutlinedTextField(
                notes, { notes = it },
                label = { Text("Notas (lote, veterinario…)") },
                minLines = 2,
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth(),
            )
            MensajeError(error)
            BotonPrincipal(
                "Guardar",
                onClick = { submit() },
                enabled = loaded && !saving && name.isNotBlank(),
                cargando = saving,
                textoCargando = "Guardando…",
            )
        }
    }
}
