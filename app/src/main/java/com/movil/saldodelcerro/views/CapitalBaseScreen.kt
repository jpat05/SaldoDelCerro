package com.movil.saldodelcerro.views

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.movil.saldodelcerro.data.models.TransactionDto
import com.movil.saldodelcerro.services.WalletService
import com.movil.saldodelcerro.utils.formatMoney
import kotlinx.coroutines.launch

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
            onValueChange = { input = it.filter(Char::isDigit) },
            label = { Text("Monto inicial") },
            prefix = { Text("US$ ") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            onClick = {
                val amount = input.toBigDecimalOrNull()
                if (amount != null) {
                    scope.launch {
                        walletService.saveCapitalBase(amount)
                        input = ""
                        saveCount++
                    }
                }
            },
            enabled = input.isNotBlank(),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Guardar capital")
        }
    }
}