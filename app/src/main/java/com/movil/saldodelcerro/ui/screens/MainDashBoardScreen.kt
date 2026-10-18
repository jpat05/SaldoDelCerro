package com.movil.saldodelcerro.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.movil.saldodelcerro.ui.theme.SaldoDelCerroTheme

@Composable
fun MainDashboardScreen(modifier: Modifier = Modifier) {
    MainDashboardContent(modifier = modifier)
}

@Composable
private fun MainDashboardContent(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(text = "Hello Android")
    }
}

@Preview(showBackground = true, name = "MainDashboardScreen")
@Composable
private fun MainDashboardContentPreview() {
    SaldoDelCerroTheme(dynamicColor = false) {
        MainDashboardContent()
    }
}
