package com.movil.saldodelcerro.views

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.movil.saldodelcerro.services.WalletService
import com.movil.saldodelcerro.utils.formatMoney
import java.math.BigDecimal

@Composable
fun SummaryScreen(
    walletService: WalletService,
    modifier: Modifier = Modifier
) {
    val balance by walletService.availableBalance.collectAsState(initial = BigDecimal.ZERO)
    val totalIncome by walletService.totalIncome.collectAsState(initial = BigDecimal.ZERO)
    val totalExpense by walletService.totalExpense.collectAsState(initial = BigDecimal.ZERO)
    val categories by walletService.categoryTotals.collectAsState(initial = emptyList())
    val transactions by walletService.allTransactionsDto.collectAsState(initial = emptyList())

    val maxCategory = categories.maxOfOrNull { it.total } ?: BigDecimal.ONE

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item { Text("Resumen / Historial", style = MaterialTheme.typography.headlineMedium) }

        item {
            Card(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp)) {
                    Text("Estado financiero", style = MaterialTheme.typography.titleMedium)
                    SummaryRow("Saldo", balance)
                    SummaryRow("Ingresos", totalIncome)
                    SummaryRow("Egresos", totalExpense)
                }
            }
        }

        item {
            Card(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp)) {
                    Text("Gastos por categoría", style = MaterialTheme.typography.titleMedium)
                    categories.forEach { cat ->
                        Text(cat.name, style = MaterialTheme.typography.titleSmall)
                        LinearProgressIndicator(
                            progress = {
                                (cat.total.toFloat() / maxCategory.toFloat()).coerceIn(0f, 1f)
                            },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Text(cat.total.formatMoney(), style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }

        item { Text("Movimientos", style = MaterialTheme.typography.titleMedium) }
        items(transactions) { TransactionRow(it) }
    }
}

@Composable
private fun SummaryRow(label: String, value: BigDecimal) {
    Row(
        Modifier.fillMaxWidth().padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label)
        Text(value.formatMoney(), fontWeight = FontWeight.Bold)
    }
}