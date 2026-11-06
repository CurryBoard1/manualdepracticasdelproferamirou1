package com.example.quiz

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(Modifier.fillMaxSize()) {
                    Box(Modifier.fillMaxSize().systemBarsPadding()) {
                        QuizApp()
                    }
                }
            }
        }
    }
}

data class Pregunta(val texto: String, val opciones: List<String>, val correcta: Int)

enum class Pantalla { INICIO, QUIZ, RESULTADO }

private val preguntas = listOf(
    Pregunta(
        "¿Qué palabra clave declara una variable que no puede cambiar su valor en Kotlin?",
        listOf("var", "val", "let", "static"), 1
    ),
    Pregunta(
        "¿Cuál es el resultado de 7 % 3?",
        listOf("2", "1", "0", "3"), 1
    ),
    Pregunta(
        "¿Qué tipo de dato conviene usar para guardar el valor 3.14?",
        listOf("Int", "Double", "String", "Boolean"), 1
    ),
    Pregunta(
        "¿Qué estructura repite un bloque mientras se cumpla una condición?",
        listOf("if", "when", "while", "class"), 2
    ),
    Pregunta(
        "¿Qué índice tiene el primer elemento de una lista?",
        listOf("0", "1", "-1", "Depende de la lista"), 0
    )
)

private val rojoBorde = Color(0xFFB3261E)
private val rojoFondo = Color(0xFFF9DEDC)
private val verdeBorde = Color(0xFF2E7D32)
private val verdeFondo = Color(0xFFDDEFD9)

class QuizViewModel : ViewModel() {
    var pantalla by mutableStateOf(Pantalla.INICIO)
        private set
    var nombre by mutableStateOf("")
        private set
    var indicePregunta by mutableIntStateOf(0)
        private set
    var seleccionada by mutableStateOf<Int?>(null)
        private set
    var respondida by mutableStateOf(false)
        private set
    var puntaje by mutableIntStateOf(0)
        private set

    val preguntaActual: Pregunta
        get() = preguntas[indicePregunta]

    fun onNombreChange(nuevoNombre: String) {
        nombre = nuevoNombre
    }

    fun seleccionarOpcion(index: Int) {
        if (!respondida) {
            seleccionada = index
        }
    }

    fun confirmar() {
        if (seleccionada != null && !respondida) {
            respondida = true
            if (seleccionada == preguntaActual.correcta) {
                puntaje++
            }
        }
    }

    fun siguiente() {
        if (indicePregunta < preguntas.size - 1) {
            indicePregunta++
            seleccionada = null
            respondida = false
        } else {
            pantalla = Pantalla.RESULTADO
        }
    }

    fun reintentar() {
        indicePregunta = 0
        seleccionada = null
        respondida = false
        puntaje = 0
        pantalla = Pantalla.QUIZ
    }

    fun cambiarJugador() {
        indicePregunta = 0
        seleccionada = null
        respondida = false
        puntaje = 0
        nombre = ""
        pantalla = Pantalla.INICIO
    }

    fun iniciarQuiz() {
        indicePregunta = 0
        seleccionada = null
        respondida = false
        puntaje = 0
        pantalla = Pantalla.QUIZ
    }
}

@Composable
fun QuizApp(viewModel: QuizViewModel = viewModel()) {
    when (viewModel.pantalla) {
        Pantalla.INICIO -> InicioScreen(
            nombre = viewModel.nombre,
            onNombreChange = viewModel::onNombreChange,
            onComenzar = viewModel::iniciarQuiz
        )

        Pantalla.QUIZ -> PreguntaScreen(
            pregunta = viewModel.preguntaActual,
            numero = viewModel.indicePregunta + 1,
            total = preguntas.size,
            puntaje = viewModel.puntaje,
            seleccionada = viewModel.seleccionada,
            respondida = viewModel.respondida,
            onSeleccionar = viewModel::seleccionarOpcion,
            onConfirmar = viewModel::confirmar,
            onSiguiente = viewModel::siguiente
        )

        Pantalla.RESULTADO -> ResultadoScreen(
            nombre = viewModel.nombre,
            puntaje = viewModel.puntaje,
            total = preguntas.size,
            onReintentar = viewModel::reintentar,
            onCambiarJugador = viewModel::cambiarJugador
        )
    }
}

