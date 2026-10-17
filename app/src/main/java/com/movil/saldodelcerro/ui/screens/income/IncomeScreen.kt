package com.movil.saldodelcerro.ui.screens.income

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
import androidx.compose.material.icons.filled.AddCircle
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
fun IncomeScreen(
    viewModel: IncomeViewModel,
    modifier: Modifier = Modifier
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    // Estado efímero del diálogo, puramente de UI
    var showAddCategoryDialog by remember { mutableStateOf(false) }
    var newCategoryName by remember { mutableStateOf("") }

    IncomeContent(
        incomeCategories = state.categories,
        amountInput = state.amountInput,
        selectedTypeId = state.selectedCategoryId,
        message = state.feedback?.message,
        isError = state.feedback?.isError ?: false,
        isSubmitting = state.isSubmitting,
        onAmountChange = viewModel::onAmountChange,
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
private fun IncomeContent(
    incomeCategories: List<CategoryEntity>,
    amountInput: String,
    selectedTypeId: Long,
    message: String?,
    isError: Boolean,
    isSubmitting: Boolean,
    onAmountChange: (String) -> Unit,
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
            text = "Registrar ingreso",
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
                    imageVector = Icons.Default.AddCircle,
                    contentDescription = "Monto",
                    tint = MaterialTheme.colorScheme.tertiary
                )
            },
            suffix = { Text("$") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.small
        )

        // 2. Sección de Categorías de ingreso
        CategorySelector(
            types = incomeCategories,
            selectedTypeId = selectedTypeId,
            onTypeSelected = onTypeSelected,
            onAddCategoryClick = onAddCategoryClick
        )

        // 3. Botón Registrar Ingreso (color de marca: primary)
        Button(
            onClick = onSubmit,
            enabled = amountInput.isNotBlank() && selectedTypeId > 0L && !isSubmitting,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = MaterialTheme.shapes.small
        ) {
            Text(
                "Registrar ingreso",
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

@Preview(showBackground = true, name = "IncomeScreen")
@Composable
private fun IncomeContentPreview() {
    SaldoDelCerroTheme(dynamicColor = false) {
        IncomeContent(
            incomeCategories = listOf(
                CategoryEntity(id = 1L, name = "Salario", isIncome = true),
                CategoryEntity(id = 2L, name = "Freelance", isIncome = true),
                CategoryEntity(id = 3L, name = "Otros ingresos", isIncome = true)
            ),
            amountInput = "1500",
            selectedTypeId = 1L,
            message = "¡Ingreso registrado con éxito!",
            isError = false,
            isSubmitting = false,
            onAmountChange = {},
            onTypeSelected = {},
            onAddCategoryClick = {},
            onSubmit = {}
        )
    }
}
