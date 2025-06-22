package com.example.marubatsuevolution

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import com.example.marubatsuevolution.ui.theme.MaruBatsuEvolutionTheme

class MainActivity : ComponentActivity() {
    private val viewModel: GameViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaruBatsuEvolutionTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    val state by viewModel.stateFlow.collectAsState()
                    when (state.currentAppScreen) {
                        AppScreen.GAME -> GameScreen(viewModel)
                        AppScreen.LEVEL_SELECT -> LevelSelectScreen(viewModel)
                    }
                }
            }
        }
    }
}

@Composable
fun GameScreen(viewModel: GameViewModel) {
    val state by viewModel.stateFlow.collectAsState()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Level: ${'$'}{state.level}")

        Button(
            onClick = { viewModel.navigateToLevelSelect() },
            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
        ) {
            Text("Select Level (Current Max: ${'$'}{state.highestUnlockedLevel})")
        }

        if (state.level >= 4 && state.currentPlayer == Player.X && !state.isXAbilityUsed && state.winner == null && !state.isDraw) {
            Button(
                onClick = { viewModel.activateAbilitySelection() },
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
            ) {
                Text("Use Ability (Clear your mark once)")
            }
        }

        if (state.isAbilitySelectionMode) {
            Text(text = "Ability Mode: Tap your mark to clear it", color = MaterialTheme.colorScheme.primary)
        }

        Board(
            size = state.boardSize,
            board = state.board,
            blocked = state.blocked,
            isAbilitySelectionMode = state.isAbilitySelectionMode,
            currentPlayer = state.currentPlayer
        ) { row, col ->
            viewModel.onCellTapped(row, col)
        }
        if (state.winner != null) {
            Text(text = "${'$'}{state.winner!!.symbol} Wins!")
            Button(onClick = { viewModel.restartCurrentGameOrNextLevel(true) }) {
                Text("Next Level")
            }
        } else if (state.isDraw) {
            Text(text = "It's a Draw!")
            Button(onClick = { viewModel.restartCurrentGameOrNextLevel(false) }) {
                Text("Retry Level")
            }
        }
    }
}

@Composable
fun LevelSelectScreen(viewModel: GameViewModel) {
    val state by viewModel.stateFlow.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(text = "Select Level", style = MaterialTheme.typography.headlineMedium)
        Text(text = "Highest Unlocked: ${'$'}{state.highestUnlockedLevel}", style = MaterialTheme.typography.bodyLarge)

        LazyVerticalGrid(
            columns = GridCells.Fixed(4),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(state.highestUnlockedLevel) { idx ->
                val levelNum = idx + 1
                Button(onClick = { viewModel.playSelectedLevel(levelNum) }, modifier = Modifier.fillMaxWidth()) {
                    Text("Lv $levelNum")
                }
            }
        }

        Button(
            onClick = { viewModel.navigateToGame() },
            modifier = Modifier.fillMaxWidth().padding(top = 16.dp)
        ) { Text("Back to Current Game") }
    }
}
