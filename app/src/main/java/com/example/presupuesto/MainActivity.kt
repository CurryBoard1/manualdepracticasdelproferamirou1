package com.example.presupuesto

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.util.Locale

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(Modifier.fillMaxSize()) {
                    Box(Modifier.fillMaxSize().systemBarsPadding()) {
                        PresupuestoScreen()
                    }
                }
            }
        }
    }
}

enum class Categoria(val nombre: String) {
    COMIDA("Comida"),
    TRANSPORTE("Transporte"),
    RENTA("Renta"),
    OCIO("Ocio"),
    OTROS("Otros")
}

data class Gasto(
    val id: Int,
    val concepto: String,
    val monto: Double,
    val categoria: Categoria
)

private val rojo = Color(0xFFB3261E)
private val verde = Color(0xFF2E7D32)
private val naranja = Color(0xFFF9A825)

// sin decimales cuando el número es entero ($2,000), con 2 si no ($85.50)
private fun dinero(x: Double): String =
    if (x == Math.floor(x)) "$" + String.format(Locale.US, "%,.0f", x)
    else "$" + String.format(Locale.US, "%,.2f", x)

@Composable
fun PresupuestoScreen() {
    var ingresoTexto by remember { mutableStateOf("") }
    val gastos = remember { mutableStateListOf<Gasto>() }
    var siguienteId by remember { mutableStateOf(1) }

    val ingreso = ingresoTexto.toDoubleOrNull() ?: 0.0

    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text("Mi presupuesto", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(20.dp))

        OutlinedTextField(
            value = ingresoTexto,
            onValueChange = { ingresoTexto = it },
            label = { Text("Ingreso mensual") },
            prefix = { Text("$ ") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(12.dp))
        ResumenPresupuesto(ingreso, gastos)

        Spacer(Modifier.height(20.dp))
        FormularioGasto(onAgregar = { concepto, monto, categoria ->
            gastos.add(Gasto(siguienteId, concepto, monto, categoria))
            siguienteId++
        })

        Spacer(Modifier.height(20.dp))
        ListaGastos(gastos, onEliminar = { g -> gastos.remove(g) })
    }
}

