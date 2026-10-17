package com.movil.saldodelcerro.ui.screens.expense

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.RemoveCircle
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.movil.saldodelcerro.data.models.CategoryEntity
import com.movil.saldodelcerro.ui.components.AddCategoryDialog
import com.movil.saldodelcerro.ui.components.CategorySelector
import com.movil.saldodelcerro.ui.theme.SaldoDelCerroTheme

@Composable
fun ExpenseScreen(
    viewModel: ExpenseViewModel,
    modifier: Modifier = Modifier
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    // Estado efímero del diálogo, puramente de UI
    var showAddCategoryDialog by remember { mutableStateOf(false) }
    var newCategoryName by remember { mutableStateOf("") }

    ExpenseContent(
        expenseCategories = state.categories,
        amountInput = state.amountInput,
        reasonInput = state.reasonInput,
        selectedTypeId = state.selectedCategoryId,
        message = state.feedback?.message,
        isError = state.feedback?.isError ?: false,
        isSubmitting = state.isSubmitting,
        onAmountChange = viewModel::onAmountChange,
        onReasonChange = viewModel::onReasonChange,
        onTypeSelected = viewModel::onCategorySelected,
        onAddCategoryClick = { showAddCategoryDialog = true },
        onSubmit = viewModel::submit,
        modifier = modifier
    )

    if (showAddCategoryDialog) {
        AddCategoryDialog(
            name = newCategoryName,
            onNameChange = { newCategoryName = it },
            onConfirm = {
                if (newCategoryName.isNotBlank()) {
                    viewModel.addCategory(newCategoryName)
                    newCategoryName = ""
                    showAddCategoryDialog = false
                }
            },
            onDismiss = { showAddCategoryDialog = false }
        )
    }
}

@Composable
private fun ExpenseContent(
    expenseCategories: List<CategoryEntity>,
    amountInput: String,
    reasonInput: String,
    selectedTypeId: Long,
    message: String?,
    isError: Boolean,
    isSubmitting: Boolean,
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
            style = MaterialTheme.typography.headlineMedium
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
                    tint = MaterialTheme.colorScheme.error
                )
            },
            suffix = { Text("$") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.small
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
                    tint = MaterialTheme.colorScheme.error
                )
            },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.small
        )

        // 3. Sección de Categorías ordenadas en filas simétricas
        CategorySelector(
            types = expenseCategories,
            selectedTypeId = selectedTypeId,
            onTypeSelected = onTypeSelected,
            onAddCategoryClick = onAddCategoryClick
        )

        // 4. Botón Registrar Gasto (color de marca: primary)
        Button(
            onClick = onSubmit,
            enabled = amountInput.isNotBlank() && selectedTypeId > 0L && !isSubmitting,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = MaterialTheme.shapes.small
        ) {
            Text(
                "Registrar gasto",
                style = MaterialTheme.typography.labelLarge
            )
        }

        if (message != null) {
            Text(
                message,
                color = if (isError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.tertiary,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Preview(showBackground = true, name = "ExpenseScreen")
@Composable
private fun ExpenseContentPreview() {
    SaldoDelCerroTheme(dynamicColor = false) {
        ExpenseContent(
            expenseCategories = listOf(
                CategoryEntity(id = 1L, name = "Comida", isIncome = false),
                CategoryEntity(id = 2L, name = "Transporte", isIncome = false),
                CategoryEntity(id = 3L, name = "Ocio", isIncome = false)
            ),
            amountInput = "20.50",
            reasonInput = "Compra en supermercado",
            selectedTypeId = 1L,
            message = "¡Gasto registrado con éxito!",
            isError = false,
            isSubmitting = false,
            onAmountChange = {},
            onReasonChange = {},
            onTypeSelected = {},
            onAddCategoryClick = {},
            onSubmit = {}
        )
    }
}
