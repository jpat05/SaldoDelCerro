package com.movil.saldodelcerro.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.movil.saldodelcerro.ui.theme.SaldoDelCerroTheme
import com.movil.saldodelcerro.utils.formatMoney
import java.math.BigDecimal

@Composable
fun SummaryCard(label: String, value: BigDecimal, modifier: Modifier = Modifier) {
    Card(modifier) {
        Column(
            Modifier.fillMaxWidth().padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(label, style = MaterialTheme.typography.labelLarge)
            Text(value.formatMoney(), style = MaterialTheme.typography.titleMedium)
        }
    }
}

@Preview(showBackground = true, name = "SummaryCard")
@Composable
private fun SummaryCardPreview() {
    SaldoDelCerroTheme(dynamicColor = false) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            SummaryCard(label = "Ingresos", value = BigDecimal("1500.00"), modifier = Modifier.fillMaxWidth())
            SummaryCard(label = "Egresos", value = BigDecimal("320.75"), modifier = Modifier.fillMaxWidth())
        }
    }
}
