package com.movil.saldodelcerro.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.movil.saldodelcerro.services.CategoryService
import com.movil.saldodelcerro.services.LedgerService
import com.movil.saldodelcerro.ui.screens.expense.ExpenseScreen
import com.movil.saldodelcerro.ui.screens.home.HomeScreen
import com.movil.saldodelcerro.ui.screens.income.IncomeScreen
import com.movil.saldodelcerro.ui.screens.summary.SummaryScreen

@Composable
fun FinGoApp(
    ledgerService: LedgerService,
    categoryService: CategoryService
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Scaffold(
        bottomBar = {
            NavigationBar {
                FinGoTab.entries.forEach { tab ->
                    NavigationBarItem(
                        icon = { Icon(tab.icon, contentDescription = tab.label) },
                        label = { Text(tab.label) },
                        selected = currentRoute == tab.name.lowercase(),
                        onClick = {
                            navController.navigate(tab.name.lowercase()) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "home",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("home") { HomeScreen(ledgerService) }
            composable("income") { IncomeScreen(ledgerService, categoryService) }
            composable("expenses") { ExpenseScreen(ledgerService, categoryService) }
            composable("summary") { SummaryScreen(ledgerService) }
        }
    }
}
