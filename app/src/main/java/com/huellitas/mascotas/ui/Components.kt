@file:OptIn(ExperimentalMaterial3Api::class)

package com.huellitas.mascotas.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.huellitas.mascotas.data.Dates
import com.huellitas.mascotas.data.VaccineStatus
import kotlinx.coroutines.delay
import kotlin.math.abs

private const val MILLIS_PER_DAY = 86_400_000L

data class Tab(val route: String, val label: String, val icon: ImageVector)

val Tabs = listOf(
    Tab("pets", "Mascotas", Icons.Filled.Favorite),
    Tab("pendientes", "Pendientes", Icons.Filled.Notifications),
    Tab("citas", "Citas", Icons.Filled.DateRange),
    Tab("seguro", "Seguro", Icons.Filled.CheckCircle),
    Tab("mas", "Más", Icons.Filled.Menu),
)

// ---------------------------------------------------------------- Animaciones

/**
 * Entrada suave de un elemento: sube un poco y aparece. [indice] escalona la entrada en las listas
 * (cada tarjeta llega un poquito después de la anterior); pasado el octavo ya no espera más.
 */
fun Modifier.aparecer(indice: Int = 0): Modifier = composed {
    val progreso = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        delay(indice.coerceIn(0, 8) * 45L)
        progreso.animateTo(1f, tween(380, easing = FastOutSlowInEasing))
    }
    graphicsLayer {
        alpha = progreso.value
        translationY = (1f - progreso.value) * 28.dp.toPx()
    }
}

/** Sube y baja despacio, para las ilustraciones (estados vacíos, logo). */
fun Modifier.flotar(amplitud: Float = 6f): Modifier = composed {
    val transicion = rememberInfiniteTransition(label = "flotar")
    val y by transicion.animateFloat(
        initialValue = -amplitud,
        targetValue = amplitud,
        animationSpec = infiniteRepeatable(tween(1600, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "flotarY",
    )
    graphicsLayer { translationY = y.dp.toPx() / 2 }
}

// ---------------------------------------------------------------- Estructura de las pantallas

/** Encabezado verde de las pestañas: título grande, subtítulo y huellitas de adorno. */
@Composable
fun EncabezadoTab(title: String, subtitle: String? = null) {
    val sobre = colorSobreEncabezado()
    Box(
        Modifier
            .fillMaxWidth()
            .background(encabezadoBrush(), RoundedCornerShape(bottomStart = 28.dp, bottomEnd = 28.dp)),
    ) {
        HuellasDeAdorno(Modifier.matchParentSize())
        Column(
            Modifier.statusBarsPadding().padding(start = 20.dp, end = 20.dp, top = 18.dp, bottom = 22.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            Text(
                title,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = sobre,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.aparecer(),
            )
            AnimatedContent(
                targetState = subtitle,
                transitionSpec = { fadeIn(tween(250)) togetherWith fadeOut(tween(150)) },
                label = "subtitulo",
            ) { texto ->
                if (texto != null) Text(texto, style = MaterialTheme.typography.bodyMedium, color = sobre.copy(alpha = 0.85f))
            }
        }
    }
}

/** Huellitas tenues en la esquina del encabezado. */
@Composable
fun HuellasDeAdorno(modifier: Modifier = Modifier) {
    Box(modifier) {
        Text("🐾", fontSize = 64.sp, modifier = Modifier.align(Alignment.TopEnd).offset(x = (-12).dp, y = 22.dp).rotate(-18f).alpha(0.14f))
        Text("🐾", fontSize = 30.sp, modifier = Modifier.align(Alignment.BottomEnd).offset(x = (-96).dp, y = (-8).dp).rotate(12f).alpha(0.12f))
    }
}

/** Pantalla de las cinco secciones del menú de abajo. */
@Composable
fun TabScaffold(
    nav: NavController,
    current: String,
    title: String,
    subtitle: String? = null,
    fab: @Composable () -> Unit = {},
    content: @Composable (PaddingValues) -> Unit,
) {
    Scaffold(
        topBar = { EncabezadoTab(title, subtitle) },
        bottomBar = {
            NavigationBar(containerColor = MaterialTheme.colorScheme.surfaceContainerLow) {
                Tabs.forEach { tab ->
                    val selected = tab.route == current
                    // Rebote corto del ícono de la pestaña elegida.
                    val escala by animateFloatAsState(
                        if (selected) 1.15f else 1f,
                        spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessMediumLow),
                        label = "iconoPestana",
                    )
                    NavigationBarItem(
                        selected = selected,
                        onClick = {
                            if (!selected) {
                                nav.navigate(tab.route) {
                                    popUpTo("pets") { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        },
                        icon = { Icon(tab.icon, contentDescription = null, modifier = Modifier.scale(escala)) },
                        label = { Text(tab.label, fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                            selectedTextColor = MaterialTheme.colorScheme.primary,
                            indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                        ),
                    )
                }
            }
        },
        floatingActionButton = fab,
        content = content,
    )
}

/** Pantalla interna (detalle o formulario): barra verde con la flecha para volver. */
@Composable
fun BackScaffold(
    title: String,
    onBack: () -> Unit,
    actions: @Composable RowScope.() -> Unit = {},
    content: @Composable (PaddingValues) -> Unit,
) {
    val sobre = colorSobreEncabezado()
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(title, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis) },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver") }
                },
                actions = actions,
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = colorEncabezado(),
                    titleContentColor = sobre,
                    navigationIconContentColor = sobre,
                    actionIconContentColor = sobre,
                ),
            )
        },
        content = content,
    )
}

