package com.movil.saldodelcerro.views

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.movil.saldodelcerro.services.WalletService
import com.movil.saldodelcerro.ui.theme.FinGoBlue
import com.movil.saldodelcerro.ui.theme.FinGoMuted

private enum class FinGoTab(val label: String, val icon: ImageVector) {
    Home("Inicio", Icons.Filled.Home),
    Capital("Capital", Icons.Filled.Savings),
    Expenses("Egresos", Icons.Filled.ReceiptLong),
    // La cuarta pestana sigue siendo Resumen; cuando exista la pantalla de
    // Perfil se agrega como quinta.
    Summary("Resumen", Icons.Filled.PieChart)
}

@Composable
fun FinGoApp(
    walletService: WalletService,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableIntStateOf(0) }

    Scaffold(
        modifier = modifier,
        bottomBar = {
            FinGoBottomBar(
                selectedTab = selectedTab,
                onTabSelected = { selectedTab = it }
            )
        }
    ) { innerPadding ->
        val contentModifier = Modifier.padding(innerPadding)
        when (FinGoTab.entries[selectedTab]) {
            FinGoTab.Home -> HomeScreen(
                walletService = walletService,
                onSeeTransactions = { selectedTab = FinGoTab.Summary.ordinal },
                modifier = contentModifier
            )
            FinGoTab.Capital -> CapitalBaseScreen(walletService, contentModifier)
            FinGoTab.Expenses -> ExpenseScreen(walletService, contentModifier)
            FinGoTab.Summary -> SummaryScreen(walletService, contentModifier)
        }
    }
}

/** Barra de navegacion inferior: divisor superior, pestana activa en azul FinGO. */
@Composable
private fun FinGoBottomBar(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit
) {
    Column {
        HorizontalDivider(color = MaterialTheme.colorScheme.outline)
        NavigationBar(
            containerColor = MaterialTheme.colorScheme.surface,
            tonalElevation = 0.dp
        ) {
            FinGoTab.entries.forEachIndexed { index, tab ->
                NavigationBarItem(
                    selected = selectedTab == index,
                    onClick = { onTabSelected(index) },
                    icon = { Icon(tab.icon, contentDescription = tab.label) },
                    label = { Text(tab.label) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = FinGoBlue,
                        selectedTextColor = FinGoBlue,
                        indicatorColor = Color.Transparent,
                        unselectedIconColor = FinGoMuted,
                        unselectedTextColor = FinGoMuted
                    )
                )
            }
        }
    }
}
