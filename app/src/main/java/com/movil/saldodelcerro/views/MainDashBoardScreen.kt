package com.movil.saldodelcerro.views

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.movil.saldodelcerro.services.WalletService

@Composable
fun MainDashboardScreen(
    walletService: WalletService,
    modifier: Modifier = Modifier
) {
    /* TODO: Mondongo
    * TODO: Pues Hagan las cosas aquí
    *  */
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(text = "Me fumo cuatro porros")
    }
}