package com.movil.saldodelcerro.views

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import com.movil.saldodelcerro.services.WalletService

private enum class FinGoTab(val label: String, val icon: ImageVector) {
    Home("Inicio", Icons.Filled.Home),
    Capital("Capital", Icons.Filled.Settings),
    Expenses("Egresos", Icons.Filled.AddCircle),
    Summary("Resumen", Icons.Filled.List)
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
            NavigationBar {
                FinGoTab.entries.forEachIndexed { index, tab ->
                    NavigationBarItem(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        icon = { Icon(tab.icon, contentDescription = tab.label) },
                        label = { Text(tab.label) }
                    )
                }
            }
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