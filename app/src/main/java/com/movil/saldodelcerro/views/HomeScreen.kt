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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.movil.saldodelcerro.services.WalletService
import com.movil.saldodelcerro.utils.formatMoney
import java.math.BigDecimal

@Composable
fun HomeScreen(
    walletService: WalletService,
    modifier: Modifier = Modifier
) {
    val balance by walletService.availableBalance.collectAsState(initial = BigDecimal.ZERO)
    val totalIncome by walletService.totalIncome.collectAsState(initial = BigDecimal.ZERO)
    val totalExpense by walletService.totalExpense.collectAsState(initial = BigDecimal.ZERO)
    val transactions by walletService.allTransactionsDto.collectAsState(initial = emptyList())

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item { Text("Saldo desde el Cerro", style = MaterialTheme.typography.headlineMedium) }

        item {
            Card(Modifier.fillMaxWidth()) {
                Column(
                    Modifier.fillMaxWidth().padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Saldo disponible", style = MaterialTheme.typography.labelLarge)
                    Text(balance.formatMoney(), style = MaterialTheme.typography.headlineLarge)
                }
            }
        }

        item {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                SummaryCard("Ingresos", totalIncome, Modifier.weight(1f))
                SummaryCard("Egresos", totalExpense, Modifier.weight(1f))
            }
        }

        item { Text("Últimos movimientos", style = MaterialTheme.typography.titleMedium) }
        items(transactions.take(5)) { TransactionRow(it) }
    }
}

@Composable
private fun SummaryCard(label: String, value: BigDecimal, modifier: Modifier = Modifier) {
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