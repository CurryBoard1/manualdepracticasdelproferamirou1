package com.example.aforo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.aforo.ui.theme.AforoTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AforoTheme {
                Surface(Modifier.fillMaxSize()) {
                    Box(Modifier.fillMaxSize().systemBarsPadding()) {
                        AforoScreen()
                    }
                }
            }
        }
    }
}

const val MAX_CAPACITY = 50

private val GreenColor = Color(0xFF2E7D32)
private val YellowColor = Color(0xFFF9A825)
private val RedColor = Color(0xFFB3261E)

@Composable
fun AforoScreen() {
    var currentCount by remember { mutableStateOf(0) }
    var dailyVisits by remember { mutableStateOf(0) }

    Column(
        Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Text("Biblioteca Central", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(20.dp))
        Text(
            "Sala de estudio · capacidad $MAX_CAPACITY",
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(16.dp))

        AforoIndicator(currentCount, MAX_CAPACITY)

        Spacer(Modifier.height(16.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            OutlinedButton(
                onClick = { if (currentCount > 0) currentCount-- },
                enabled = currentCount > 0,
                modifier = Modifier.weight(1f).height(52.dp)
            ) { Text("− Salir", fontWeight = FontWeight.Bold) }

            Button(
                onClick = {
                    if (currentCount < MAX_CAPACITY) {
                        currentCount++
                        dailyVisits++
                    }
                },
                enabled = currentCount < MAX_CAPACITY,
                modifier = Modifier.weight(1f).height(52.dp)
            ) { Text("+ Entrar", fontWeight = FontWeight.Bold) }
        }

        Spacer(Modifier.height(8.dp))

        TextButton(
            onClick = {
                val added = (currentCount + 5).coerceAtMost(MAX_CAPACITY) - currentCount
                currentCount += added
                dailyVisits += added
            },
            enabled = currentCount < MAX_CAPACITY,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) { Text("Añadir grupo (+5)") }

        Spacer(Modifier.height(8.dp))

        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Visitas del día", fontSize = 18.sp, fontWeight = FontWeight.Medium)
                    Text(
                        "Total de entradas registradas",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Text("$dailyVisits", fontSize = 40.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(Modifier.height(20.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
            Leyenda(GreenColor, "< 70%")
            Leyenda(YellowColor, "70–99%")
            Leyenda(RedColor, "100%")
        }

        Spacer(Modifier.weight(1f))

        OutlinedButton(
            onClick = {
                currentCount = 0
                dailyVisits = 0
            },
            border = BorderStroke(1.dp, RedColor),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = RedColor),
            modifier = Modifier.fillMaxWidth().height(52.dp)
        ) { Text("Reiniciar registro", fontWeight = FontWeight.Bold) }
    }
}

@Composable
fun AforoIndicator(current: Int, max: Int) {
    val percentage = current / max.toFloat()

    val (textColor, cardColor, statusText) = when {
        percentage >= 1f -> Triple(RedColor, Color(0xFFFADDD9), "Aforo completo")
        percentage >= 0.7f -> Triple(Color(0xFF8A5A00), Color(0xFFFFF0CC), "Cerca del límite")
        else -> Triple(Color(0xFF1B5E20), Color(0xFFE6F4E7), "Disponible")
    }

    val barColor = when {
        percentage >= 1f -> RedColor
        percentage >= 0.7f -> YellowColor
        else -> GreenColor
    }

    Card(
        colors = CardDefaults.cardColors(containerColor = cardColor),
        shape = RoundedCornerShape(28.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("$current / $max", fontSize = 64.sp, fontWeight = FontWeight.Bold, color = textColor)
            Text("personas en la sala", fontSize = 18.sp)
            Spacer(Modifier.height(24.dp))
            LinearProgressIndicator(
                progress = { percentage },
                color = barColor,
                trackColor = Color(0xFFE7E0EC),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(10.dp)
            )
            Spacer(Modifier.height(20.dp))
            Text(
                "${(percentage * 100).toInt()}% de ocupación",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = textColor
            )
            Text(statusText, fontSize = 15.sp)
        }
    }
}

@Composable
private fun Leyenda(color: Color, texto: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            Modifier
                .size(12.dp)
                .background(color, CircleShape)
        )
        Spacer(Modifier.width(4.dp))
        Text(texto, fontSize = 13.sp)
    }
}

@Preview(showBackground = true)
@Composable
fun AforoIndicatorPreview() {
    AforoTheme {
        AforoIndicator(current = 32, max = 50)
    }
}
