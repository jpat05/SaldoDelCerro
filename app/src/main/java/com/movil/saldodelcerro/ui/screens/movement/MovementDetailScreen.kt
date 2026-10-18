package com.movil.saldodelcerro.ui.screens.movement

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.movil.saldodelcerro.domain.model.Movement
import com.movil.saldodelcerro.ui.UiState
import com.movil.saldodelcerro.ui.components.FinGoTopBar
import com.movil.saldodelcerro.ui.components.UiStateError
import com.movil.saldodelcerro.ui.components.UiStateLoading
import com.movil.saldodelcerro.ui.theme.SaldoDelCerroTheme
import com.movil.saldodelcerro.utils.formatoFecha
import com.movil.saldodelcerro.utils.formatMoney
import java.math.BigDecimal

/**
 * Destino de detalle: se llega desde el resumen con el id del movimiento como argumento.
 *
 * Stateful (Route) vs Stateless (Content): [MovementDetailScreen] consume el ViewModel y
 * [MovementDetailContent] recibe solo estado inmutable y lambdas.
 */
@Composable
fun MovementDetailScreen(
    viewModel: MovementDetailViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    MovementDetailContent(
        state = state,
        onBack = onBack,
        onRetry = viewModel::load,
        modifier = modifier
    )
}

@Composable
private fun MovementDetailContent(
    state: UiState<Movement>,
    onBack: () -> Unit,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    when (state) {
        is UiState.Loading -> UiStateLoading(modifier)

        // [FUNCIONALIDAD: DETALLE NO ENCONTRADO]
        // Proposito: el id puede apuntar a un movimiento que ya no existe (por ejemplo si la base
        // se reinicia). En ese caso se muestra el estado de error en vez de una pantalla vacia.
        is UiState.Error -> Column(modifier = modifier.fillMaxSize()) {
            FinGoTopBar(title = "Movimiento", onBack = onBack)
            UiStateError(message = state.message, onRetry = onRetry)
        }

        is UiState.Success -> MovementDetailBody(
            movement = state.data,
            onBack = onBack,
            modifier = modifier
        )
    }
}

@Composable
private fun MovementDetailBody(
    movement: Movement,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxSize()) {
        FinGoTopBar(title = "Movimiento", onBack = onBack)

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp),
            shape = MaterialTheme.shapes.medium
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = if (movement.isIncome) "Ingreso" else "Egreso",
                    style = MaterialTheme.typography.labelLarge,
                    color = if (movement.isIncome) {
                        MaterialTheme.colorScheme.tertiary
                    } else {
                        MaterialTheme.colorScheme.error
                    }
                )

                Text(
                    text = (if (movement.isIncome) "+" else "-") + movement.amount.formatMoney(),
                    style = MaterialTheme.typography.headlineMedium,
                    color = if (movement.isIncome) {
                        MaterialTheme.colorScheme.tertiary
                    } else {
                        MaterialTheme.colorScheme.error
                    }
                )

                DetailRow(label = "Categoría", value = movement.categoryName)
                DetailRow(label = "Descripción", value = movement.reason)
                DetailRow(label = "Fecha", value = movement.date.formatoFecha())
            }
        }
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

@Preview(showBackground = true, name = "MovementDetail - Egreso")
@Composable
private fun MovementDetailExpensePreview() {
    SaldoDelCerroTheme(dynamicColor = false) {
        MovementDetailContent(
            state = UiState.Success(
                Movement(
                    id = 1L,
                    reason = "Compra en supermercado",
                    amount = BigDecimal("20.00"),
                    date = 1_760_000_000_000L,
                    categoryId = 1L,
                    categoryName = "Alimentación",
                    isIncome = false
                )
            ),
            onBack = {},
            onRetry = {}
        )
    }
}

@Preview(showBackground = true, name = "MovementDetail - No encontrado")
@Composable
private fun MovementDetailErrorPreview() {
    SaldoDelCerroTheme(dynamicColor = false) {
        MovementDetailContent(
            state = UiState.Error("No se encontró el movimiento solicitado."),
            onBack = {},
            onRetry = {}
        )
    }
}