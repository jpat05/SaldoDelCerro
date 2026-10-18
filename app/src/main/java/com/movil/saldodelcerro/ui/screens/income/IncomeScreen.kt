package com.movil.saldodelcerro.ui.screens.income

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
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.movil.saldodelcerro.domain.model.Category
import com.movil.saldodelcerro.ui.UiEvent
import com.movil.saldodelcerro.ui.UiState
import com.movil.saldodelcerro.ui.components.AddCategoryDialog
import com.movil.saldodelcerro.ui.components.CategoryChipVisual
import com.movil.saldodelcerro.ui.components.CategoryGrid
import com.movil.saldodelcerro.ui.components.GridCategory
import com.movil.saldodelcerro.ui.components.UiStateError
import com.movil.saldodelcerro.ui.components.UiStateLoading
import com.movil.saldodelcerro.ui.components.getCategoryStyle
import com.movil.saldodelcerro.ui.theme.SaldoDelCerroTheme

@Composable
fun IncomeScreen(
    viewModel: IncomeViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    // Estado efímero del diálogo, puramente de UI
    var showAddCategoryDialog by remember { mutableStateOf(false) }
    var newCategoryName by remember { mutableStateOf("") }

    // [FUNCIONALIDAD: EVENTOS PUNTUALES]
    // Proposito: los mensajes de exito/rechazo llegan por el Channel del ViewModel y se muestran
    // una sola vez como Snackbar. Al no vivir en el estado, un cambio de configuracion no los repite.
    LaunchedEffect(viewModel) {
        viewModel.events.collect { event ->
            when (event) {
                is UiEvent.ShowMessage -> snackbarHostState.showSnackbar(event.message)
            }
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        when (val state = uiState) {
            is UiState.Loading -> UiStateLoading()

            is UiState.Error -> UiStateError(message = state.message)

            is UiState.Success -> IncomeContent(
                incomeCategories = state.data.categories,
                amountInput = state.data.amountInput,
                selectedTypeId = state.data.selectedCategoryId,
                isSubmitting = state.data.isSubmitting,
                onAmountChange = viewModel::onAmountChange,
                onTypeSelected = viewModel::onCategorySelected,
                onAddCategoryClick = { showAddCategoryDialog = true },
                onSubmit = viewModel::submit
            )
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp)
        )
    }

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
    incomeCategories: List<Category>,
    amountInput: String,
    selectedTypeId: Long,
    isSubmitting: Boolean,
    onAmountChange: (String) -> Unit,
    onTypeSelected: (Long) -> Unit,
    onAddCategoryClick: () -> Unit,
    onSubmit: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    val gridCategories = incomeCategories.map { GridCategory(it.id, it.name) }

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

        // Selector unico de categoria: cuadricula de 3 columnas con "+" como ultimo circulo.
        CategoryGrid(
            categories = gridCategories,
            selectedTypeId = selectedTypeId,
            onTypeSelected = onTypeSelected,
            onAddCategoryClick = onAddCategoryClick,
            visualProvider = { name ->
                getCategoryStyle(name).let { style ->
                    CategoryChipVisual(
                        icon = style.icon,
                        containerColor = style.backgroundColor,
                        accentColor = style.tintColor,
                        onAccentColor = style.onTintColor
                    )
                }
            }
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
    }
}

@Preview(showBackground = true, name = "IncomeScreen")
@Composable
private fun IncomeContentPreview() {
    SaldoDelCerroTheme(dynamicColor = false) {
        IncomeContent(
            incomeCategories = listOf(
                Category(id = 1L, name = "Salario", isIncome = true),
                Category(id = 2L, name = "Freelance", isIncome = true),
                Category(id = 3L, name = "Otros ingresos", isIncome = true)
            ),
            amountInput = "1500",
            selectedTypeId = 1L,
            isSubmitting = false,
            onAmountChange = {},
            onTypeSelected = {},
            onAddCategoryClick = {},
            onSubmit = {}
        )
    }
}

@Preview(showBackground = true, name = "IncomeScreen - Diálogo")
@Composable
private fun IncomeDialogPreview() {
    var name by remember { mutableStateOf("Mascotas") }
    SaldoDelCerroTheme(dynamicColor = false) {
        AddCategoryDialog(
            name = name,
            onNameChange = { name = it },
            onConfirm = {},
            onDismiss = {}
        )
    }
}