package com.huellitas.mascotas.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.huellitas.mascotas.MainViewModel
import kotlinx.coroutines.launch

@Composable
private fun AuthLayout(title: String, subtitle: String, content: @Composable ColumnScope.() -> Unit) {
    val sobre = colorSobreEncabezado()
    Box(Modifier.fillMaxSize()) {
        // Medialuna verde arriba; el formulario va en una tarjeta blanca que se monta encima.
        Box(
            Modifier.fillMaxWidth().height(300.dp)
                .background(encabezadoBrush(), RoundedCornerShape(bottomStart = 40.dp, bottomEnd = 40.dp)),
        ) { HuellasDeAdorno(Modifier.matchParentSize()) }
        Column(
            Modifier.fillMaxSize().systemBarsPadding().imePadding().verticalScroll(rememberScrollState()).padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(Modifier.height(8.dp))
            Box(
                Modifier.size(96.dp).flotar(4f).background(sobre.copy(alpha = 0.2f), CircleShape),
                contentAlignment = Alignment.Center,
            ) { Text("🐾", fontSize = 46.sp) }
            Text(
                title,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                color = sobre,
                modifier = Modifier.aparecer(1),
            )
            Text(
                subtitle,
                style = MaterialTheme.typography.bodyLarge,
                color = sobre.copy(alpha = 0.85f),
                textAlign = TextAlign.Center,
                modifier = Modifier.aparecer(2),
            )
            Spacer(Modifier.height(4.dp))
            Tarjeta(Modifier.fillMaxWidth().aparecer(3)) {
                Column(
                    Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    content = content,
                )
            }
        }
    }
}

/** Campo de texto con ícono a la izquierda, del mismo estilo en login y registro. */
@Composable
private fun CampoConIcono(value: String, onChange: (String) -> Unit, label: String, icon: ImageVector, keyboardOptions: KeyboardOptions) {
    OutlinedTextField(
        value = value,
        onValueChange = onChange,
        label = { Text(label) },
        singleLine = true,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        leadingIcon = { Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
        keyboardOptions = keyboardOptions,
    )
}

@Composable
private fun PasswordField(value: String, onChange: (String) -> Unit, label: String, imeAction: ImeAction) {
    var visible by remember { mutableStateOf(false) }
    OutlinedTextField(
        value = value,
        onValueChange = onChange,
        label = { Text(label) },
        singleLine = true,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        leadingIcon = { Icon(Icons.Filled.Lock, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
        visualTransformation = if (visible) VisualTransformation.None else PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = imeAction),
        // canFocus = false: con «Siguiente» del teclado el foco pasa al campo de abajo, no a este botón.
        trailingIcon = {
            TextButton(onClick = { visible = !visible }, modifier = Modifier.focusProperties { canFocus = false }) {
                Text(if (visible) "Ocultar" else "Ver")
            }
        },
    )
}

@Composable
fun LoginScreen(vm: MainViewModel, onRegister: () -> Unit) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }
    var busy by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    AuthLayout("Bienvenido de nuevo", "Entra para ver a tus mascotas y sus vacunas") {
        CampoConIcono(
            email, { email = it; error = null }, "Correo", Icons.Filled.Email,
            KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next),
        )
        PasswordField(password, { password = it; error = null }, "Contraseña", ImeAction.Done)
        MensajeError(error)
        BotonPrincipal(
            "Entrar",
            onClick = {
                busy = true
                error = null
                scope.launch {
                    error = vm.login(email, password)
                    busy = false
                }
            },
            enabled = !busy && email.isNotBlank() && password.isNotEmpty(),
            cargando = busy,
            textoCargando = "Entrando…",
        )
        TextButton(onClick = onRegister) { Text("¿No tienes cuenta? Crear una") }
    }
}

@Composable
fun RegisterScreen(vm: MainViewModel, onBack: () -> Unit) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var repeat by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }
    var busy by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    AuthLayout("Crea tu cuenta", "Se guarda solo en este celular") {
        CampoConIcono(name, { name = it; error = null }, "Tu nombre", Icons.Filled.Person, KeyboardOptions(imeAction = ImeAction.Next))
        CampoConIcono(
            email, { email = it; error = null }, "Correo", Icons.Filled.Email,
            KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next),
        )
        PasswordField(password, { password = it; error = null }, "Contraseña (mínimo 8)", ImeAction.Next)
        PasswordField(repeat, { repeat = it; error = null }, "Repite la contraseña", ImeAction.Done)
        MensajeError(error)
        BotonPrincipal(
            "Crear cuenta",
            onClick = {
                if (password != repeat) {
                    error = "Las contraseñas no coinciden."
                    return@BotonPrincipal
                }
                busy = true
                error = null
                scope.launch {
                    error = vm.register(name, email, password)
                    busy = false
                }
            },
            enabled = !busy && name.isNotBlank() && email.isNotBlank() && password.isNotEmpty(),
            cargando = busy,
            textoCargando = "Creando…",
        )
        TextButton(onClick = onBack) { Text("Ya tengo cuenta") }
    }
}
