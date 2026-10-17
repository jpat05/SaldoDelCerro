package com.movil.saldodelcerro.ui.screens.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.movil.saldodelcerro.data.models.CategoryEntity
import com.movil.saldodelcerro.data.models.TransactionDto
import com.movil.saldodelcerro.data.models.TransactionEntity
import com.movil.saldodelcerro.services.LedgerService
import com.movil.saldodelcerro.ui.components.BalanceCard
import com.movil.saldodelcerro.ui.components.SummaryCard
import com.movil.saldodelcerro.ui.components.TransactionRow
import com.movil.saldodelcerro.ui.theme.SaldoDelCerroTheme
import java.math.BigDecimal

@Composable
fun HomeScreen(
    ledgerService: LedgerService,
    modifier: Modifier = Modifier
) {
    val balance by ledgerService.availableBalance.collectAsState(initial = BigDecimal.ZERO)
    val totalIncome by ledgerService.totalIncome.collectAsState(initial = BigDecimal.ZERO)
    val totalExpense by ledgerService.totalExpense.collectAsState(initial = BigDecimal.ZERO)
    val movements by ledgerService.movements.collectAsState(initial = emptyList())

    HomeContent(
        balance = balance,
        totalIncome = totalIncome,
        totalExpense = totalExpense,
        movements = movements,
        modifier = modifier
    )
}

@Composable
private fun HomeContent(
    balance: BigDecimal,
    totalIncome: BigDecimal,
    totalExpense: BigDecimal,
    movements: List<TransactionDto>,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item { Text("Saldo desde el Cerro", style = MaterialTheme.typography.headlineMedium) }

        item { BalanceCard(balance) }

        item {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                SummaryCard("Ingresos", totalIncome, Modifier.weight(1f))
                SummaryCard("Egresos", totalExpense, Modifier.weight(1f))
            }
        }

        item { Text("Últimos movimientos", style = MaterialTheme.typography.titleMedium) }
        items(movements.take(5)) { TransactionRow(it) }
    }
}

@Preview(showBackground = true, name = "HomeScreen")
@Composable
private fun HomeContentPreview() {
    SaldoDelCerroTheme(dynamicColor = false) {
        HomeContent(
            balance = BigDecimal("1179.25"),
            totalIncome = BigDecimal("1500.00"),
            totalExpense = BigDecimal("320.75"),
            movements = listOf(
                TransactionDto(
                    transaction = TransactionEntity(reason = "Salario", amount = BigDecimal("1500.00"), typeId = 1L),
                    type = CategoryEntity(id = 1L, name = "Salario", isIncome = true)
                ),
                TransactionDto(
                    transaction = TransactionEntity(reason = "Compra supermercado", amount = BigDecimal("120.50"), typeId = 2L),
                    type = CategoryEntity(id = 2L, name = "Comida", isIncome = false)
                ),
                TransactionDto(
                    transaction = TransactionEntity(reason = "Pasaje", amount = BigDecimal("2.25"), typeId = 3L),
                    type = CategoryEntity(id = 3L, name = "Transporte", isIncome = false)
                )
            )
        )
    }
}
