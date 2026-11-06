package com.example.practica

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(Modifier.fillMaxSize()) {
                    ColorCards()
                }
            }
        }
    }
}

val colores = listOf(Color.Red, Color.Green, Color.Blue, Color.Yellow, Color.Magenta, Color.Cyan)

@Composable
fun ColorCards() {
    Column(Modifier.fillMaxSize().systemBarsPadding().padding(8.dp)) {
        Text("Toca una card para cambiar su color", modifier = Modifier.padding(8.dp))
        ColorCard("Card 1", 0)
        ColorCard("Card 2", 1)
        ColorCard("Card 3", 2)
        ColorCard("Card 4", 3)
    }
}

@Composable
fun ColorCard(nombre: String, colorInicial: Int) {
    var indice by remember { mutableStateOf(colorInicial) }

    Card(
        colors = CardDefaults.cardColors(containerColor = colores[indice]),
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { indice = (indice + 1) % colores.size }
    ) {
        Text(
            nombre,
            fontSize = 22.sp,
            color = Color.Black,
            modifier = Modifier.padding(24.dp)
        )
    }
}
