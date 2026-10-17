package com.movil.saldodelcerro.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.movil.saldodelcerro.data.models.CategoryTotal
import java.math.BigDecimal
import kotlin.math.roundToInt

@Composable
fun CategoryDonutChart(
    categories: List<CategoryTotal>,
    modifier: Modifier = Modifier
) {
    // Solo categorías con gasto
    val visibles = categories.filter { it.total > BigDecimal.ZERO }
    val total = visibles.fold(BigDecimal.ZERO) { acc, c -> acc + c.total }
    if (total <= BigDecimal.ZERO) return

    // Los colores se calculan ANTES del Canvas, en contexto @Composable.
    // Así el archivo funciona igual hoy y después del merge de ramas.
    val colores = visibles.map { getCategoryStyle(it.name).tintColor }

    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        // El dibujo del donut
        Canvas(Modifier.size(120.dp)) {
            val grosor = 24.dp.toPx()
            val inset = grosor / 2
            var angulo = -90f
            visibles.forEachIndexed { i, cat ->
                val barrido = cat.total.toFloat() / total.toFloat() * 360f
                drawArc(
                    color = colores[i],
                    startAngle = angulo,
                    sweepAngle = barrido,
                    useCenter = false,
                    topLeft = Offset(inset, inset),
                    size = Size(size.width - grosor, size.height - grosor),
                    style = Stroke(width = grosor)
                )
                angulo += barrido
            }
        }

        Spacer(Modifier.width(16.dp))

        // La leyenda: color + nombre + porcentaje
        Column(Modifier.weight(1f)) {
            visibles.forEachIndexed { i, cat ->
                val porcentaje = (cat.total.toFloat() / total.toFloat() * 100).roundToInt()
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        Modifier
                            .size(10.dp)
                            .background(colores[i], CircleShape)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(cat.name, Modifier.weight(1f), style = MaterialTheme.typography.bodyMedium)
                    Text("$porcentaje%", style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
    }
}