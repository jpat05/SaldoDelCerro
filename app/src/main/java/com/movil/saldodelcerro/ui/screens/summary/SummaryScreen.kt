package com.movil.saldodelcerro.ui.screens.summary

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.movil.saldodelcerro.domain.model.CategoryTotal
import com.movil.saldodelcerro.domain.model.Movement
import com.movil.saldodelcerro.ui.UiState
import com.movil.saldodelcerro.ui.components.CategoryDonutChart
import com.movil.saldodelcerro.ui.components.SummaryRow
import com.movil.saldodelcerro.ui.components.TransactionRow
import com.movil.saldodelcerro.ui.components.UiStateError
import com.movil.saldodelcerro.ui.components.UiStateLoading
import com.movil.saldodelcerro.ui.theme.SaldoDelCerroTheme
import java.math.BigDecimal

@Composable
fun SummaryScreen(
    viewModel: SummaryViewModel,
    onMovementClick: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    when (val state = uiState) {
        is UiState.Loading -> UiStateLoading(modifier)

        is UiState.Error -> UiStateError(message = state.message, modifier = modifier)

        is UiState.Success -> SummaryContent(
            balance = state.data.balance,
            totalIncome = state.data.totalIncome,
            totalExpense = state.data.totalExpense,
            categories = state.data.categoryTotals,
            movements = state.data.movements,
            onMovementClick = onMovementClick,
            modifier = modifier
        )
    }
}

@Composable
private fun SummaryContent(
    balance: BigDecimal,
    totalIncome: BigDecimal,
    totalExpense: BigDecimal,
    categories: List<CategoryTotal>,
    movements: List<Movement>,
    onMovementClick: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
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
                    Spacer(Modifier.height(12.dp))
                    CategoryDonutChart(categories)
                }
            }
        }

        item { Text("Movimientos", style = MaterialTheme.typography.titleMedium) }
        items(movements) { movement ->
            TransactionRow(
                movement = movement,
                onClick = { onMovementClick(movement.id) }
            )
        }
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
                Movement(reason = "Compra supermercado", amount = BigDecimal("120.50"), categoryId = 2L, categoryName = "Comida", isIncome = false)
            ),
            onMovementClick = {}
        )
    }
}
