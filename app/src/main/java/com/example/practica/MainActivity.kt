package com.example.practica

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(Modifier.fillMaxSize()) {
                    Calculadora()
                }
            }
        }
    }
}

@Composable
fun Calculadora() {
    var num1 by remember { mutableStateOf("") }
    var num2 by remember { mutableStateOf("") }
    var resultado by remember { mutableStateOf("") }

    fun calcular(operacion: Char) {
        val a = num1.toDoubleOrNull()
        val b = num2.toDoubleOrNull()

        if (a == null || b == null) {
            resultado = "Escribe dos números"
            return
        }

        resultado = when (operacion) {
            '+' -> (a + b).toString()
            '-' -> (a - b).toString()
            '*' -> (a * b).toString()
            else -> if (b == 0.0) "No se puede dividir entre 0" else (a / b).toString()
        }
    }

    Column(Modifier.fillMaxSize().systemBarsPadding().padding(16.dp)) {
        Text("Calculadora", fontSize = 28.sp)

        OutlinedTextField(
            value = num1,
            onValueChange = { num1 = it },
            label = { Text("Primer número") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = num2,
            onValueChange = { num2 = it },
            label = { Text("Segundo número") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            modifier = Modifier.fillMaxWidth()
        )

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = { calcular('+') }) { Text("+") }
            Button(onClick = { calcular('-') }) { Text("-") }
            Button(onClick = { calcular('*') }) { Text("×") }
            Button(onClick = { calcular('/') }) { Text("÷") }
        }

        Text("Resultado: $resultado", fontSize = 22.sp)
    }
}
