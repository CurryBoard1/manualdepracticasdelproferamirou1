package com.example.practica

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(Modifier.fillMaxSize()) {
                    CardsDinamicas()
                }
            }
        }
    }
}

@Composable
fun CardsDinamicas() {
    val cards = remember { mutableStateListOf("Card 1", "Card 2") }
    var numero by remember { mutableStateOf(3) }

    Column(Modifier.fillMaxSize().systemBarsPadding().padding(8.dp)) {
        Button(
            onClick = {
                cards.add("Card $numero")
                numero++
            },
            modifier = Modifier.padding(8.dp)
        ) { Text("Agregar card") }

        LazyColumn {
            items(cards, key = { it }) { nombre ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                ) {
                    Row(
                        Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(nombre, fontSize = 20.sp, modifier = Modifier.weight(1f))
                        TextButton(onClick = { cards.remove(nombre) }) { Text("Eliminar") }
                    }
                }
            }
        }
    }
}
