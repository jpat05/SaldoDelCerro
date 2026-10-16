package com.movil.saldodelcerro.ui.components

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.movil.saldodelcerro.ui.theme.SaldoDelCerroTheme

@Composable
fun AddCategoryDialog(
    name: String,
    onNameChange: (String) -> Unit,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Nueva categoría") },
        text = {
            OutlinedTextField(
                value = name,
                onValueChange = onNameChange,
                label = { Text("Nombre de la categoría") },
                singleLine = true,
                shape = RoundedCornerShape(8.dp)
            )
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text("Guardar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

@Preview(showBackground = true, name = "AddCategoryDialog")
@Composable
private fun AddCategoryDialogPreview() {
    var name by remember { mutableStateOf("") }
    SaldoDelCerroTheme(dynamicColor = false) {
        AddCategoryDialog(
            name = name,
            onNameChange = { name = it },
            onConfirm = {},
            onDismiss = {}
        )
    }
}
