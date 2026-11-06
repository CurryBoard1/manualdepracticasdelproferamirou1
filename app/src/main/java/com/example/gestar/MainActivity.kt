package com.example.gestar

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
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
                        TareasScreen()
                    }
                }
            }
        }
    }
}

enum class Prioridad(val etiqueta: String, val fondo: Color, val texto: Color) {
    ALTA("Alta", Color(0xFFF9DEDC), Color(0xFF8C1D18)),
    MEDIA("Media", Color(0xFFFFEFB8), Color(0xFF6B4E00)),
    BAJA("Baja", Color(0xFFD8EDD5), Color(0xFF2E6B2E))
}

enum class Filtro(val etiqueta: String) {
    TODAS("Todas"),
    PENDIENTES("Pendientes"),
    HECHAS("Hechas")
}

data class Tarea(
    val id: Int,
    val titulo: String,
    val prioridad: Prioridad,
    val hecha: Boolean = false
)

@Composable
fun TareasScreen() {
    val tareas = remember { mutableStateListOf<Tarea>() }
    var siguienteId by remember { mutableIntStateOf(1) }
    var textoNueva by remember { mutableStateOf("") }
    var prioridadNueva by remember { mutableStateOf(Prioridad.ALTA) }
    var filtro by remember { mutableStateOf(Filtro.TODAS) }
    var mostrarDialogo by remember { mutableStateOf(false) }

    val pendientes = tareas.count { !it.hecha }
    val hechas = tareas.size - pendientes
    val tareasVisibles = when (filtro) {
        Filtro.TODAS -> tareas.toList()
        Filtro.PENDIENTES -> tareas.filter { !it.hecha }
        Filtro.HECHAS -> tareas.filter { it.hecha }
    }.sortedBy { it.prioridad }

    fun agregar() {
        if (textoNueva.isBlank()) return
        tareas.add(Tarea(siguienteId, textoNueva.trim(), prioridadNueva))
        siguienteId++
        textoNueva = ""
    }

    Column(
        Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Text("Mis pendientes", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(20.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            OutlinedTextField(
                value = textoNueva,
                onValueChange = { textoNueva = it },
                placeholder = { Text("Nueva tarea") },
                singleLine = true,
                modifier = Modifier.weight(1f)
            )
            Spacer(Modifier.width(8.dp))
            FilledIconButton(
                onClick = { agregar() },
                enabled = textoNueva.isNotBlank(),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.size(56.dp)
            ) { Icon(Icons.Default.Add, contentDescription = "Agregar", Modifier.size(30.dp)) }
        }

        Row(
            Modifier.padding(top = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text("Prioridad", color = MaterialTheme.colorScheme.onSurfaceVariant)
            Prioridad.entries.forEach { p ->
                ChipSeleccion(p.etiqueta, prioridadNueva == p) { prioridadNueva = p }
            }
        }

        Row(
            Modifier.padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Filtro.entries.forEach { f ->
                ChipSeleccion(f.etiqueta, filtro == f) { filtro = f }
            }
        }

        HorizontalDivider(Modifier.padding(top = 8.dp))

        LazyColumn(Modifier.weight(1f)) {
            items(tareasVisibles, key = { it.id }) { tarea ->
                TareaItem(
                    tarea = tarea,
                    onCheckedChange = { nuevo ->
                        val index = tareas.indexOfFirst { it.id == tarea.id }
                        if (index != -1) tareas[index] = tareas[index].copy(hecha = nuevo)
                    },
                    onDelete = { tareas.removeAll { it.id == tarea.id } }
                )
                HorizontalDivider()
            }
            item {
                Text(
                    "$pendientes pendientes de ${tareas.size}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    modifier = Modifier.padding(vertical = 16.dp)
                )
            }
        }

        OutlinedButton(
            onClick = { mostrarDialogo = true },
            enabled = hechas > 0,
            modifier = Modifier.fillMaxWidth().height(52.dp)
        ) { Text("Borrar completadas", fontWeight = FontWeight.Bold) }
    }

    if (mostrarDialogo) {
        ConfirmarBorradoDialog(
            cantidad = hechas,
            onConfirm = {
                tareas.removeAll { it.hecha }
                mostrarDialogo = false
            },
            onDismiss = { mostrarDialogo = false }
        )
    }
}

@Composable
private fun ChipSeleccion(texto: String, activo: Boolean, onClick: () -> Unit) {
    FilterChip(
        selected = activo,
        onClick = onClick,
        label = { Text(texto, fontSize = 16.sp) },
        leadingIcon = if (activo) {
            { Icon(Icons.Default.Check, null, Modifier.size(FilterChipDefaults.IconSize)) }
        } else null
    )
}

@Composable
fun TareaItem(tarea: Tarea, onCheckedChange: (Boolean) -> Unit, onDelete: () -> Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(checked = tarea.hecha, onCheckedChange = onCheckedChange)
        Column(Modifier.weight(1f)) {
            Text(
                tarea.titulo,
                fontSize = 19.sp,
                textDecoration = if (tarea.hecha) TextDecoration.LineThrough else TextDecoration.None,
                color = if (tarea.hecha) MaterialTheme.colorScheme.onSurfaceVariant
                else MaterialTheme.colorScheme.onSurface
            )
            Spacer(Modifier.height(4.dp))
            Surface(shape = RoundedCornerShape(50), color = tarea.prioridad.fondo) {
                Text(
                    tarea.prioridad.etiqueta,
                    color = tarea.prioridad.texto,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 3.dp)
                )
            }
        }
        IconButton(onClick = onDelete) {
            Icon(
                Icons.Default.Close,
                contentDescription = "Eliminar",
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun ConfirmarBorradoDialog(cantidad: Int, onConfirm: () -> Unit, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Borrar completadas") },
        text = { Text("Se van a eliminar $cantidad tareas hechas. ¿Continuar?") },
        confirmButton = { TextButton(onClick = onConfirm) { Text("Borrar") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } }
    )
}

@Preview(showBackground = true)
@Composable
fun TareaItemPreview() {
    MaterialTheme {
        TareaItem(Tarea(1, "Leer capítulo 4", Prioridad.BAJA, true), {}, {})
    }
}