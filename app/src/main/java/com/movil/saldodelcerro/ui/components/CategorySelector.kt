package com.movil.saldodelcerro.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.movil.saldodelcerro.domain.model.Category
import com.movil.saldodelcerro.ui.theme.SaldoDelCerroTheme

@Composable
fun CategorySelector(
    types: List<Category>,
    selectedTypeId: Long,
    onTypeSelected: (Long) -> Unit,
    onAddCategoryClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = Icons.Default.LocalOffer,
                contentDescription = "Categorías",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp)
            )
            Text(
                text = "Categoría",
                style = MaterialTheme.typography.titleMedium
            )
        }

        if (types.isEmpty()) {
            Text("No hay categorías disponibles", style = MaterialTheme.typography.bodySmall)
        } else {
            types.chunked(2).forEach { rowItems ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    rowItems.forEach { type ->
                        CategoryChip(
                            type = type,
                            isSelected = selectedTypeId == type.id,
                            onClick = { onTypeSelected(type.id) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                    if (rowItems.size == 1) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }

        OutlinedButton(
            onClick = onAddCategoryClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = MaterialTheme.shapes.extraLarge
        ) {
            Icon(imageVector = Icons.Default.Add, contentDescription = "Agregar", modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Agregar nueva categoría")
        }
    }
}

@Preview(showBackground = true, name = "CategorySelector")
@Composable
private fun CategorySelectorPreview() {
    SaldoDelCerroTheme(dynamicColor = false) {
        CategorySelector(
            types = listOf(
                Category(id = 1L, name = "Comida", isIncome = false),
                Category(id = 2L, name = "Transporte", isIncome = false),
                Category(id = 3L, name = "Ocio", isIncome = false),
                Category(id = 4L, name = "Salud", isIncome = false),
                Category(id = 5L, name = "Otros", isIncome = false)
            ),
            selectedTypeId = 2L,
            onTypeSelected = {},
            onAddCategoryClick = {}
        )
    }
}
