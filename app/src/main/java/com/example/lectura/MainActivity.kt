package com.example.lectura

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LecturaScreen()
        }
    }
}

@Composable
fun LecturaScreen() {
    // Variables de estado para los ajustes de lectura
    var tamano by remember { mutableStateOf(20f) }
    var interlineado by remember { mutableStateOf("Normal") }
    var modoOscuro by remember { mutableStateOf(false) }
    var usaSerif by remember { mutableStateOf(false) }

    // Texto de prueba para la vista previa
    val textoPrueba = "Era un dia muy tranquilo en utt hasta que inició el CUATRIMESTRE, a 1 2 3 4 5 6 7 8 9 !  # $ % & /("

    MaterialTheme(
        colorScheme = if (modoOscuro) darkColorScheme() else lightColorScheme()
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            ) {
                // Título de la pantalla
                Text(
                    text = "Ajustes de lectura",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(vertical = 8.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Tamaño de letra
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "Tamaño de letra", fontSize = 16.sp)
                    Text(
                        text = "${tamano.toInt()} sp",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Slider(
                    value = tamano,
                    onValueChange = { tamano = it },
                    valueRange = 14f..32f
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "A", fontSize = 14.sp)
                    Text(text = "A", fontSize = 24.sp)
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Interlineado
                Text(text = "Interlineado", fontSize = 16.sp)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val opciones = listOf("Compacto", "Normal", "Amplio")
                    for (opcion in opciones) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            RadioButton(
                                selected = interlineado == opcion,
                                onClick = { interlineado = opcion }
                            )
                            Text(text = opcion, fontSize = 14.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Modo oscuro
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "Modo oscuro", fontSize = 16.sp)
                    Switch(
                        checked = modoOscuro,
                        onCheckedChange = { modoOscuro = it }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Fuente
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "Fuente", fontSize = 16.sp)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(
                            onClick = { usaSerif = true },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (usaSerif) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                                contentColor = if (usaSerif) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        ) {
                            Text("Serif")
                        }
                        Button(
                            onClick = { usaSerif = false },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (!usaSerif) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                                contentColor = if (!usaSerif) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        ) {
                            Text("Sans")
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Sección de vista previa
                Text(
                    text = "VISTA PREVIA",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(8.dp))

                val lineH = when (interlineado) {
                    "Compacto" -> (tamano * 1.1f).sp
                    "Amplio" -> (tamano * 1.6f).sp
                    else -> (tamano * 1.35f).sp
                }

                val previewBg = if (modoOscuro) Color.White else Color(0xFF1E1E1E)
                val previewText = if (modoOscuro) Color.Black else Color.White

                Box(
                    modifier = Modifier.fillMaxWidth()
                        .background(previewBg, shape = RoundedCornerShape(16.dp))
                        .padding(16.dp)
                ) {
                    Text(
                        text = textoPrueba,
                        fontSize = tamano.sp,
                        lineHeight = lineH,
                        fontFamily = if (usaSerif) FontFamily.Serif else FontFamily.SansSerif,
                        color = previewText
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Botón para restablecer valores por defecto
                OutlinedButton(
                    onClick = {
                        tamano = 20f
                        interlineado = "Normal"
                        modoOscuro = false
                        usaSerif = false
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(25.dp)
                ) {
                    Text(text = "Restablecer", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
