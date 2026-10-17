package com.movil.saldodelcerro.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.movil.saldodelcerro.data.models.CategoryEntity
import com.movil.saldodelcerro.ui.theme.SaldoDelCerroTheme

@Composable
fun CategoryChip(
    type: CategoryEntity,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val style = getCategoryStyle(type.name)
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(24.dp),
        color = if (isSelected) style.tintColor else style.backgroundColor,
        modifier = modifier.height(48.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            Icon(
                imageVector = style.icon,
                contentDescription = type.name,
                tint = if (isSelected) Color.White else style.tintColor,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = type.name,
                color = if (isSelected) Color.White else style.textColor,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                maxLines = 1
            )
        }
    }
}

@Preview(showBackground = true, name = "CategoryChip")
@Composable
private fun CategoryChipPreview() {
    SaldoDelCerroTheme(dynamicColor = false) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            CategoryChip(
                type = CategoryEntity(id = 1L, name = "Comida", isIncome = false),
                isSelected = false,
                onClick = {}
            )
            CategoryChip(
                type = CategoryEntity(id = 2L, name = "Transporte", isIncome = false),
                isSelected = true,
                onClick = {}
            )
        }
    }
}