// concepto, monto, categoria y menuAbierto se quedan aquí: son estado de edición
// y al padre solo le sirve recibir el gasto ya armado.
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormularioGasto(onAgregar: (concepto: String, monto: Double, Categoria) -> Unit) {
    var concepto by remember { mutableStateOf("") }
    var montoTexto by remember { mutableStateOf("") }
    var categoria by remember { mutableStateOf(Categoria.COMIDA) }
    var menuAbierto by remember { mutableStateOf(false) }

    val monto = montoTexto.toDoubleOrNull()
    val valido = concepto.isNotBlank() && monto != null && monto > 0

    Column {
        Text("Nuevo gasto", fontSize = 22.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value = concepto,
            onValueChange = { concepto = it },
            label = { Text("Concepto") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Row(
            Modifier.padding(top = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = montoTexto,
                onValueChange = { montoTexto = it },
                label = { Text("Monto") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true,
                modifier = Modifier.weight(1f)
            )

            ExposedDropdownMenuBox(
                expanded = menuAbierto,
                onExpandedChange = { menuAbierto = it },
                modifier = Modifier.weight(1f)
            ) {
                OutlinedTextField(
                    value = categoria.nombre,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Categoría") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = menuAbierto) },
                    modifier = Modifier.menuAnchor()
                )
                ExposedDropdownMenu(
                    expanded = menuAbierto,
                    onDismissRequest = { menuAbierto = false }
                ) {
                    Categoria.entries.forEach { c ->
                        DropdownMenuItem(
                            text = { Text(c.nombre) },
                            onClick = {
                                categoria = c
                                menuAbierto = false
                            }
                        )
                    }
                }
            }
        }

        Spacer(Modifier.height(12.dp))
        FilledTonalButton(
            onClick = {
                onAgregar(concepto.trim(), monto!!, categoria)
                concepto = ""
                montoTexto = ""
            },
            enabled = valido,
            modifier = Modifier.fillMaxWidth().height(52.dp)
        ) { Text("Agregar gasto", fontWeight = FontWeight.Bold, fontSize = 16.sp) }
    }
}

@Composable
fun ListaGastos(gastos: List<Gasto>, onEliminar: (Gasto) -> Unit) {
    Column {
        Text("Gastos recientes", fontSize = 22.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(8.dp))
        if (gastos.isEmpty()) {
            Text("Todavía no hay gastos", color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        gastos.forEach { g ->
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(Modifier.weight(1f)) {
                    Text(g.concepto, fontWeight = FontWeight.Bold, fontSize = 19.sp)
                    Text(g.categoria.nombre, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Text("−" + dinero(g.monto), color = rojo, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                IconButton(onClick = { onEliminar(g) }) {
                    Icon(
                        Icons.Default.Close,
                        contentDescription = "Eliminar",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
fun ResumenPresupuesto(ingreso: Double, gastos: List<Gasto>) {
    // todo derivado
    val total = gastos.sumOf { it.monto }
    val saldo = ingreso - total
    val porcentaje = if (ingreso > 0) (total / ingreso).toFloat() else 0f
    val top = gastos
        .groupBy { it.categoria }
        .mapValues { (_, lista) -> lista.sumOf { it.monto } }
        .maxByOrNull { it.value }

    val fondoCard = when {
        porcentaje > 1f -> Color(0xFFFBE4E1)
        porcentaje >= 0.8f -> Color(0xFFFFF5DC)
        else -> Color(0xFFF3EDF7)
    }
    val colorBarra = when {
        porcentaje > 1f -> rojo
        porcentaje >= 0.8f -> naranja
        else -> MaterialTheme.colorScheme.primary
    }

    Card(
        colors = CardDefaults.cardColors(containerColor = fondoCard),
        shape = RoundedCornerShape(24.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(Modifier.padding(20.dp)) {
            Row(Modifier.fillMaxWidth()) {
                Column(Modifier.weight(1f)) {
                    Text("Gastado", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(dinero(total), fontSize = 30.sp, fontWeight = FontWeight.Bold)
                }
                Column(Modifier.weight(1f)) {
                    Text("Saldo", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(
                        dinero(saldo),
                        fontSize = 30.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (saldo >= 0) verde else rojo
                    )
                }
            }
            Spacer(Modifier.height(12.dp))
            LinearProgressIndicator(
                progress = { porcentaje.coerceIn(0f, 1f) },
                color = colorBarra,
                trackColor = Color(0xFFE7E0EC),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(10.dp)
            )
            Spacer(Modifier.height(12.dp))
            val textoTop = if (top != null) " · Mayor gasto: ${top.key.nombre} (${dinero(top.value)})" else ""
            Text("${(porcentaje * 100).toInt()} % usado$textoTop", fontSize = 15.sp)
        }
    }

    if (porcentaje > 1f) {
        Alerta(
            fondo = Color(0xFFF9DEDC),
            colorTexto = Color(0xFF8C1D18),
            texto = "Te pasaste del presupuesto por ${dinero(total - ingreso)}"
        )
    } else if (porcentaje >= 0.8f) {
        Alerta(
            fondo = Color(0xFFFFDFB5),
            colorTexto = Color(0xFF7A4A00),
            texto = "Cuidado: pasaste el 80 %"
        )
    }
}

@Composable
private fun Alerta(fondo: Color, colorTexto: Color, texto: String) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(top = 12.dp)
            .background(fondo, RoundedCornerShape(16.dp))
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(Icons.Default.Warning, contentDescription = null, tint = colorTexto)
        Spacer(Modifier.width(12.dp))
        Text(texto, color = colorTexto, fontWeight = FontWeight.Bold, fontSize = 17.sp)
    }
}

@Preview(showBackground = true)
@Composable
fun ListaGastosPreview() {
    MaterialTheme {
        ListaGastos(
            listOf(
                Gasto(1, "Renta mensual", 2000.0, Categoria.RENTA),
                Gasto(2, "Despensa", 650.0, Categoria.COMIDA)
            ),
            {}
        )
    }
}