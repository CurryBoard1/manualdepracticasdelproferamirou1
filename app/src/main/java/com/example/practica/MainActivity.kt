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

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(Modifier.fillMaxSize()) {
                    Listas()
                }
            }
        }
    }
}

@Composable
fun Listas() {
    val lista = remember { mutableStateListOf("Manzana", "Pera", "Uva") }
    var nuevo by remember { mutableStateOf("") }

    Column(Modifier.fillMaxSize().systemBarsPadding().padding(16.dp)) {
        Text("Mi lista")

        Row(verticalAlignment = Alignment.CenterVertically) {
            OutlinedTextField(
                value = nuevo,
                onValueChange = { nuevo = it },
                label = { Text("Nuevo elemento") },
                modifier = Modifier.weight(1f)
            )
            Button(
                onClick = {
                    lista.add(nuevo)
                    nuevo = ""
                },
                enabled = nuevo.isNotBlank()
            ) { Text("Agregar") }
        }

        LazyColumn {
            items(lista) { elemento ->
                Row(
                    Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(elemento, modifier = Modifier.weight(1f))
                    TextButton(onClick = { lista.remove(elemento) }) { Text("Quitar") }
                }
            }
        }
    }
}