/** Botón flotante con texto: entra con un rebote. */
@Composable
fun BotonFlotante(texto: String, icon: ImageVector, onClick: () -> Unit) {
    val entrada = remember { Animatable(0f) }
    LaunchedEffect(Unit) { entrada.animateTo(1f, spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow)) }
    ExtendedFloatingActionButton(
        onClick = onClick,
        icon = { Icon(icon, contentDescription = null) },
        text = { Text(texto, fontWeight = FontWeight.Bold) },
        containerColor = MaterialTheme.colorScheme.primary,
        contentColor = MaterialTheme.colorScheme.onPrimary,
        modifier = Modifier.graphicsLayer {
            scaleX = entrada.value
            scaleY = entrada.value
        },
    )
}

// ---------------------------------------------------------------- Tarjetas

/** Fondo de las tarjetas: blanco sobre la crema en claro, un café un poco más claro en oscuro. */
@Composable
fun tarjetaColores(): CardColors {
    val scheme = MaterialTheme.colorScheme
    return CardDefaults.cardColors(containerColor = if (scheme.esOscuro) scheme.surfaceContainerHigh else scheme.surfaceContainerLowest)
}

/** Tarjeta fija (no se toca). */
@Composable
fun Tarjeta(
    modifier: Modifier = Modifier,
    colors: CardColors = tarjetaColores(),
    content: @Composable ColumnScope.() -> Unit,
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        colors = colors,
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        content = content,
    )
}

/** Tarjeta que se toca: se encoge un poquito mientras el dedo está encima. */
@Composable
fun Tarjeta(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    colors: CardColors = tarjetaColores(),
    border: BorderStroke? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    val interaccion = remember { MutableInteractionSource() }
    val presionada by interaccion.collectIsPressedAsState()
    val escala by animateFloatAsState(if (presionada) 0.97f else 1f, spring(stiffness = Spring.StiffnessMedium), label = "presion")
    Card(
        onClick = onClick,
        modifier = modifier.graphicsLayer {
            scaleX = escala
            scaleY = escala
        },
        shape = RoundedCornerShape(20.dp),
        colors = colors,
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp, pressedElevation = 6.dp),
        border = border,
        interactionSource = interaccion,
        content = content,
    )
}

/** Título de una sección con una rayita de color y, a la derecha, una acción opcional. */
@Composable
fun SeccionTitulo(texto: String, modifier: Modifier = Modifier, accion: @Composable () -> Unit = {}) {
    Row(modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Box(Modifier.size(width = 4.dp, height = 22.dp).background(MaterialTheme.colorScheme.secondary, RoundedCornerShape(50)))
        Spacer(Modifier.width(10.dp))
        Text(texto, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
        accion()
    }
}

/** Renglón con ícono redondo pequeño y texto (fechas, direcciones, teléfonos). */
@Composable
fun DatoConIcono(icon: ImageVector, texto: String, color: Color = MaterialTheme.colorScheme.onSurfaceVariant) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
        Text(texto, style = MaterialTheme.typography.bodyMedium, color = color)
    }
}

/** Avisos de «Demostración académica»: una franja suave con el ícono de información. */
@Composable
fun NotaDemo(texto: String, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
        contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
        shape = RoundedCornerShape(14.dp),
    ) {
        Row(Modifier.padding(horizontal = 12.dp, vertical = 10.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Icon(Icons.Filled.Info, contentDescription = null, modifier = Modifier.size(18.dp))
            Text(texto, style = MaterialTheme.typography.bodySmall)
        }
    }
}

