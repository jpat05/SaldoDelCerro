package com.movil.saldodelcerro.ui.screens.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.movil.saldodelcerro.ui.UiState
import com.movil.saldodelcerro.ui.components.BalanceCard
import com.movil.saldodelcerro.ui.components.SummaryCard
import com.movil.saldodelcerro.ui.components.UiStateError
import com.movil.saldodelcerro.ui.components.UiStateLoading
import com.movil.saldodelcerro.ui.theme.SaldoDelCerroTheme
import java.math.BigDecimal

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    when (val state = uiState) {
        is UiState.Loading -> UiStateLoading(modifier)

        is UiState.Error -> UiStateError(message = state.message, modifier = modifier)

        is UiState.Success -> HomeContent(
            balance = state.data.balance,
            totalIncome = state.data.totalIncome,
            totalExpense = state.data.totalExpense,
            modifier = modifier
        )
    }
}

@Composable
private fun HomeContent(
    balance: BigDecimal,
    totalIncome: BigDecimal,
    totalExpense: BigDecimal,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item { Text("FinGO", style = MaterialTheme.typography.headlineMedium) }

        item { BalanceCard(balance) }

        item {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                SummaryCard("Ingresos", totalIncome, true, Modifier.weight(1f))
                SummaryCard("Egresos", totalExpense, false, Modifier.weight(1f))
            }
        }

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
        )
    }
}
