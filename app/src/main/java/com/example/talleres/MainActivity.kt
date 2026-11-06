package com.example.talleres

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(Modifier.fillMaxSize()) {
                    Box(Modifier.fillMaxSize().systemBarsPadding()) {
                        InscripcionScreen()
                    }
                }
            }
        }
    }
}

// taller -> lugares que quedan
private val cupos = mapOf("Pintura" to 8, "Fotografía" to 6, "Guitarra" to 4)
private val talleres = cupos.keys.toList()

fun validarNombre(nombre: String): String? =
    if (nombre.trim().length < 3) "Escribe tu nombre completo (mínimo 3 caracteres)" else null

fun validarCorreo(correo: String): String? {
    val arroba = correo.indexOf('@')
    val punto = correo.indexOf('.', arroba + 1)
    val bien = arroba > 0 && punto > arroba + 1 && punto < correo.length - 1
    return if (bien) null else "Ingresa un correo válido"
}

fun validarEdad(edad: String): String? {
    val n = edad.toIntOrNull()
    return if (n == null || n < 12 || n > 99) "La edad debe estar entre 12 y 99" else null
}

fun validarAviso(acepta: Boolean): String? =
    if (acepta) null else "Debes aceptar el aviso de privacidad"

@Composable
fun InscripcionScreen() {
    var nombre by rememberSaveable { mutableStateOf("") }
    var correo by rememberSaveable { mutableStateOf("") }
    var edad by rememberSaveable { mutableStateOf("") }
    var taller by rememberSaveable { mutableStateOf(talleres[0]) }
    var aceptaAviso by rememberSaveable { mutableStateOf(false) }
    var intentoEnviar by rememberSaveable { mutableStateOf(false) }
    var inscrito by rememberSaveable { mutableStateOf(false) }

    val errNombre = validarNombre(nombre)
    val errCorreo = validarCorreo(correo)
    val errEdad = validarEdad(edad)
    val errAviso = validarAviso(aceptaAviso)

    if (inscrito) {
        ConfirmacionScreen(
            nombre = nombre,
            correo = correo,
            taller = taller,
            onNueva = {
                nombre = ""
                correo = ""
                edad = ""
                taller = talleres[0]
                aceptaAviso = false
                intentoEnviar = false
                inscrito = false
            }
        )
    } else {
        InscripcionForm(
            nombre = nombre, onNombreChange = { nombre = it },
            correo = correo, onCorreoChange = { correo = it },
            edad = edad, onEdadChange = { nueva -> edad = nueva.filter { it.isDigit() } },
            taller = taller, onTallerChange = { taller = it },
            aceptaAviso = aceptaAviso, onAceptaAvisoChange = { aceptaAviso = it },
            errNombre = if (intentoEnviar) errNombre else null,
            errCorreo = if (intentoEnviar) errCorreo else null,
            errEdad = if (intentoEnviar) errEdad else null,
            errAviso = if (intentoEnviar) errAviso else null,
            onEnviar = {
                intentoEnviar = true
                if (errNombre == null && errCorreo == null && errEdad == null && errAviso == null) {
                    inscrito = true
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InscripcionForm(
    nombre: String, onNombreChange: (String) -> Unit,
    correo: String, onCorreoChange: (String) -> Unit,
    edad: String, onEdadChange: (String) -> Unit,
    taller: String, onTallerChange: (String) -> Unit,
    aceptaAviso: Boolean, onAceptaAvisoChange: (Boolean) -> Unit,
    errNombre: String?, errCorreo: String?, errEdad: String?, errAviso: String?,
    onEnviar: () -> Unit
) {
    var menuAbierto by remember { mutableStateOf(false) }

    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text("Inscripción a talleres", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(20.dp))
        Text(
            "Casa del Lago · cupo limitado",
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(4.dp))

        CampoTexto("Nombre completo", nombre, onNombreChange, errNombre)
        CampoTexto(
            "Correo", correo, onCorreoChange, errCorreo,
            KeyboardOptions(keyboardType = KeyboardType.Email)
        )
        CampoTexto(
            "Edad", edad, onEdadChange, errEdad,
            KeyboardOptions(keyboardType = KeyboardType.Number)
        )

        ExposedDropdownMenuBox(
            expanded = menuAbierto,
            onExpandedChange = { menuAbierto = it },
            modifier = Modifier.padding(top = 4.dp)
        ) {
            OutlinedTextField(
                value = taller,
                onValueChange = {},
                readOnly = true,
                label = { Text("Taller") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = menuAbierto) },
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth()
            )
            ExposedDropdownMenu(
                expanded = menuAbierto,
                onDismissRequest = { menuAbierto = false }
            ) {
                talleres.forEach { t ->
                    DropdownMenuItem(
                        text = { Text(t) },
                        onClick = {
                            onTallerChange(t)
                            menuAbierto = false
                        }
                    )
                }
            }
        }

        Row(
            Modifier.padding(top = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(checked = aceptaAviso, onCheckedChange = onAceptaAvisoChange)
            Text("Acepto el aviso de privacidad", fontSize = 17.sp)
        }
        if (errAviso != null) {
            Text(
                errAviso,
                color = MaterialTheme.colorScheme.error,
                fontSize = 13.sp,
                modifier = Modifier.padding(start = 16.dp)
            )
        }

        Spacer(Modifier.height(16.dp))
        Button(
            onClick = onEnviar,
            modifier = Modifier.fillMaxWidth().height(52.dp)
        ) { Text("Inscribirme", fontWeight = FontWeight.Bold, fontSize = 16.sp) }

        Spacer(Modifier.height(16.dp))
        Text(
            "Quedan ${cupos[taller]} lugares en este taller",
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun CampoTexto(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    error: String?,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        isError = error != null,
        supportingText = { if (error != null) Text(error) },
        keyboardOptions = keyboardOptions,
        singleLine = true,
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 4.dp)
    )
}

@Composable
fun ConfirmacionScreen(nombre: String, correo: String, taller: String, onNueva: () -> Unit) {
    Column(
        Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("¡Inscripción lista!", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(20.dp))

        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF3EDF7)),
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                FilaResumen("Nombre", nombre)
                FilaResumen("Correo", correo)
                FilaResumen("Taller", taller)
            }
        }

        Spacer(Modifier.height(24.dp))
        OutlinedButton(
            onClick = onNueva,
            modifier = Modifier.fillMaxWidth().height(52.dp)
        ) { Text("Nueva inscripción", fontWeight = FontWeight.Bold) }
    }
}

@Composable
private fun FilaResumen(titulo: String, valor: String) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(titulo, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(valor, fontWeight = FontWeight.Bold)
    }
}

@Preview(showBackground = true)
@Composable
fun ConfirmacionPreview() {
    MaterialTheme { ConfirmacionScreen("Ana López", "ana.lopez@correo.com", "Fotografía", {}) }
}