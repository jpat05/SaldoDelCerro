package com.movil.saldodelcerro.views

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.RemoveCircle
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.movil.saldodelcerro.data.models.TransactionEntity
import com.movil.saldodelcerro.data.models.TransactionTypeEntity
import com.movil.saldodelcerro.services.WalletService
import com.movil.saldodelcerro.utils.getCategoryStyle
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

    // Control para eliminar categoría (mantener presionado)
    var categoryToDelete by remember { mutableStateOf<TransactionTypeEntity?>(null) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()
    val scrollState = rememberScrollState()

    LaunchedEffect(expenseTypes) {
        if (selectedTypeId == 0L && expenseTypes.isNotEmpty()) {
            selectedTypeId = expenseTypes.first().id
        }
    }

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
            onValueChange = { amountInput = it.filter(Char::isDigit) },
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
            onValueChange = { reasonInput = it },
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

        // 3. Sección de Categorías con opciones arriba e iconos abajo
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.LocalOffer,
                    contentDescription = "Categorías",
                    tint = Color(0xFFE57373),
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = "Categoría",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }

            if (expenseTypes.isEmpty()) {
                Text("No hay categorías disponibles", style = MaterialTheme.typography.bodySmall)
            } else {
                val chunks = expenseTypes.chunked(2)
                chunks.forEach { rowItems ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        rowItems.forEach { type ->
                            val style = getCategoryStyle(type.name)
                            val isSelected = selectedTypeId == type.id

                            Surface(
                                shape = RoundedCornerShape(16.dp),
                                color = if (isSelected) style.tintColor else style.backgroundColor,
                                modifier = Modifier
                                    .weight(1f)
                                    .height(72.dp)
                                    .pointerInput(type) {
                                        detectTapGestures(
                                            onTap = { selectedTypeId = type.id },
                                            onLongPress = {
                                                categoryToDelete = type
                                                showDeleteDialog = true
                                            }
                                        )
                                    }
                            ) {
                                // Distribución: Opción (Texto) arriba e Icono abajo
                                Column(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(8.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Text(
                                        text = type.name,
                                        color = if (isSelected) Color.White else style.textColor,
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Bold,
                                        maxLines = 1
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Icon(
                                        imageVector = style.icon,
                                        contentDescription = type.name,
                                        tint = if (isSelected) Color.White else style.tintColor,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        }
                        if (rowItems.size == 1) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }

            // Botón para agregar una nueva categoría
            OutlinedButton(
                onClick = { showAddCategoryDialog = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(24.dp)
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Agregar", modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Agregar nueva categoría")
            }
        }

        // 4. Botón Registrar Gasto
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
                        mensaje = "¡Gasto registrado con éxito!"
                        amountInput = ""
                        reasonInput = ""
                    }
                }
            },
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

        if (mensaje.isNotEmpty()) {
            Text(
                mensaje,
                color = Color(0xFF4CAF50),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )
        }
    }

    // Diálogo para ingresar nueva categoría
    if (showAddCategoryDialog) {
        AlertDialog(
            onDismissRequest = { showAddCategoryDialog = false },
            title = { Text("Nueva categoría") },
            text = {
                OutlinedTextField(
                    value = newCategoryName,
                    onValueChange = { newCategoryName = it },
                    label = { Text("Nombre de la categoría") },
                    singleLine = true,
                    shape = RoundedCornerShape(8.dp)
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
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
                    }
                ) {
                    Text("Guardar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddCategoryDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }

    // Diálogo para confirmar la eliminación de una categoría
    if (showDeleteDialog && categoryToDelete != null) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Eliminar categoría") },
            text = { Text("¿Estás seguro de que deseas eliminar la categoría \"${categoryToDelete?.name}\"?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        scope.launch {
                            categoryToDelete?.let { type ->
                                walletService.deleteType(type)
                                if (selectedTypeId == type.id) {
                                    selectedTypeId = expenseTypes.firstOrNull { it.id != type.id }?.id ?: 0L
                                }
                            }
                            showDeleteDialog = false
                            categoryToDelete = null
                        }
                    }
                ) {
                    Text("Eliminar", color = Color(0xFFE57373))
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}