@Composable
fun InicioScreen(
    nombre: String,
    onNombreChange: (String) -> Unit,
    onComenzar: () -> Unit
) {
    Column(
        Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text("Quiz de programación", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(8.dp))
        Text(
            "5 preguntas de fundamentos. ¿Cómo te llamas?",
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(24.dp))
        OutlinedTextField(
            value = nombre,
            onValueChange = onNombreChange,
            label = { Text("Tu nombre") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(24.dp))
        Button(
            onClick = onComenzar,
            enabled = nombre.trim().length >= 2,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
        ) {
            Text("Comenzar", fontWeight = FontWeight.Bold, fontSize = 16.sp)
        }
    }
}

@Composable
fun PreguntaScreen(
    pregunta: Pregunta,
    numero: Int,
    total: Int,
    puntaje: Int,
    seleccionada: Int?,
    respondida: Boolean,
    onSeleccionar: (Int) -> Unit,
    onConfirmar: () -> Unit,
    onSiguiente: () -> Unit
) {
    val acerto = seleccionada == pregunta.correcta

    Column(
        Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Quiz de programación", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(20.dp))

        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                "Pregunta $numero de $total",
                fontSize = 18.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                "Puntaje: $puntaje",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }
        Spacer(Modifier.height(8.dp))
        LinearProgressIndicator(
            progress = { numero / total.toFloat() },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
        )

        Spacer(Modifier.height(24.dp))
        Text(
            pregunta.texto,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            lineHeight = 30.sp
        )
        Spacer(Modifier.height(24.dp))

        pregunta.opciones.forEachIndexed { i, opcion ->
            val esCorrecta = respondida && i == pregunta.correcta
            val esMala = respondida && i == seleccionada && i != pregunta.correcta

            val fondo = when {
                esCorrecta -> verdeFondo
                esMala -> rojoFondo
                else -> Color.Transparent
            }
            val borde = when {
                esCorrecta -> BorderStroke(2.dp, verdeBorde)
                esMala -> BorderStroke(2.dp, rojoBorde)
                else -> BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
            }

            Surface(
                color = fondo,
                shape = RoundedCornerShape(16.dp),
                border = borde,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
                    .clickable(enabled = !respondida) { onSeleccionar(i) }
            ) {
                Row(
                    Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = seleccionada == i,
                        onClick = null,
                        colors = RadioButtonDefaults.colors(
                            selectedColor = if (esMala) rojoBorde else MaterialTheme.colorScheme.primary
                        )
                    )
                    Text(
                        opcion,
                        fontSize = 18.sp,
                        fontWeight = if (esCorrecta || esMala || (!respondida && seleccionada == i))
                            FontWeight.Bold else FontWeight.Normal,
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = 16.dp)
                    )
                    if (esCorrecta) Text("✓", fontSize = 20.sp, color = verdeBorde, fontWeight = FontWeight.Bold)
                    if (esMala) Text("✕", fontSize = 20.sp, color = rojoBorde, fontWeight = FontWeight.Bold)
                }
            }
        }

        if (respondida) {
            Spacer(Modifier.height(8.dp))
            Surface(
                color = if (acerto) verdeFondo else rojoFondo,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    if (acerto) "¡Correcto!"
                    else "Incorrecto. La respuesta correcta es '${pregunta.opciones[pregunta.correcta]}'.",
                    color = if (acerto) Color(0xFF1B5E20) else Color(0xFF8C1D18),
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }

        Spacer(Modifier.weight(1f))

        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            FilledTonalButton(
                onClick = onConfirmar,
                enabled = seleccionada != null && !respondida,
                modifier = Modifier
                    .weight(2f)
                    .height(52.dp)
            ) {
                Text("Confirmar", fontWeight = FontWeight.Bold)
            }
            Button(
                onClick = onSiguiente,
                enabled = respondida,
                modifier = Modifier
                    .weight(3f)
                    .height(52.dp)
            ) {
                Text(
                    if (numero == total) "Ver resultados" else "Siguiente",
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun ResultadoScreen(
    nombre: String,
    puntaje: Int,
    total: Int,
    onReintentar: () -> Unit,
    onCambiarJugador: () -> Unit
) {
    val mensaje = when {
        puntaje <= 2 -> "Sigue practicando"
        puntaje <= 4 -> "Buen trabajo"
        else -> "Perfecto"
    }

    Column(
        Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            "¡Muy bien, $nombre!",
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(16.dp))
        Text(
            "$puntaje de $total",
            fontSize = 56.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(Modifier.height(8.dp))
        Text(
            mensaje,
            fontSize = 20.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = FontWeight.Medium
        )

        Spacer(Modifier.height(40.dp))
        Button(
            onClick = onReintentar,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
        ) {
            Text("Reintentar", fontWeight = FontWeight.Bold)
        }
        Spacer(Modifier.height(12.dp))
        OutlinedButton(
            onClick = onCambiarJugador,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
        ) {
            Text("Cambiar jugador", fontWeight = FontWeight.Bold)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ResultadoPreview() {
    MaterialTheme {
        ResultadoScreen("Ana", 4, 5, {}, {})
    }
}
