package com.movil.saldodelcerro.ui.components

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
fun BalanceCard(
    balance: BigDecimal,
    modifier: Modifier = Modifier
) {
    Card(modifier.fillMaxWidth()) {
        Column(
            Modifier.fillMaxWidth().padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Saldo disponible", style = MaterialTheme.typography.labelLarge)
            Text(balance.formatMoney(), style = MaterialTheme.typography.headlineLarge)
        }
    }
}

@Preview(showBackground = true, name = "BalanceCard")
@Composable
private fun BalanceCardPreview() {
    SaldoDelCerroTheme(dynamicColor = false) {
        BalanceCard(balance = BigDecimal("1234.56"))
    }
}