/** Mensaje de error que aparece y se va con animación. */
@Composable
fun MensajeError(texto: String?, modifier: Modifier = Modifier) {
    // Guarda el último texto para que no desaparezca de golpe mientras se encoge.
    val ultimo = remember { mutableStateOf("") }
    if (texto != null) ultimo.value = texto
    AnimatedVisibility(
        visible = texto != null,
        modifier = modifier,
        enter = expandVertically() + fadeIn(),
        exit = shrinkVertically() + fadeOut(),
    ) {
        Surface(
            color = MaterialTheme.colorScheme.errorContainer,
            contentColor = MaterialTheme.colorScheme.onErrorContainer,
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier.fillMaxWidth(),
        ) {
            Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Icon(Icons.Filled.Warning, contentDescription = null, modifier = Modifier.size(18.dp))
                Text(ultimo.value, style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}

/** Botón principal de los formularios: más alto, y con ruedita mientras trabaja. */
@Composable
fun BotonPrincipal(
    texto: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    cargando: Boolean = false,
    textoCargando: String = texto,
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier.fillMaxWidth().height(54.dp),
        shape = RoundedCornerShape(16.dp),
    ) {
        AnimatedContent(cargando, transitionSpec = { fadeIn() togetherWith fadeOut() }, label = "botonCargando") { trabajando ->
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                if (trabajando) CircularProgressIndicator(Modifier.size(18.dp), strokeWidth = 2.dp, color = MaterialTheme.colorScheme.onPrimary)
                Text(if (trabajando) textoCargando else texto, fontWeight = FontWeight.Bold)
            }
        }
    }
}

// ---------------------------------------------------------------- Estados vacíos y diálogos

/** Estado vacío ilustrado: manchas de color, el dibujo flotando y el texto; opcionalmente un botón. */
@Composable
fun EmptyState(
    emoji: String,
    title: String,
    text: String,
    modifier: Modifier = Modifier,
    accion: (@Composable () -> Unit)? = null,
) {
    Column(
        modifier.fillMaxSize().padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Ilustracion(emoji, Modifier.aparecer())
        Spacer(Modifier.height(20.dp))
        Text(
            title,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier.aparecer(1),
        )
        Spacer(Modifier.height(6.dp))
        Text(
            text,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.aparecer(2),
        )
        if (accion != null) {
            Spacer(Modifier.height(20.dp))
            Box(Modifier.aparecer(3)) { accion() }
        }
    }
}

/** Dibujo de los estados vacíos: dos círculos de color, huellitas alrededor y el emoji flotando. */
@Composable
fun Ilustracion(emoji: String, modifier: Modifier = Modifier, size: Int = 160) {
    val scheme = MaterialTheme.colorScheme
    Box(modifier.size(size.dp), contentAlignment = Alignment.Center) {
        Box(
            Modifier.size((size * 0.86f).dp)
                .background(Brush.radialGradient(listOf(scheme.primaryContainer, scheme.primaryContainer.copy(alpha = 0.35f))), CircleShape),
        )
        Box(
            Modifier.size((size * 0.3f).dp)
                .align(Alignment.TopEnd)
                .offset(x = (-6).dp, y = 10.dp)
                .background(scheme.secondaryContainer, CircleShape),
        )
        Text("🐾", fontSize = (size / 9).sp, modifier = Modifier.align(Alignment.BottomStart).offset(x = 8.dp, y = (-10).dp).rotate(-20f).alpha(0.5f))
        Text("🐾", fontSize = (size / 12).sp, modifier = Modifier.align(Alignment.TopStart).offset(x = 18.dp, y = 14.dp).rotate(15f).alpha(0.35f))
        Text(emoji, fontSize = (size / 2.6f).sp, modifier = Modifier.flotar())
    }
}

@Composable
fun ConfirmDialog(title: String, text: String, confirmLabel: String, onConfirm: () -> Unit, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = { Icon(Icons.Filled.Warning, contentDescription = null, tint = MaterialTheme.colorScheme.error) },
        title = { Text(title, textAlign = TextAlign.Center) },
        text = { Text(text, textAlign = TextAlign.Center) },
        confirmButton = { TextButton(onClick = onConfirm) { Text(confirmLabel, color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Bold) } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } },
    )
}

// ---------------------------------------------------------------- Estados

/** Color fuerte de cada estado de vacuna (la rayita de las tarjetas, el punto de la etiqueta). */
@Composable
fun colorDeEstado(status: VaccineStatus): Color {
    val scheme = MaterialTheme.colorScheme
    return when (status) {
        VaccineStatus.OVERDUE -> scheme.error
        VaccineStatus.SOON -> scheme.secondary
        VaccineStatus.OK -> scheme.primary
        VaccineStatus.NONE -> scheme.outlineVariant
    }
}

