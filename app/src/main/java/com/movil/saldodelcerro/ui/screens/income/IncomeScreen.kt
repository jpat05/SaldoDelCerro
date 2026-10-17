package com.movil.saldodelcerro.ui.screens.income

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.movil.saldodelcerro.data.models.CategoryEntity
import com.movil.saldodelcerro.ui.components.AddCategoryDialog
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
            .padding(horizontal = 20.dp, vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Icon(
            imageVector = Icons.Default.AddCircle,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.tertiary,
            modifier = Modifier.size(64.dp)
        )

        Text(
            text = "Registra tu ingreso",
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center
        )

        Text(
            text = "Ingresa el monto que recibiste.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

        OutlinedTextField(
            value = amountInput,
            onValueChange = onAmountChange,
            label = { Text("Monto") },
            placeholder = { Text("Ej. 100.00") },
            prefix = { Text("$") },
            textStyle = MaterialTheme.typography.headlineSmall,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.small
        )

        CategoryDropdown(
            categories = incomeCategories,
            selectedId = selectedTypeId,
            onSelected = onTypeSelected,
            onAddCategory = onAddCategoryClick,
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            onClick = onSubmit,
            enabled = amountInput.isNotBlank() && selectedTypeId > 0L && !isSubmitting,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = MaterialTheme.shapes.small
        ) {
            Text(
                "Guardar ingreso",
                style = MaterialTheme.typography.labelLarge
            )
        }

        Surface(
            color = MaterialTheme.colorScheme.surfaceVariant,
            shape = MaterialTheme.shapes.small,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = "Los ingresos se suman a tu saldo disponible.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        if (message != null) {
            Text(
                text = message,
                color = if (isError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.tertiary,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun CategoryDropdown(
    categories: List<CategoryEntity>,
    selectedId: Long,
    onSelected: (Long) -> Unit,
    onAddCategory: () -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    val selectedName = categories.firstOrNull { it.id == selectedId }?.name ?: "Sin categorías"

    Box(modifier) {
        OutlinedTextField(
            value = selectedName,
            onValueChange = {},
            readOnly = true,
            label = { Text("Categoría") },
            trailingIcon = {
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = "Ver categorías"
                )
            },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.small
        )

        Box(
            Modifier
                .matchParentSize()
                .clickable { expanded = true }
        )

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            categories.forEach { category ->
                DropdownMenuItem(
                    text = { Text(category.name) },
                    onClick = {
                        onSelected(category.id)
                        expanded = false
                    }
                )
            }

            HorizontalDivider()

            DropdownMenuItem(
                text = { Text("+ Nueva categoría") },
                onClick = {
                    expanded = false
                    onAddCategory()
                }
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
