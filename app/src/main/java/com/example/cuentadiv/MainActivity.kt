package com.example.cuentadiv

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.util.Locale
import kotlin.math.ceil

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(Modifier.fillMaxSize()) {
                    Box(Modifier.fillMaxSize().systemBarsPadding()) {
                        CuentaScreen()
                    }
                }
            }
        }
    }
}

private fun dinero(x: Double) = "$" + String.format(Locale.US, "%,.2f", x)

@Composable
fun CuentaScreen() {
    var montoTexto by remember { mutableStateOf("") }
    var propinaPct by remember { mutableStateOf(10) }
    var usaOtro by remember { mutableStateOf(false) }
    var personas by remember { mutableStateOf(2) }
    var redondear by remember { mutableStateOf(false) }

    // derivados
    val monto = montoTexto.toDoubleOrNull() ?: 0.0
    val montoInvalido = montoTexto.isNotEmpty() && montoTexto.toDoubleOrNull() == null
    val propina = monto * propinaPct / 100.0
    val total = monto + propina
    val sinRedondear = total / personas
    val porPersona = if (redondear) ceil(sinRedondear) else sinRedondear
    val sobrante = if (redondear) porPersona * personas - total else null

    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text("Cuenta dividida", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(24.dp))

        OutlinedTextField(
            value = montoTexto,
            onValueChange = { montoTexto = it },
            label = { Text("Monto de la cuenta") },
            prefix = { Text("$ ") },
            isError = montoInvalido,
            supportingText = {
                if (montoInvalido) Text("Monto no válido, se toma como $0")
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(8.dp))
        SelectorPropina(
            porcentaje = propinaPct,
            usaOtro = usaOtro,
            onPorcentajeChange = { propinaPct = it },
            onOtroChange = { usaOtro = it }
        )

        Spacer(Modifier.height(8.dp))
        StepperPersonas(personas) { personas = it }

        Row(
            Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Redondear por persona", fontSize = 20.sp)
            Switch(checked = redondear, onCheckedChange = { redondear = it })
        }

        HorizontalDivider(Modifier.padding(vertical = 8.dp))

        ResultadoCuenta(propinaPct, propina, total, porPersona, personas, sobrante)
    }
}

@Composable
fun SelectorPropina(
    porcentaje: Int,
    usaOtro: Boolean,
    onPorcentajeChange: (Int) -> Unit,
    onOtroChange: (Boolean) -> Unit
) {
    Column {
        Text("Propina", fontSize = 20.sp)
        Spacer(Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf(10, 15, 20).forEach { p ->
                ChipPropina("$p %", !usaOtro && porcentaje == p) {
                    onOtroChange(false)
                    onPorcentajeChange(p)
                }
            }
            ChipPropina("Otro", usaOtro) { onOtroChange(true) }
        }
        if (usaOtro) {
            Text("$porcentaje %", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
            Slider(
                value = porcentaje.toFloat(),
                onValueChange = { onPorcentajeChange(it.toInt()) },
                valueRange = 0f..30f
            )
        }
    }
}

@Composable
private fun ChipPropina(texto: String, activo: Boolean, onClick: () -> Unit) {
    FilterChip(
        selected = activo,
        onClick = onClick,
        label = { Text(texto, fontSize = 16.sp) },
        leadingIcon = if (activo) {
            { Icon(Icons.Default.Check, null, Modifier.size(FilterChipDefaults.IconSize)) }
        } else null
    )
}

@Composable
fun StepperPersonas(personas: Int, onChange: (Int) -> Unit) {
    Row(
        Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("Personas", fontSize = 20.sp)
        Row(verticalAlignment = Alignment.CenterVertically) {
            OutlinedIconButton(
                onClick = { onChange(personas - 1) },
                enabled = personas > 1,
                modifier = Modifier.size(38.dp)
            ) { Text("−", fontSize = 20.sp) }
            Text(
                "$personas",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.width(40.dp)
            )
            OutlinedIconButton(
                onClick = { onChange(personas + 1) },
                enabled = personas < 20,
                modifier = Modifier.size(38.dp)
            ) { Text("+", fontSize = 20.sp) }
        }
    }
}

@Composable
fun ResultadoCuenta(
    propinaPct: Int,
    propina: Double,
    total: Double,
    porPersona: Double,
    personas: Int,
    sobrante: Double?
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF3EDF7)),
        shape = RoundedCornerShape(24.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            FilaDato("Propina ($propinaPct %)", dinero(propina))
            Spacer(Modifier.height(8.dp))
            FilaDato("Total con propina", dinero(total))

            HorizontalDivider(Modifier.padding(vertical = 14.dp))

            Text("Cada quien paga", fontSize = 18.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(
                dinero(porPersona),
                fontSize = 56.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                "entre $personas personas",
                fontSize = 18.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            if (sobrante != null) {
                Text(
                    "Sobrante por redondeo: ${dinero(sobrante)}",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun FilaDato(etiqueta: String, valor: String) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(etiqueta, fontSize = 17.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(valor, fontSize = 17.sp, fontWeight = FontWeight.Bold)
    }
}

@Preview(showBackground = true)
@Composable
fun ResultadoCuentaPreview() {
    MaterialTheme { ResultadoCuenta(20, 250.0, 1500.0, 300.0, 5, 0.0) }
}