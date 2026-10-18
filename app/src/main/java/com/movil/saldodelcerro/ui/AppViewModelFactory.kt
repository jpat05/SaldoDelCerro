package com.movil.saldodelcerro.ui

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.movil.saldodelcerro.services.CategoryService
import com.movil.saldodelcerro.services.LedgerService
import com.movil.saldodelcerro.ui.screens.expense.ExpenseViewModel
import com.movil.saldodelcerro.ui.screens.home.HomeViewModel
import com.movil.saldodelcerro.ui.screens.income.IncomeViewModel
import com.movil.saldodelcerro.ui.screens.movement.MovementDetailViewModel
import com.movil.saldodelcerro.ui.screens.summary.SummaryViewModel

/**
 * Manual DI root for the ViewModels — the analog of wiring `@Service` beans into a context.
 */
fun appViewModelFactory(
    ledgerService: LedgerService,
    categoryService: CategoryService
): ViewModelProvider.Factory = viewModelFactory {
    initializer { HomeViewModel(ledgerService) }
    initializer { SummaryViewModel(ledgerService) }
    initializer { IncomeViewModel(ledgerService, categoryService) }
    initializer { ExpenseViewModel(ledgerService, categoryService) }

    // El detalle es el unico ViewModel con argumento de navegacion: `createSavedStateHandle()`
    // recibe el `SavedStateHandle` que el framework llena con los argumentos de la ruta.
    initializer {
        MovementDetailViewModel(
            ledgerService = ledgerService,
            savedStateHandle = createSavedStateHandle()
        )
    }
}
