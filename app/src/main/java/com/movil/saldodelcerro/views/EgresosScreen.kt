package com.movil.saldodelcerro.views

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Gamepad
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Sell
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.filled.*
private val VerdeEgresos = Color(0xFF4F9F83)
private val AzulEgresos = Color(0xFF368BA7)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EgresosScreen(
    onRegresar: () -> Unit = {},
    onRegistrarGasto: (Double, String, String) -> Unit = { _, _, _ -> }
) {
    var monto by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var categoria by remember { mutableStateOf("") }
    var mensaje by remember { mutableStateOf("") }
    var mostrarCategorias by remember { mutableStateOf(false) }

    val categorias = listOf(
        "Alimentación",
        "Transporte",
        "Estudios",
        "Entretenimiento",
        "Salud",
        "Otros"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Registrar gasto") },
                navigationIcon = {
                    IconButton(onClick = onRegresar) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Regresar"
                        )
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            OutlinedTextField(
                value = monto,
                onValueChange = {
                    if (it.matches(Regex("^\\d*\\.?\\d{0,2}$"))) {
                        monto = it
                    }
                    mensaje = ""
                },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Monto") },
                placeholder = { Text("Ej. 20.00") },
                leadingIcon = {
                    Icon(
                        Icons.Default.Remove,
                        contentDescription = null,
                        tint = AzulEgresos
                    )
                },
                prefix = { Text("$ ") },
                singleLine = true
            )

            Text(
                "Categoría",
                fontWeight = FontWeight.SemiBold
            )

            OutlinedCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { mostrarCategorias = !mostrarCategorias }
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Sell,
                        contentDescription = null,
                        tint = VerdeEgresos
                    )
                    Spacer(Modifier.width(12.dp))
                    Text(
                        categoria.ifBlank { "Selecciona una categoría" },
                        modifier = Modifier.weight(1f),
                        color = if (categoria.isBlank())
                            Color.Gray else Color.DarkGray
                    )
                    Text(if (mostrarCategorias) "▲" else "▼")
                }
            }

            if (mostrarCategorias) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    categorias.forEach { nombre ->
                        TextButton(
                            onClick = {
                                categoria = nombre
                                mostrarCategorias = false
                                mensaje = ""
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                nombre,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
            }

            OutlinedTextField(
                value = descripcion,
                onValueChange = { descripcion = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Descripción (opcional)") },
                placeholder = { Text("Ej. Compra en supermercado") },
                leadingIcon = {
                    Icon(
                        Icons.Default.ReceiptLong,
                        contentDescription = null
                    )
                },
                singleLine = true
            )

            Button(
                onClick = {
                    val valor = monto.toDoubleOrNull()
                    if (valor == null || valor <= 0) {
                        mensaje = "Ingresa un monto mayor que cero."
                    } else if (categoria.isBlank()) {
                        mensaje = "Selecciona una categoría."
                    } else {
                        onRegistrarGasto(valor, categoria, descripcion)
                        mensaje = "Gasto listo para registrar."
                        monto = ""
                        descripcion = ""
                        categoria = ""
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = AzulEgresos
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Registrar gasto", fontSize = 16.sp)
            }

            if (mensaje.isNotBlank()) {
                Text(
                    mensaje,
                    color = if (mensaje.startsWith("Gasto"))
                        VerdeEgresos else MaterialTheme.colorScheme.error
                )
            }

            HorizontalDivider()

            Text(
                "Categorías",
                fontSize = 19.sp,
                fontWeight = FontWeight.Bold
            )

            val iconos = listOf(
                Icons.Default.Fastfood,
                Icons.Default.DirectionsBus,
                Icons.Default.School,
                Icons.Default.Gamepad,
                Icons.Default.Favorite,
                Icons.Default.MoreHoriz
            )

            val colores = listOf(
                Color(0xFFE5F3EF),
                Color(0xFFE5EFF8),
                Color(0xFFEDE8FA),
                Color(0xFFF0E8F8),
                Color(0xFFFBE8EB),
                Color(0xFFF2ECE7)
            )

            for (fila in 0..1) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    for (columna in 0..2) {
                        val indice = fila * 3 + columna
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .clickable { categoria = categorias[indice] }
                                .padding(vertical = 8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(68.dp)
                                    .background(
                                        colores[indice],
                                        CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = iconos[indice],
                                    contentDescription = categorias[indice],
                                    modifier = Modifier.size(29.dp),
                                    tint = VerdeEgresos
                                )
                            }
                            Spacer(Modifier.height(6.dp))
                            Text(
                                categorias[indice],
                                fontSize = 11.sp,
                                maxLines = 2
                            )
                        }
                    }
                }
            }
        }
    }
}
