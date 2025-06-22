package com.example.marubatsuevolution

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun Board(
    size: Int,
    board: List<List<Player?>>, 
    blocked: Set<Pair<Int, Int>>,
    isAbilitySelectionMode: Boolean,
    currentPlayer: Player,
    onTap: (Int, Int) -> Unit
) {
    Column {
        for (row in 0 until size) {
            Row {
                for (col in 0 until size) {
                    val isBlocked = blocked.contains(row to col)
                    val isOwnMark = board[row][col] == currentPlayer
                    val isTarget = isAbilitySelectionMode && isOwnMark && !isBlocked
                    Cell(board[row][col], isBlocked, isTarget) { onTap(row, col) }
                }
            }
        }
    }
}

@Composable
fun Cell(player: Player?, blocked: Boolean, isAbilityTarget: Boolean, onTap: () -> Unit) {
    val cellSize: Dp = 64.dp
    val symbol = when {
        blocked -> "■"
        player != null -> player.symbol
        else -> ""
    }
    val clickableModifier = if (blocked) Modifier else Modifier.clickable {
        if (player == null || isAbilityTarget) onTap()
    }
    Box(
        modifier = Modifier
            .size(cellSize)
            .border(BorderStroke(1.dp, MaterialTheme.colorScheme.onBackground))
            .background(
                when {
                    blocked -> Color.LightGray
                    isAbilityTarget -> Color.Yellow.copy(alpha = 0.5f)
                    else -> MaterialTheme.colorScheme.surface
                }
            )
            .then(clickableModifier),
        contentAlignment = Alignment.Center
    ) {
        Text(text = symbol)
    }
}
