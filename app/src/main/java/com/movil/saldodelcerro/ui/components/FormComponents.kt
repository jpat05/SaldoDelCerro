package com.movil.saldodelcerro.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

/**
 * Componentes de formulario reutilizables, autocontenidos: solo dependen de Compose.
 * Sirven para cualquier pantalla de registro (Egresos, Ingresos, etc.) cambiando
 * textos, iconos y color de acento.
 */

/** Categoria minima que necesita la cuadricula (mapea desde tu modelo de datos). */
data class GridCategory(val id: Long, val name: String)

/** Visual de una categoria para la cuadricula (icono + colores). */
data class CategoryChipVisual(
    val icon: ImageVector,
    val containerColor: Color,
    val accentColor: Color,
    val onAccentColor: Color
)

// ------------------------------------------------------------------
// 1. Barra superior: flecha de volver + titulo a la izquierda.
// ------------------------------------------------------------------
@Composable
fun FinGoTopBar(
    title: String,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(48.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onBack) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Volver",
                tint = MaterialTheme.colorScheme.onSurface
            )
        }
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

// ------------------------------------------------------------------
// 2. Etiqueta fija sobre cada campo del formulario.
// ------------------------------------------------------------------
@Composable
fun LabeledField(
    label: String,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 6.dp)
        )
        content()
    }
}

// ------------------------------------------------------------------
// 3. Campo desplegable (ExposedDropdownMenuBox) sincronizado con la cuadricula.
// ------------------------------------------------------------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryDropdownField(
    categories: List<GridCategory>,
    selectedTypeId: Long,
    onTypeSelected: (Long) -> Unit,
    placeholder: String = "Selecciona una categoría",
    accentTint: Color = MaterialTheme.colorScheme.primary,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    val selectedName = categories.firstOrNull { it.id == selectedTypeId }?.name

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it },
        modifier = modifier
    ) {
        OutlinedTextField(
            value = selectedName.orEmpty(),
            onValueChange = {},
            readOnly = true,
            placeholder = { Text(placeholder) },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.LocalOffer,
                    contentDescription = "Categoría",
                    tint = accentTint
                )
            },
            trailingIcon = {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowDown,
                    contentDescription = "Desplegar",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            shape = MaterialTheme.shapes.small,
            colors = OutlinedTextFieldDefaults.colors(),
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable, enabled = true)
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            categories.forEach { category ->
                DropdownMenuItem(
                    text = { Text(category.name) },
                    onClick = {
                        onTypeSelected(category.id)
                        expanded = false
                    }
                )
            }
        }
    }
}

// ------------------------------------------------------------------
// 4. Cuadricula de categorias en 3 columnas con "+" como ultimo circulo.
// ------------------------------------------------------------------
@Composable
fun CategoryGrid(
    categories: List<GridCategory>,
    selectedTypeId: Long,
    onTypeSelected: (Long) -> Unit,
    onAddCategoryClick: () -> Unit,
    visualProvider: @Composable (String) -> CategoryChipVisual,
    modifier: Modifier = Modifier
) {
    val cellHeight = 92.dp
    val spacing = 12.dp
    val totalCells = categories.size + 1
    val rows = (totalCells + 2) / 3
    val gridHeight = cellHeight * rows + spacing * (rows - 1)

    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        modifier = modifier
            .fillMaxWidth()
            .height(gridHeight),
        verticalArrangement = Arrangement.spacedBy(spacing),
        horizontalArrangement = Arrangement.spacedBy(spacing),
        userScrollEnabled = false
    ) {
        items(categories, key = { it.id }) { category ->
            CategoryGridCell(
                category = category,
                isSelected = category.id == selectedTypeId,
                visual = visualProvider(category.name),
                onClick = { onTypeSelected(category.id) },
                modifier = Modifier.height(cellHeight)
            )
        }
        item {
            AddCategoryGridCell(
                onClick = onAddCategoryClick,
                modifier = Modifier.height(cellHeight)
            )
        }
    }
}

// ------------------------------------------------------------------
// 5. Celda circular: icono centrado + nombre debajo. Seleccionada con aro y check.
// ------------------------------------------------------------------
@Composable
fun CategoryGridCell(
    category: GridCategory,
    isSelected: Boolean,
    visual: CategoryChipVisual,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(CircleShape)
                    .background(visual.containerColor)
                    .then(
                        if (isSelected) {
                            Modifier.border(2.dp, visual.accentColor, CircleShape)
                        } else {
                            Modifier
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = visual.icon,
                    contentDescription = category.name,
                    tint = visual.accentColor,
                    modifier = Modifier.size(26.dp)
                )
            }
            Text(
                text = category.name,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.fillMaxWidth()
            )
        }

        if (isSelected) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .size(18.dp)
                    .clip(CircleShape)
                    .background(visual.accentColor),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Seleccionada",
                    tint = visual.onAccentColor,
                    modifier = Modifier.size(12.dp)
                )
            }
        }
    }
}

// ------------------------------------------------------------------
// 6. Ultima celda: circulo con "+" que abre el dialogo de nueva categoria.
// ------------------------------------------------------------------
@Composable
fun AddCategoryGridCell(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceContainerHigh)
                    .border(1.dp, MaterialTheme.colorScheme.outline, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Agregar",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(26.dp)
                )
            }
            Text(
                text = "Agregar",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center,
                maxLines = 1,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

// ------------------------------------------------------------------
// 7. Campo de monto reutilizable: icono + placeholder + sufijo "$" + error.
// ------------------------------------------------------------------
@Composable
fun AmountField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    leadingIcon: ImageVector,
    accentTint: Color,
    isError: Boolean = false,
    errorMessage: String? = null,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = { Text(placeholder) },
        leadingIcon = {
            Icon(
                imageVector = leadingIcon,
                contentDescription = "Monto",
                tint = accentTint
            )
        },
        suffix = { Text("$") },
        isError = isError,
        supportingText = if (isError && errorMessage != null) {
            { Text(errorMessage) }
        } else null,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
        singleLine = true,
        shape = MaterialTheme.shapes.small,
        colors = OutlinedTextFieldDefaults.colors(),
        modifier = modifier.fillMaxWidth()
    )
}
