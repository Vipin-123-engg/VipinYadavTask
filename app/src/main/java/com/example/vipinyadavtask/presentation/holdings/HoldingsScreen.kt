package com.example.vipinyadavtask.presentation.holdings

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.vipinyadavtask.domain.model.Holding
import com.example.vipinyadavtask.domain.model.PortfolioCalculator
import kotlin.math.abs

//@Preview(showSystemUi = true, showBackground = true)
@Composable
fun HoldingsScreen(
    viewModel: HoldingsViewModel = viewModel()
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.dispatch(HoldingsIntent.Load)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.statusBars)
    ) {
        when {
            state.isLoading -> CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            state.error != null -> Text(
                state.error ?: "",
                color = Color.Red,
                modifier = Modifier.align(Alignment.Center)
            )

            else -> {
                Column(modifier = Modifier.fillMaxSize()) {
                    LazyColumn(modifier = Modifier.weight(1f)) {
                        items(state.holdings) { holding ->
                            HoldingRow(holding)
                            Divider()
                        }
                    }
                    SummaryCard(
                        expanded = state.isSummaryExpanded,
                        onToggle = { viewModel.dispatch(HoldingsIntent.ToggleSummary) },
                        currentValue = state.summary?.currentValue ?: 0.0,
                        totalInvestment = state.summary?.totalInvestment ?: 0.0,
                        todaysPnl = state.summary?.todaysPnl ?: 0.0,
                        totalPnl = state.summary?.totalPnl ?: 0.0
                    )
                }
            }
        }
    }
}

@Composable
private fun HoldingRow(holding: Holding) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Column {
                Text(
                    holding.symbol,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    "NET QTY: ${holding.quantity}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }
            Column(horizontalAlignment = Alignment.End) {
                Text("LTP: ₹ ${holding.ltp}", style = MaterialTheme.typography.bodyMedium)
                val pnl = PortfolioCalculator.perHoldingPnl(holding)
                val color = if (pnl >= 0) Color(0xFF0A8754) else Color(0xFFB00020)
                Text("P&L: ${formatCurrency(pnl)}", color = color)
            }
        }
    }
}

@Composable
private fun SummaryCard(
    expanded: Boolean,
    onToggle: () -> Unit,
    currentValue: Double,
    totalInvestment: Double,
    todaysPnl: Double,
    totalPnl: Double
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .padding(bottom = 8.dp)
            .animateContentSize()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Details expand ABOVE the header
            AnimatedVisibility(
                visible = expanded,
                enter = expandVertically(),
                exit = shrinkVertically()
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    SummaryRow("Current value*", currentValue)
                    SummaryRow("Total investment*", totalInvestment)
                    SummaryRow(
                        "Today's PNL*",
                        todaysPnl,
                        valueColor = if (todaysPnl >= 0) Color(0xFF0A8754) else Color(0xFFB00020)
                    )
                    Spacer(Modifier.height(8.dp))
                }
            }

            Divider()
            // Fixed header at the bottom of the card
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Profit & Loss*")
                    // Rotating arrow icon toggles expansion
                    val rotationAngle by animateFloatAsState(
                        targetValue = if (expanded) 180f else 0f,
                        animationSpec = tween(durationMillis = 300),
                        label = "arrow_rotation"
                    )
                    IconButton(onClick = onToggle) {
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowDown,
                            contentDescription = null,
                            modifier = Modifier.rotate(rotationAngle),
                            tint = Color.Gray
                        )
                    }
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val color = if (totalPnl >= 0) Color(0xFF0A8754) else Color(0xFFB00020)
                    val pct = if (totalInvestment > 0) (totalPnl / totalInvestment) * 100 else 0.0
                    Text(
                        "${formatCurrency(totalPnl)} (${"%.2f".format(pct)}%)",
                        color = color,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

@Composable
private fun SummaryRow(
    label: String,
    value: Double,
    valueColor: Color = MaterialTheme.colorScheme.onSurface
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp), horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, color = Color.Gray)
        Text(formatCurrency(value), color = valueColor)
    }
}

private fun formatCurrency(amount: Double): String {
    val absValue = abs(amount)
    val formatted = "₹ ${"%.2f".format(absValue)}"
    return if (amount < 0) "-$formatted" else formatted
}


