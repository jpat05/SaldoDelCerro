package com.movil.saldodelcerro.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.movil.saldodelcerro.services.CategoryService
import com.movil.saldodelcerro.services.LedgerService
import com.movil.saldodelcerro.ui.appViewModelFactory
import com.movil.saldodelcerro.ui.screens.expense.ExpenseScreen
import com.movil.saldodelcerro.ui.screens.home.HomeScreen
import com.movil.saldodelcerro.ui.screens.income.IncomeScreen
import com.movil.saldodelcerro.ui.screens.movement.MovementDetailScreen
import com.movil.saldodelcerro.ui.screens.summary.SummaryScreen

@Composable
fun FinGoApp(
    ledgerService: LedgerService,
    categoryService: CategoryService
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val viewModelFactory = remember(ledgerService, categoryService) {
        appViewModelFactory(ledgerService, categoryService)
    }

    Scaffold(
        bottomBar = {
            NavigationBar {
                FinGoTab.entries.forEach { tab ->
                    NavigationBarItem(
                        icon = { Icon(tab.icon, contentDescription = tab.label) },
                        label = { Text(tab.label) },
                        selected = currentRoute == tab.route.route,
                        onClick = {
                            navController.navigate(tab.route.route) {
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
            startDestination = FinGoRoute.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(FinGoRoute.Home.route) {
                HomeScreen(viewModel(factory = viewModelFactory))
            }
            composable(FinGoRoute.Income.route) {
                IncomeScreen(viewModel(factory = viewModelFactory))
            }
            composable(FinGoRoute.Expenses.route) {
                ExpenseScreen(viewModel(factory = viewModelFactory))
            }
            composable(FinGoRoute.Summary.route) {
                SummaryScreen(
                    viewModel = viewModel(factory = viewModelFactory),
                    onMovementClick = { movementId ->
                        navController.navigate(FinGoRoute.MovementDetail.build(movementId))
                    }
                )
            }

            // [FUNCIONALIDAD: DETALLE CON ARGUMENTO]
            // Proposito: unico destino que recibe un argumento. Viaja tipado como Long (no como
            // texto dentro de la ruta) y el ViewModel lo lee de su SavedStateHandle, asi que la
            // pantalla nunca parsea la ruta a mano.
            composable(
                route = FinGoRoute.MovementDetail.route,
                arguments = listOf(navArgument(MOVEMENT_ID_ARG) { type = NavType.LongType })
            ) {
                MovementDetailScreen(
                    viewModel = viewModel(factory = viewModelFactory),
                    onBack = { navController.popBackStack() }
                )
            }
        }
    }
}
