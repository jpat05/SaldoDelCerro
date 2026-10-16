package com.movil.saldodelcerro.ui.screens.expense

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.RemoveCircle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.movil.saldodelcerro.data.models.TransactionEntity
import com.movil.saldodelcerro.data.models.TransactionTypeEntity
import com.movil.saldodelcerro.services.WalletService
import com.movil.saldodelcerro.ui.components.AddCategoryDialog
import com.movil.saldodelcerro.ui.components.CategorySelector
import com.movil.saldodelcerro.ui.theme.SaldoDelCerroTheme
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
    var mensaje by remember { mutableStateOf("") }

    // Control para el diálogo emergente de nueva categoría
    var showAddCategoryDialog by remember { mutableStateOf(false) }
    var newCategoryName by remember { mutableStateOf("") }

    val scope = rememberCoroutineScope()

    LaunchedEffect(expenseTypes) {
        if (selectedTypeId == 0L && expenseTypes.isNotEmpty()) {
            selectedTypeId = expenseTypes.first().id
        }
    }

    ExpenseContent(
        expenseTypes = expenseTypes,
        amountInput = amountInput,
        reasonInput = reasonInput,
        selectedTypeId = selectedTypeId,
        message = mensaje,
        onAmountChange = { amountInput = it.filter(Char::isDigit) },
        onReasonChange = { reasonInput = it },
        onTypeSelected = { selectedTypeId = it },
        onAddCategoryClick = { showAddCategoryDialog = true },
        onSubmit = {
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
                    mensaje = "¡Gasto registrado con éxito!"
                    amountInput = ""
                    reasonInput = ""
                }
            }
        },
        modifier = modifier
    )

    // Diálogo para ingresar nueva categoría usando `walletService.insertType`
    if (showAddCategoryDialog) {
        AddCategoryDialog(
            name = newCategoryName,
            onNameChange = { newCategoryName = it },
            onConfirm = {
                if (newCategoryName.isNotBlank()) {
                    scope.launch {
                        walletService.insertType(
                            TransactionTypeEntity(
                                name = newCategoryName.trim(),
                                isIncome = false
                            )
                        )
                        newCategoryName = ""
                        showAddCategoryDialog = false
                    }
                }
            },
            onDismiss = { showAddCategoryDialog = false }
        )
    }
}

@Composable
private fun ExpenseContent(
    expenseTypes: List<TransactionTypeEntity>,
    amountInput: String,
    reasonInput: String,
    selectedTypeId: Long,
    message: String,
    onAmountChange: (String) -> Unit,
    onReasonChange: (String) -> Unit,
    onTypeSelected: (Long) -> Unit,
    onAddCategoryClick: () -> Unit,
    onSubmit: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Registrar gasto",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        // 1. Campo Monto con ícono
        OutlinedTextField(
            value = amountInput,
            onValueChange = onAmountChange,
            label = { Text("Monto") },
            placeholder = { Text("Ej. 20.00") },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.RemoveCircle,
                    contentDescription = "Monto",
                    tint = Color(0xFFE57373)
                )
            },
            suffix = { Text("$") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        )

        // 2. Campo Descripción con ícono
        OutlinedTextField(
            value = reasonInput,
            onValueChange = onReasonChange,
            label = { Text("Descripción (opcional)") },
            placeholder = { Text("Ej. Compra en supermercado") },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Description,
                    contentDescription = "Descripción",
                    tint = Color(0xFFE57373)
                )
            },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        )

        // 3. Sección de Categorías ordenadas en filas simétricas
        CategorySelector(
            types = expenseTypes,
            selectedTypeId = selectedTypeId,
            onTypeSelected = onTypeSelected,
            onAddCategoryClick = onAddCategoryClick
        )

        // 4. Botón Registrar Gasto
        Button(
            onClick = onSubmit,
            enabled = amountInput.isNotBlank() && selectedTypeId > 0L,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFE67E53)
            )
        ) {
            Text(
                "Registrar gasto",
                color = Color.White,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold
            )
        }

        if (message.isNotEmpty()) {
            Text(
                message,
                color = Color(0xFF4CAF50),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Preview(showBackground = true, name = "ExpenseScreen")
@Composable
private fun ExpenseContentPreview() {
    SaldoDelCerroTheme(dynamicColor = false) {
        ExpenseContent(
            expenseTypes = listOf(
                TransactionTypeEntity(id = 1L, name = "Comida", isIncome = false),
                TransactionTypeEntity(id = 2L, name = "Transporte", isIncome = false),
                TransactionTypeEntity(id = 3L, name = "Ocio", isIncome = false)
            ),
            amountInput = "20",
            reasonInput = "Compra en supermercado",
            selectedTypeId = 1L,
            message = "¡Gasto registrado con éxito!",
            onAmountChange = {},
            onReasonChange = {},
            onTypeSelected = {},
            onAddCategoryClick = {},
            onSubmit = {}
        )
    }
}
