package com.movil.saldodelcerro.ui.components

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
import com.movil.saldodelcerro.data.models.CategoryEntity
import com.movil.saldodelcerro.data.models.TransactionDto
import com.movil.saldodelcerro.data.models.TransactionEntity
import com.movil.saldodelcerro.ui.theme.SaldoDelCerroTheme
import com.movil.saldodelcerro.utils.formatMoney
import java.math.BigDecimal

@Composable
fun TransactionRow(dto: TransactionDto) {
    Card(Modifier.fillMaxWidth()) {
        Row(
            Modifier.fillMaxWidth().padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(dto.type.name, style = MaterialTheme.typography.titleSmall)
                Text(dto.transaction.reason, style = MaterialTheme.typography.bodySmall)
            }
            Text(
                text = (if (dto.type.isIncome) "+" else "-") + dto.transaction.amount.formatMoney(),
                style = MaterialTheme.typography.titleMedium,
                color = if (dto.type.isIncome) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.error
            )
        }
    }
}

@Preview(showBackground = true, name = "TransactionRow - Egreso")
@Composable
private fun TransactionRowExpensePreview() {
    SaldoDelCerroTheme(dynamicColor = false) {
        TransactionRow(
            dto = TransactionDto(
                transaction = TransactionEntity(
                    reason = "Compra supermercado",
                    amount = BigDecimal("45.50"),
                    typeId = 1L
                ),
                type = CategoryEntity(id = 1L, name = "Comida", isIncome = false)
            )
        )
    }
}

@Preview(showBackground = true, name = "TransactionRow - Ingreso")
@Composable
private fun TransactionRowIncomePreview() {
    SaldoDelCerroTheme(dynamicColor = false) {
        TransactionRow(
            dto = TransactionDto(
                transaction = TransactionEntity(
                    reason = "Pago de salario",
                    amount = BigDecimal("1500.00"),
                    typeId = 2L
                ),
                type = CategoryEntity(id = 2L, name = "Salario", isIncome = true)
            )
        )
    }
}
