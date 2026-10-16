package com.movil.saldodelcerro.ui.screens.capital

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.movil.saldodelcerro.data.models.TransactionDto
import com.movil.saldodelcerro.data.models.TransactionEntity
import com.movil.saldodelcerro.data.models.TransactionTypeEntity
import com.movil.saldodelcerro.services.WalletService
import com.movil.saldodelcerro.ui.theme.SaldoDelCerroTheme
import com.movil.saldodelcerro.utils.formatMoney
import kotlinx.coroutines.launch
import java.math.BigDecimal

@Composable
fun CapitalBaseScreen(
    walletService: WalletService,
    modifier: Modifier = Modifier
) {
    var saveCount by remember { mutableIntStateOf(0) }
    var capital by remember { mutableStateOf<TransactionDto?>(null) }
    var input by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()

    LaunchedEffect(saveCount) { capital = walletService.getCapitalBase() }

    CapitalBaseContent(
        capital = capital,
        input = input,
        onInputChange = { input = it.filter(Char::isDigit) },
        onSave = {
            val amount = input.toBigDecimalOrNull()
            if (amount != null) {
                scope.launch {
                    walletService.saveCapitalBase(amount)
                    input = ""
                    saveCount++
                }
            }
        },
        modifier = modifier
    )
}

@Composable
private fun CapitalBaseContent(
    capital: TransactionDto?,
    input: String,
    onInputChange: (String) -> Unit,
    onSave: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Capital base", style = MaterialTheme.typography.headlineMedium)

        Card(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(16.dp)) {
                Text("Capital actual", style = MaterialTheme.typography.labelLarge)
                Text(
                    capital?.transaction?.amount?.formatMoney() ?: "Agrega un monto",
                    style = MaterialTheme.typography.headlineSmall
                )
            }
        }

        OutlinedTextField(
            value = input,
            onValueChange = onInputChange,
            label = { Text("Monto inicial") },
            prefix = { Text("US$ ") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            onClick = onSave,
            enabled = input.isNotBlank(),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Guardar capital")
        }
    }
}

@Preview(showBackground = true, name = "CapitalBaseScreen")
@Composable
private fun CapitalBaseContentPreview() {
    SaldoDelCerroTheme(dynamicColor = false) {
        CapitalBaseContent(
            capital = TransactionDto(
                transaction = TransactionEntity(reason = "Capital base", amount = BigDecimal("1500.00"), typeId = 1L),
                type = TransactionTypeEntity(id = 1L, name = "Capital base", isIncome = true)
            ),
            input = "500",
            onInputChange = {},
            onSave = {}
        )
    }
}
