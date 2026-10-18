package com.movil.saldodelcerro.ui.navigation

/** Nombre del argumento de la ruta de detalle. Se usa en el grafo y en el `SavedStateHandle`. */
const val MOVEMENT_ID_ARG = "movementId"

/**
 * Rutas del grafo de navegacion en un unico lugar.
 *
 * Nadie escribe un string suelto: la barra inferior y el `NavHost` leen de aqui, y la ruta con
 * argumento se construye con [MovementDetail.build], asi que el id nunca se concatena a mano.
 */
sealed class FinGoRoute(val route: String) {

    data object Home : FinGoRoute("home")

    data object Income : FinGoRoute("income")

    data object Expenses : FinGoRoute("expenses")

    data object Summary : FinGoRoute("summary")

    /** Detalle de un movimiento: unico destino con argumento (tipado `Long`). */
    data object MovementDetail : FinGoRoute("movement/{$MOVEMENT_ID_ARG}") {
        fun build(movementId: Long): String = "movement/$movementId"
    }
}