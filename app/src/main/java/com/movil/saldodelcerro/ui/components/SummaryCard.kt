package com.movil.saldodelcerro.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingDown
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.movil.saldodelcerro.ui.theme.FinGoMuted
import com.movil.saldodelcerro.ui.theme.FinGoSurface
import com.movil.saldodelcerro.ui.theme.SaldoDelCerroTheme
import com.movil.saldodelcerro.ui.theme.finGo
import com.movil.saldodelcerro.utils.formatMoney
import java.math.BigDecimal

/**
 * Tarjeta de resumen financiero (ingresos o egresos).
 * @param isIncome true para ingresos (verde) y false para egresos (rojo).
 */
@Composable
fun SummaryCard(
    label: String,
    value: BigDecimal,
    isIncome: Boolean,
    modifier: Modifier = Modifier
) {
    val colors = MaterialTheme.finGo
    val accent = if (isIncome) colors.income else colors.expense
    val container = if (isIncome) colors.incomeContainer else colors.expenseContainer
    val icon: ImageVector = if (isIncome) Icons.AutoMirrored.Filled.TrendingUp else Icons.AutoMirrored.Filled.TrendingDown

    Card(
        modifier = modifier,
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(containerColor = FinGoSurface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(container),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = accent,
                    modifier = Modifier.size(22.dp)
                )
            }
            Text(label, style = MaterialTheme.typography.labelLarge, color = FinGoMuted)
            Text(
                text = value.formatMoney(),
                style = MaterialTheme.typography.titleMedium,
                color = accent
            )
        }
    }
}

@Preview(showBackground = true, name = "SummaryCard")
@Composable
private fun SummaryCardPreview() {
    SaldoDelCerroTheme(dynamicColor = false) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            SummaryCard(
                label = "Ingresos",
                value = BigDecimal("1500.00"),
                isIncome = true,
                modifier = Modifier.weight(1f)
            )
            SummaryCard(
                label = "Egresos",
                value = BigDecimal("320.75"),
                isIncome = false,
                modifier = Modifier.weight(1f)
            )
        }
    }
}
