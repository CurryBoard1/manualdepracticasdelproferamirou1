package com.example.practica

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
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
                    Incremento()
                }
            }
        }
    }
}

@Composable
fun Incremento() {
    var contador by remember { mutableStateOf(0) }

    Column(
        modifier = Modifier.fillMaxSize().systemBarsPadding(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Contador", fontSize = 24.sp)
        Text("$contador", fontSize = 60.sp)

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Button(onClick = { contador-- }) { Text("-") }
            Button(onClick = { contador++ }) { Text("+") }
        }
        Spacer(Modifier.height(12.dp))
        Button(onClick = { contador = 0 }) { Text("Reiniciar") }
    }
}
