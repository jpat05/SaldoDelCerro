package com.movil.saldodelcerro.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.movil.saldodelcerro.ui.theme.SaldoDelCerroTheme
import com.movil.saldodelcerro.utils.formatMoney
import java.math.BigDecimal

@Composable
fun SummaryRow(label: String, value: BigDecimal) {
    Row(
        Modifier.fillMaxWidth().padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label)
        Text(value.formatMoney(), style = MaterialTheme.typography.titleMedium)
    }
}

@Preview(showBackground = true, name = "SummaryRow")
@Composable
private fun SummaryRowPreview() {
    SaldoDelCerroTheme(dynamicColor = false) {
        Column(Modifier.fillMaxWidth().padding(16.dp)) {
            SummaryRow("Saldo", BigDecimal("1179.25"))
            SummaryRow("Ingresos", BigDecimal("1500.00"))
            SummaryRow("Egresos", BigDecimal("320.75"))
        }
    }
}
