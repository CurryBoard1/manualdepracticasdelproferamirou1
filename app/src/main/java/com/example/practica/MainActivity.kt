package com.example.practica

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(Modifier.fillMaxSize()) {
                    Cards()
                }
            }
        }
    }
}

@Composable
fun Cards() {
    Column(Modifier.fillMaxSize().systemBarsPadding().padding(8.dp)) {
        Tarjeta("Card 1", "Esta es la primera tarjeta")
        Tarjeta("Card 2", "Esta es la segunda tarjeta")
        Tarjeta("Card 3", "Esta es la tercera tarjeta")
    }
}

@Composable
fun Tarjeta(titulo: String, descripcion: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(titulo, fontSize = 22.sp)
            Text(descripcion)
        }
    }
}
