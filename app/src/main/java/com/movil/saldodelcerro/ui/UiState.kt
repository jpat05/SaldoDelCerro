package com.movil.saldodelcerro.ui

/**
 * Estado de UI de una pantalla, modelado explicitamente.
 *
 * Cada ViewModel expone **un unico** `StateFlow<UiState<T>>`: el primer valor es [Loading] porque
 * la primera emision de Room es asincrona, y [Error] cubre una lectura que falla. El caso normal
 * es [Success], que envuelve el estado concreto de la pantalla (un `data class` propio de cada
 * screen), de modo que los composables hijos siguen recibiendo solo datos inmutables.
 */
sealed interface UiState<out T> {

    /** Todavia no llego el primer dato de la base de datos. */
    data object Loading : UiState<Nothing>

    /** Estado estable de la pantalla. */
    data class Success<T>(val data: T) : UiState<T>

    /** La lectura fallo y no hay datos que mostrar. */
    data class Error(val message: String) : UiState<Nothing>
}