package com.example.reseas

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.util.Locale

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(Modifier.fillMaxSize()) {
                    Box(Modifier.fillMaxSize().systemBarsPadding()) {
                        ResenaScreen(onBack = { finish() })
                    }
                }
            }
        }
    }
}

private val ETIQUETAS = listOf("Familiar", "Económico", "Rápido", "Romántico", "Vegetariano")
private val DESCRIPCION = listOf("Sin calificar", "Malo", "Regular", "Bueno", "Muy bueno", "Excelente")
private const val MAX_COMENTARIO = 200
private val dorado = Color(0xFFF9A825)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ResenaScreen(onBack: () -> Unit = {}) {
    var comida by remember { mutableStateOf(0) }
    var servicio by remember { mutableStateOf(0) }
    var ambiente by remember { mutableStateOf(0) }
    var comentario by remember { mutableStateOf("") }
    var etiquetas by remember { mutableStateOf(setOf<String>()) }
    var publicada by remember { mutableStateOf(false) }

    // derivados
    val completo = comida > 0 && servicio > 0 && ambiente > 0
    val general = (comida + servicio + ambiente) / 3.0
    val puedePublicar = completo && comentario.trim().length >= 20

    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Regresar")
            }
            Text("Escribe tu reseña", style = MaterialTheme.typography.headlineMedium)
        }
        Spacer(Modifier.height(12.dp))

        Text("Restaurante El Mezquite", fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Text("Califica tu experiencia", color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.height(12.dp))

        FilaCalificacion("Comida", comida) { comida = it }
        FilaCalificacion("Servicio", servicio) { servicio = it }
        FilaCalificacion("Ambiente", ambiente) { ambiente = it }

        if (completo) {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF3EDF7)),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp)
            ) {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 18.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Calificación general",
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            String.format(Locale.US, "%.1f", general),
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(Modifier.width(6.dp))
                        Icon(Icons.Default.Star, null, tint = dorado)
                    }
                }
            }
        }

        Spacer(Modifier.height(8.dp))
        ComentarioField(comentario, { comentario = it }, MAX_COMENTARIO)

        FlowRow(
            Modifier.padding(top = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ETIQUETAS.forEach { e ->
                val marcada = e in etiquetas
                FilterChip(
                    selected = marcada,
                    onClick = { etiquetas = if (marcada) etiquetas - e else etiquetas + e },
                    label = { Text(e, fontSize = 16.sp) },
                    leadingIcon = if (marcada) {
                        { Icon(Icons.Default.Check, null, Modifier.size(FilterChipDefaults.IconSize)) }
                    } else null
                )
            }
        }

        Spacer(Modifier.height(24.dp))
        Button(
            onClick = { publicada = true },
            enabled = puedePublicar,
            modifier = Modifier.fillMaxWidth().height(52.dp)
        ) { Text("Publicar", fontWeight = FontWeight.Bold, fontSize = 16.sp) }

        if (publicada) {
            Spacer(Modifier.height(16.dp))
            ResumenResena(general, comentario, etiquetas)
        }
        Spacer(Modifier.height(16.dp))
    }
}

@Composable
private fun FilaCalificacion(nombre: String, rating: Int, onChange: (Int) -> Unit) {
    Text(nombre, fontSize = 18.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
    Row(
        Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        RatingBar(rating, onChange)
        Text(
            if (rating > 0) DESCRIPCION[rating] else "",
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp
        )
    }
}

// si tocas la estrella que ya estaba marcada se quita la calificación (rating = 0).
// Lo dejé aquí dentro para que cualquier pantalla que use RatingBar ya lo traiga.
@Composable
fun RatingBar(rating: Int, onRatingChange: (Int) -> Unit, modifier: Modifier = Modifier) {
    Row(modifier) {
        for (i in 1..5) {
            Icon(
                Icons.Default.Star,
                contentDescription = "$i estrellas",
                tint = if (i <= rating) dorado else Color(0xFFCAC4D0),
                modifier = Modifier
                    .size(44.dp)
                    .clickable { onRatingChange(if (i == rating) 0 else i) }
                    .padding(4.dp)
            )
        }
    }
}

@Composable
fun ComentarioField(texto: String, onChange: (String) -> Unit, maximo: Int = 200) {
    Column {
        OutlinedTextField(
            value = texto,
            onValueChange = { if (it.length <= maximo) onChange(it) },
            label = { Text("Comentario") },
            minLines = 4,
            modifier = Modifier.fillMaxWidth()
        )
        Text(
            "${texto.length}/$maximo",
            color = if (texto.length >= 180) Color.Red else MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier
                .align(Alignment.End)
                .padding(top = 4.dp)
        )
    }
}

@Composable
fun ResumenResena(general: Double, comentario: String, etiquetas: Set<String>) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFFE6F4E7)),
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text("¡Reseña publicada!", fontWeight = FontWeight.Bold, fontSize = 20.sp)
            Text("Calificación general: ${String.format(Locale.US, "%.1f", general)} ★")
            Text(comentario)
            if (etiquetas.isNotEmpty()) {
                Text(
                    etiquetas.joinToString(" · "),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RatingBarPreview() {
    MaterialTheme { RatingBar(rating = 4, onRatingChange = {}) }
}

@Preview(showBackground = true, heightDp = 800)
@Composable
fun ResenaScreenPreview() {
    MaterialTheme {
        ResenaScreen()
    }
}