/** Etiqueta redonda de estado con punto; el punto late si [latir] (por ejemplo, vencida). */
@Composable
fun EtiquetaEstado(texto: String, fondo: Color, letra: Color, latir: Boolean = false) {
    // Al cambiar de estado (p. ej. de «Pronto» a «Al día») el color se funde en vez de saltar.
    val fondoAnimado by animateColorAsState(fondo, tween(450), label = "fondoEstado")
    val letraAnimada by animateColorAsState(letra, tween(450), label = "letraEstado")
    val transicion = rememberInfiniteTransition(label = "latido")
    val pulso by transicion.animateFloat(
        initialValue = 1f,
        targetValue = 1.6f,
        animationSpec = infiniteRepeatable(tween(700), RepeatMode.Reverse),
        label = "pulso",
    )
    Surface(color = fondoAnimado, contentColor = letraAnimada, shape = RoundedCornerShape(50)) {
        Row(
            Modifier.padding(start = 9.dp, end = 11.dp, top = 5.dp, bottom = 5.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Box(Modifier.size(7.dp).scale(if (latir) pulso else 1f).background(letraAnimada, CircleShape))
            AnimatedContent(
                targetState = texto,
                transitionSpec = { (fadeIn(tween(250)) + scaleIn(initialScale = 0.85f)) togetherWith fadeOut(tween(150)) },
                label = "textoEstado",
            ) { t -> Text(t, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.SemiBold, maxLines = 1) }
        }
    }
}

@Composable
fun StatusChip(status: VaccineStatus) {
    val scheme = MaterialTheme.colorScheme
    val (bg, fg) = when (status) {
        VaccineStatus.OVERDUE -> scheme.errorContainer to scheme.onErrorContainer
        VaccineStatus.SOON -> scheme.secondaryContainer to scheme.onSecondaryContainer
        VaccineStatus.OK -> scheme.primaryContainer to scheme.onPrimaryContainer
        VaccineStatus.NONE -> scheme.surfaceVariant to scheme.onSurfaceVariant
    }
    EtiquetaEstado(status.label, bg, fg, latir = status == VaccineStatus.OVERDUE)
}

/** «Vence hoy», «Faltan 3 días», «Venció hace 2 días»: cuánto falta para una fecha. */
fun textoDias(day: Long, today: Long): String {
    val dias = day - today
    return when {
        dias == 0L -> "Vence hoy"
        dias == 1L -> "Vence mañana"
        dias > 1L -> "Faltan $dias días"
        dias == -1L -> "Venció ayer"
        else -> "Venció hace ${abs(dias)} días"
    }
}

/** Círculo con el emoji de la especie, con un degradado suave verde-durazno. */
@Composable
fun SpeciesBadge(emoji: String, size: Int = 56) {
    val scheme = MaterialTheme.colorScheme
    Box(
        Modifier.size(size.dp).background(
            Brush.linearGradient(listOf(scheme.primaryContainer, scheme.secondaryContainer.copy(alpha = 0.8f))),
            CircleShape,
        ),
        contentAlignment = Alignment.Center,
    ) { Text(emoji, fontSize = (size / 2).sp) }
}

/** Campo de fecha: se toca el calendario del costado y sale el selector. */
@Composable
fun DateField(label: String, day: Long?, onChange: (Long?) -> Unit, clearable: Boolean = false) {
    var open by remember { mutableStateOf(false) }
    OutlinedTextField(
        value = day?.let { Dates.format(it) } ?: "",
        onValueChange = {},
        readOnly = true,
        label = { Text(label) },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        leadingIcon = { Icon(Icons.Filled.DateRange, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
        trailingIcon = {
            Row {
                if (clearable && day != null) {
                    IconButton(onClick = { onChange(null) }) { Icon(Icons.Filled.Clear, contentDescription = "Quitar la fecha") }
                }
                TextButton(onClick = { open = true }) { Text("Elegir") }
            }
        },
    )
    if (open) {
        val state = rememberDatePickerState(initialSelectedDateMillis = day?.let { it * MILLIS_PER_DAY })
        DatePickerDialog(
            onDismissRequest = { open = false },
            confirmButton = {
                TextButton(onClick = {
                    state.selectedDateMillis?.let { onChange(it / MILLIS_PER_DAY) }
                    open = false
                }) { Text("Listo") }
            },
            dismissButton = { TextButton(onClick = { open = false }) { Text("Cancelar") } },
        ) { DatePicker(state = state) }
    }
}
