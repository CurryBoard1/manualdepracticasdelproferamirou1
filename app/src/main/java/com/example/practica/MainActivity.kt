package com.example.practica

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(Modifier.fillMaxSize()) {
                    ImpresorNumeros()
                }
            }
        }
    }
}

@Composable
fun ImpresorNumeros() {
    var limite by remember { mutableStateOf("") }
    var numeros by remember { mutableStateOf(listOf<Int>()) }

    Column(Modifier.fillMaxSize().systemBarsPadding().padding(16.dp)) {
        OutlinedTextField(
            value = limite,
            onValueChange = { limite = it },
            label = { Text("Hasta qué número") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        Button(onClick = {
            val n = limite.toIntOrNull() ?: 0
            numeros = (1..n).toList()
        }) { Text("Imprimir números") }

        LazyColumn {
            items(numeros) { n ->
                Text("$n")
            }
        }
    }
}
