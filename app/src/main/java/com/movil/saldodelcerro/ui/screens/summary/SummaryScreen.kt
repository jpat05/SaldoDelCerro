package com.movil.saldodelcerro.ui.screens.summary

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.movil.saldodelcerro.data.models.CategoryEntity
import com.movil.saldodelcerro.data.models.CategoryTotal
import com.movil.saldodelcerro.data.models.TransactionDto
import com.movil.saldodelcerro.data.models.TransactionEntity
import com.movil.saldodelcerro.services.LedgerService
import com.movil.saldodelcerro.ui.components.SummaryRow
import com.movil.saldodelcerro.ui.components.TransactionRow
import com.movil.saldodelcerro.ui.theme.SaldoDelCerroTheme
import com.movil.saldodelcerro.utils.formatMoney
import java.math.BigDecimal

@Composable
fun SummaryScreen(
    ledgerService: LedgerService,
    modifier: Modifier = Modifier
) {
    val balance by ledgerService.availableBalance.collectAsState(initial = BigDecimal.ZERO)
    val totalIncome by ledgerService.totalIncome.collectAsState(initial = BigDecimal.ZERO)
    val totalExpense by ledgerService.totalExpense.collectAsState(initial = BigDecimal.ZERO)
    val categories by ledgerService.categoryTotals.collectAsState(initial = emptyList())
    val movements by ledgerService.movements.collectAsState(initial = emptyList())

    SummaryContent(
        balance = balance,
        totalIncome = totalIncome,
        totalExpense = totalExpense,
        categories = categories,
        movements = movements,
        modifier = modifier
    )
}

@Composable
private fun SummaryContent(
    balance: BigDecimal,
    totalIncome: BigDecimal,
    totalExpense: BigDecimal,
    categories: List<CategoryTotal>,
    movements: List<TransactionDto>,
    modifier: Modifier = Modifier
) {
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
                    categories.filter { it.total > BigDecimal.ZERO }.forEach { cat ->
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
        items(movements) { TransactionRow(it) }
    }
}

@Preview(showBackground = true, name = "SummaryScreen")
@Composable
private fun SummaryContentPreview() {
    SaldoDelCerroTheme(dynamicColor = false) {
        SummaryContent(
            balance = BigDecimal("1179.25"),
            totalIncome = BigDecimal("1500.00"),
            totalExpense = BigDecimal("320.75"),
            categories = listOf(
                CategoryTotal("Comida", BigDecimal("120.50")),
                CategoryTotal("Transporte", BigDecimal("40.25")),
                CategoryTotal("Salud", BigDecimal("160.00"))
            ),
            movements = listOf(
                TransactionDto(
                    transaction = TransactionEntity(reason = "Compra supermercado", amount = BigDecimal("120.50"), typeId = 2L),
                    type = CategoryEntity(id = 2L, name = "Comida", isIncome = false)
                )
            )
        )
    }
}
