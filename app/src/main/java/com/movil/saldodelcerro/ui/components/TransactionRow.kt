package com.movil.saldodelcerro.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import com.movil.saldodelcerro.domain.model.Movement
import com.movil.saldodelcerro.ui.theme.SaldoDelCerroTheme
import com.movil.saldodelcerro.utils.formatMoney
import java.math.BigDecimal

@Composable
fun TransactionRow(
    movement: Movement,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Row(
            Modifier.fillMaxWidth().padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(movement.categoryName, style = MaterialTheme.typography.titleSmall)
                Text(movement.reason, style = MaterialTheme.typography.bodySmall)
            }
            Text(
                text = (if (movement.isIncome) "+" else "-") + movement.amount.formatMoney(),
                style = MaterialTheme.typography.titleMedium,
                color = if (movement.isIncome) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.error
            )
        }
    }
}

@Preview(showBackground = true, name = "TransactionRow - Egreso")
@Composable
private fun TransactionRowExpensePreview() {
    SaldoDelCerroTheme(dynamicColor = false) {
        TransactionRow(
            movement = Movement(
                reason = "Compra supermercado",
                amount = BigDecimal("45.50"),
                categoryId = 1L,
                categoryName = "Comida",
                isIncome = false
            ),
            onClick = {}
        )
    }
}

@Preview(showBackground = true, name = "TransactionRow - Ingreso")
@Composable
private fun TransactionRowIncomePreview() {
    SaldoDelCerroTheme(dynamicColor = false) {
        TransactionRow(
            movement = Movement(
                reason = "Pago de salario",
                amount = BigDecimal("1500.00"),
                categoryId = 2L,
                categoryName = "Salario",
                isIncome = true
            ),
            onClick = {}
        )
    }
}
