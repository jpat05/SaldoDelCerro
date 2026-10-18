package com.movil.saldodelcerro.ui.screens.expense

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.RemoveCircle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.movil.saldodelcerro.data.models.CategoryEntity
import com.movil.saldodelcerro.ui.UiFeedback
import com.movil.saldodelcerro.ui.components.AddCategoryDialog
import com.movil.saldodelcerro.ui.components.AmountField
import com.movil.saldodelcerro.ui.components.CategoryDropdownField
import com.movil.saldodelcerro.ui.components.CategoryGrid
import com.movil.saldodelcerro.ui.components.CategoryChipVisual
import com.movil.saldodelcerro.ui.components.FinGoTopBar
import com.movil.saldodelcerro.ui.components.GridCategory
import com.movil.saldodelcerro.ui.components.LabeledField
import com.movil.saldodelcerro.ui.components.getCategoryStyle
import com.movil.saldodelcerro.ui.theme.SaldoDelCerroTheme
import com.movil.saldodelcerro.utils.toMoneyOrNull

@Composable
fun ExpenseScreen(
    viewModel: ExpenseViewModel,
    onBack: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    // Estado efimero del dialogo, puramente de UI
    var showAddCategoryDialog by remember { mutableStateOf(false) }
    var newCategoryName by remember { mutableStateOf("") }

    ExpenseContent(
        expenseCategories = state.categories,
        amountInput = state.amountInput,
        reasonInput = state.reasonInput,
        selectedTypeId = state.selectedCategoryId,
        isSubmitting = state.isSubmitting,
        onAmountChange = viewModel::onAmountChange,
        onReasonChange = viewModel::onReasonChange,
        onTypeSelected = viewModel::onCategorySelected,
        onAddCategoryClick = { showAddCategoryDialog = true },
        onSubmit = viewModel::submit,
        onFeedbackConsumed = viewModel::dismissFeedback,
        feedback = state.feedback,
        onBack = onBack,
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
    isSubmitting: Boolean,
    onAmountChange: (String) -> Unit,
    onReasonChange: (String) -> Unit,
    onTypeSelected: (Long) -> Unit,
    onAddCategoryClick: () -> Unit,
    onSubmit: () -> Unit,
    onFeedbackConsumed: () -> Unit,
    feedback: UiFeedback?,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    val snackbarHostState = remember { SnackbarHostState() }
    val gridCategories = expenseCategories.map { GridCategory(it.id, it.name) }

    // [FUNCIONALIDAD: REGISTRO DE EGRESO]
    // Proposito: Al confirmar un registro (feedback sin error) se muestra un Snackbar de
    // confirmacion y se limpia el estado. El error de validacion del monto se muestra bajo el
    // campo via supportingText, no por Snackbar.
    LaunchedEffect(feedback) {
        val fb = feedback ?: return@LaunchedEffect
        snackbarHostState.showSnackbar(fb.message)
        onFeedbackConsumed()
    }

    // Habilita el boton solo con monto valido (> 0) y categoria seleccionada.
    val amountError = amountInput.isNotBlank() && amountInput.toMoneyOrNull() == null
    val canSubmit = amountInput.toMoneyOrNull() != null &&
        selectedTypeId > 0L &&
        !isSubmitting

    Box(
        modifier = modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 20.dp)
                .padding(top = 8.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // [FUNCIONALIDAD: BARRA SUPERIOR]
            // Proposito: Flecha de volver a la izquierda + titulo "Registrar gasto" al lado
            // (no centrado), en tamanio mediano y peso semibold.
            FinGoTopBar(
                title = "Registrar gasto",
                onBack = onBack
            )

            // 1. Campo Monto (acento rojo = egreso)
            LabeledField(label = "Monto") {
                AmountField(
                    value = amountInput,
                    onValueChange = onAmountChange,
                    placeholder = "Ej. 20.00",
                    leadingIcon = Icons.Default.RemoveCircle,
                    accentTint = MaterialTheme.colorScheme.error,
                    isError = amountError,
                    errorMessage = "El monto debe ser mayor que cero."
                )
            }

            // 2. Campo Categoria (desplegable sincronizado con la cuadricula)
            LabeledField(label = "Categoría") {
                CategoryDropdownField(
                    categories = gridCategories,
                    selectedTypeId = selectedTypeId,
                    onTypeSelected = onTypeSelected,
                    accentTint = MaterialTheme.colorScheme.error
                )
            }

            // 3. Campo Descripcion
            LabeledField(label = "Descripción (opcional)") {
                OutlinedTextField(
                    value = reasonInput,
                    onValueChange = onReasonChange,
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
                    colors = OutlinedTextFieldDefaults.colors(),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // 4. Boton Registrar Gasto
            Button(
                onClick = onSubmit,
                enabled = canSubmit,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = MaterialTheme.shapes.medium,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text(
                    text = "Registrar gasto",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }

            // 5. Divisor + seccion Categorias
            HorizontalDivider(color = MaterialTheme.colorScheme.outline)
            Text(
                text = "Categorías",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )

            // 6. Cuadricula de categorias en 3 columnas
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
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp)
        )
    }
}
