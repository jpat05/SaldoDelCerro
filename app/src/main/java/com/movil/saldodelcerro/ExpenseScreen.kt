package com.movil.saldodelcerro.views

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.movil.saldodelcerro.data.models.TransactionEntity
import com.movil.saldodelcerro.services.WalletService
import kotlinx.coroutines.launch

@Composable
fun ExpenseScreen(
    walletService: WalletService,
    modifier: Modifier = Modifier
) {
    val allTypes by walletService.allTypes.collectAsState(initial = emptyList())
    val expenseTypes = remember(allTypes) { allTypes.filter { !it.isIncome } }

    var amountInput by remember { mutableStateOf("") }
    var reasonInput by remember { mutableStateOf("") }
    var selectedTypeId by remember { mutableLongStateOf(0L) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(expenseTypes) {
        if (selectedTypeId == 0L && expenseTypes.isNotEmpty()) {
            selectedTypeId = expenseTypes.first().id
        }
    }

    Column(
        modifier = modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Registrar egreso", style = MaterialTheme.typography.headlineMedium)

        OutlinedTextField(
            value = amountInput,
            onValueChange = { amountInput = it.filter(Char::isDigit) },
            label = { Text("Monto") },
            prefix = { Text("$/ ") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        CategoryDropdown(
            categories = expenseTypes,
            selectedTypeId = selectedTypeId,
            onSelect = { selectedTypeId = it },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = reasonInput,
            onValueChange = { reasonInput = it },
            label = { Text("Descripción") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            onClick = {
                val amount = amountInput.toBigDecimalOrNull()
                if (amount != null && selectedTypeId > 0L) {
                    scope.launch {
                        walletService.insertTransaction(
                            TransactionEntity(
                                reason = reasonInput.ifBlank { "Egreso" },
                                amount = amount,
                                typeId = selectedTypeId
                            )
                        )
                    }
                    amountInput = ""
                    reasonInput = ""
                }
            },
            enabled = amountInput.isNotBlank() && selectedTypeId > 0L,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Registrar egreso")
        }
    }
}

@Composable
private fun CategoryDropdown(
    categories: List<com.movil.saldodelcerro.data.models.TransactionTypeEntity>,
    selectedTypeId: Long,
    onSelect: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    val selected = categories.firstOrNull { it.id == selectedTypeId } ?: categories.firstOrNull()

    Box(modifier) {
        OutlinedButton(
            onClick = { expanded = true },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(selected?.name ?: "Sin categorías")
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            categories.forEach { type ->
                DropdownMenuItem(
                    text = { Text(type.name) },
                    onClick = {
                        onSelect(type.id)
                        expanded = false
                    }
                )
            }
        }
    }
}