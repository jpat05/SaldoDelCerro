package com.movil.saldodelcerro.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.movil.saldodelcerro.services.WalletService
import com.movil.saldodelcerro.ui.theme.FinGoBlue
import com.movil.saldodelcerro.ui.theme.FinGoBlueSoft
import com.movil.saldodelcerro.ui.theme.FinGoGreen
import com.movil.saldodelcerro.ui.theme.FinGoGreenSoft
import com.movil.saldodelcerro.ui.theme.FinGoInk
import com.movil.saldodelcerro.ui.theme.FinGoMuted
import com.movil.saldodelcerro.ui.theme.FinGoRed
import com.movil.saldodelcerro.ui.theme.FinGoRedSoft
import com.movil.saldodelcerro.ui.theme.FinGoSurface
import com.movil.saldodelcerro.utils.formatMoney
import java.math.BigDecimal

// Nombre que se muestra en el saludo; cuando exista el login se lee del perfil.
private const val USER_NAME = "Alvin"

@Composable
fun HomeScreen(
    walletService: WalletService,
    onSeeTransactions: () -> Unit,
    modifier: Modifier = Modifier
) {
    val balance by walletService.availableBalance.collectAsState(initial = BigDecimal.ZERO)
    val totalIncome by walletService.totalIncome.collectAsState(initial = BigDecimal.ZERO)
    val totalExpense by walletService.totalExpense.collectAsState(initial = BigDecimal.ZERO)

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item { HomeHeader() }
        item { Greeting(userName = USER_NAME) }
        item { BalanceCard(balance) }
        item {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                StatCard(
                    label = "Ingresos",
                    value = totalIncome,
                    icon = Icons.Filled.TrendingUp,
                    tint = FinGoGreen,
                    badgeColor = FinGoGreenSoft,
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    label = "Egresos",
                    value = totalExpense,
                    icon = Icons.Filled.TrendingDown,
                    tint = FinGoRed,
                    badgeColor = FinGoRedSoft,
                    modifier = Modifier.weight(1f)
                )
            }
        }
        item { SeeTransactionsButton(onClick = onSeeTransactions) }
    }
}

/** Wordmark FinGO con el avatar a la derecha. */
@Composable
private fun HomeHeader() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "FinGO",
            style = MaterialTheme.typography.headlineLarge,
            color = FinGoInk
        )
        Surface(
            shape = CircleShape,
            color = FinGoSurface,
            modifier = Modifier.size(44.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.Filled.Person,
                    contentDescription = "Perfil",
                    tint = FinGoInk,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

@Composable
private fun Greeting(userName: String) {
    Column {
        Text(
            text = "Hola, $userName",
            style = MaterialTheme.typography.headlineSmall,
            color = FinGoInk
        )
        Text(
            text = "Aquí tienes un resumen de tus finanzas",
            style = MaterialTheme.typography.bodyMedium,
            color = FinGoMuted
        )
    }
}

@Composable
private fun BalanceCard(balance: BigDecimal) {
    Card(Modifier.fillMaxWidth(), shape = MaterialTheme.shapes.medium) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconBadge(
                icon = Icons.Filled.AccountBalanceWallet,
                tint = FinGoInk,
                background = FinGoBlueSoft,
                badgeSize = 52.dp,
                iconSize = 26.dp,
                shape = RoundedCornerShape(16.dp)
            )
            Column(
                modifier = Modifier.padding(start = 16.dp),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    text = "Saldo disponible",
                    style = MaterialTheme.typography.bodyMedium,
                    color = FinGoMuted
                )
                Text(
                    text = balance.formatMoney(),
                    style = MaterialTheme.typography.headlineLarge,
                    color = FinGoInk
                )
            }
        }
    }
}

@Composable
private fun StatCard(
    label: String,
    value: BigDecimal,
    icon: ImageVector,
    tint: Color,
    badgeColor: Color,
    modifier: Modifier = Modifier
) {
    Card(modifier, shape = MaterialTheme.shapes.medium) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconBadge(
                    icon = icon,
                    tint = tint,
                    background = badgeColor,
                    badgeSize = 32.dp,
                    iconSize = 18.dp,
                    shape = CircleShape
                )
                Text(
                    text = label,
                    style = MaterialTheme.typography.bodySmall,
                    color = FinGoInk,
                    modifier = Modifier.padding(start = 8.dp).weight(1f)
                )
            }
            Text(
                text = value.formatMoney(),
                style = MaterialTheme.typography.titleMedium,
                color = FinGoInk
            )
        }
    }
}

@Composable
private fun SeeTransactionsButton(onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth().height(52.dp),
        shape = MaterialTheme.shapes.small,
        colors = ButtonDefaults.buttonColors(
            containerColor = FinGoBlue,
            contentColor = Color.White
        )
    ) {
        Box(Modifier.fillMaxSize()) {
            Text(
                text = "Ver movimientos",
                style = MaterialTheme.typography.labelLarge,
                modifier = Modifier.align(Alignment.Center)
            )
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(end = 20.dp)
                    .size(20.dp)
            )
        }
    }
}

/** Cuadro o circulo con el icono del dato. */
@Composable
private fun IconBadge(
    icon: ImageVector,
    tint: Color,
    background: Color,
    badgeSize: Dp,
    iconSize: Dp,
    shape: Shape
) {
    Box(
        modifier = Modifier
            .size(badgeSize)
            .clip(shape)
            .background(background),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = tint,
            modifier = Modifier.size(iconSize)
        )
    }
}
