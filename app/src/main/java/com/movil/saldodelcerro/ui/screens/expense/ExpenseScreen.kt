package com.movil.saldodelcerro.ui.screens.expense

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
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.RemoveCircle
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
import com.movil.saldodelcerro.utils.toMoneyOrNull

@Composable
fun ExpenseScreen(
    viewModel: ExpenseViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    // Estado efimero del dialogo, puramente de UI
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

            is UiState.Success -> ExpenseContent(
                expenseCategories = state.data.categories,
                amountInput = state.data.amountInput,
                reasonInput = state.data.reasonInput,
                selectedTypeId = state.data.selectedCategoryId,
                isSubmitting = state.data.isSubmitting,
                onAmountChange = viewModel::onAmountChange,
                onReasonChange = viewModel::onReasonChange,
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
private fun ExpenseContent(
    expenseCategories: List<Category>,
    amountInput: String,
    reasonInput: String,
    selectedTypeId: Long,
    isSubmitting: Boolean,
    onAmountChange: (String) -> Unit,
    onReasonChange: (String) -> Unit,
    onTypeSelected: (Long) -> Unit,
    onAddCategoryClick: () -> Unit,
    onSubmit: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    val gridCategories = expenseCategories.map { GridCategory(it.id, it.name) }

    // Habilita el boton solo con monto valido (> 0) y categoria seleccionada.
    val amountError = amountInput.isNotBlank() && amountInput.toMoneyOrNull() == null
    val canSubmit = amountInput.toMoneyOrNull() != null &&
        selectedTypeId > 0L &&
        !isSubmitting

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 20.dp, vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // [FUNCIONALIDAD: ENCABEZADO DEL FORMULARIO]
        // Proposito: Icono de egreso (acento rojo) + titulo y bajada centrados, mismo
        // lenguaje visual que la pantalla de ingresos.
        Icon(
            imageVector = Icons.Default.RemoveCircle,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.error,
            modifier = Modifier.size(64.dp)
        )

        Text(
            text = "Registra tu gasto",
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center
        )

        Text(
            text = "Ingresa el monto que gastaste.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

        // 1. Campo Monto (acento rojo = egreso)
        OutlinedTextField(
            value = amountInput,
            onValueChange = onAmountChange,
            label = { Text("Monto") },
            placeholder = { Text("Ej. 20.00") },
            prefix = { Text("$") },
            textStyle = MaterialTheme.typography.headlineSmall,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            singleLine = true,
            isError = amountError,
            supportingText = if (amountError) {
                { Text("El monto debe ser mayor que cero.") }
            } else {
                null
            },
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.small
        )

        // 2. Campo Descripcion
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
            shape = MaterialTheme.shapes.small,
            modifier = Modifier.fillMaxWidth()
        )

        // 3. Cuadricula de categorias en 3 columnas (selector unico de la pantalla)
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

        // 4. Boton Registrar Gasto
        Button(
            onClick = onSubmit,
            enabled = canSubmit,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = MaterialTheme.shapes.small
        ) {
            Text(
                text = "Registrar gasto",
                style = MaterialTheme.typography.labelLarge
            )
        }

        // 5. Nota informativa del efecto del egreso sobre el saldo
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
                    text = "Los egresos se descuentan de tu saldo disponible.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Preview(showBackground = true, name = "ExpenseScreen")
@Composable
private fun ExpenseContentPreview() {
    SaldoDelCerroTheme(dynamicColor = false) {
        ExpenseContent(
            expenseCategories = listOf(
                Category(id = 1L, name = "Alimentación", isIncome = false),
                Category(id = 2L, name = "Transporte", isIncome = false),
                Category(id = 3L, name = "Vivienda", isIncome = false),
                Category(id = 4L, name = "Salud", isIncome = false),
                Category(id = 5L, name = "Entretenimiento", isIncome = false),
                Category(id = 6L, name = "Educación", isIncome = false),
                Category(id = 7L, name = "Otros gastos", isIncome = false)
            ),
            amountInput = "20.00",
            reasonInput = "Compra en supermercado",
            selectedTypeId = 2L,
            isSubmitting = false,
            onAmountChange = {},
            onReasonChange = {},
            onTypeSelected = {},
            onAddCategoryClick = {},
            onSubmit = {}
        )
    }
}