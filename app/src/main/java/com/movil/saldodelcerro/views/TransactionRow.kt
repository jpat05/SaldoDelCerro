package com.movil.saldodelcerro.views

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.movil.saldodelcerro.data.models.TransactionDto
import com.movil.saldodelcerro.utils.formatMoney

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
                color = if (dto.type.isIncome) Color(0xFF2E7D32) else Color(0xFFC62828)
            )
        }
    }
}