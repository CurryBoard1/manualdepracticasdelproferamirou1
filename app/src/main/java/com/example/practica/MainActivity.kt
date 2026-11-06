package com.example.practica

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(Modifier.fillMaxSize()) {
                    ImpresorChars()
                }
            }
        }
    }
}

@Composable
fun ImpresorChars() {
    var texto by remember { mutableStateOf("") }
    var caracteres by remember { mutableStateOf(listOf<Char>()) }

    Column(Modifier.fillMaxSize().systemBarsPadding().padding(16.dp)) {
        OutlinedTextField(
            value = texto,
            onValueChange = { texto = it },
            label = { Text("Escribe una palabra o frase") },
            modifier = Modifier.fillMaxWidth()
        )

        Button(onClick = { caracteres = texto.toList() }) {
            Text("Imprimir caracteres")
        }

        Text("Total de caracteres: ${caracteres.size}")

        LazyColumn {
            itemsIndexed(caracteres) { i, c ->
                Text("${i + 1}: $c")
            }
        }
    }
}
