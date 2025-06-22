package com.example.marubatsuevolution

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlin.math.max
import kotlin.math.min
import kotlin.random.Random

class GameViewModel(application: Application) : AndroidViewModel(application) {
    private val repo = ProgressRepository(application)

    private val _state = MutableStateFlow(GameState())
    val state: GameState get() = _state.value
    val stateFlow: StateFlow<GameState> = _state

    init {
        viewModelScope.launch {
            val savedLevel = repo.currentLevel.first()
            val savedHighest = repo.highestUnlockedLevel.first()
            startNewGame(savedLevel, savedHighest)
        }
    }

    fun onCellTapped(row: Int, col: Int) {
        val s = _state.value
        if (s.isAbilitySelectionMode) {
            onAbilityCellSelected(row, col)
            return
        }
        if (s.winner != null || s.isDraw || s.blocked.contains(row to col) || s.board[row][col] != null) return

        val newBoard = s.board.toMutableList().apply {
            this[row] = this[row].toMutableList().apply {
                this[col] = s.currentPlayer
            }
        }.toList()

        val winner = checkWinner(s.boardSize, newBoard)
        val isDraw = winner == null && isBoardFull(newBoard, s.blocked)

        _state.value = s.copy(
            board = newBoard,
            currentPlayer = if (s.currentPlayer == Player.X) Player.O else Player.X,
            winner = winner,
            isDraw = isDraw
        )

        if (winner != null || isDraw) {
            onGameOver(winner)
        } else if (_state.value.level >= 5 && _state.value.currentPlayer == Player.O) {
            aiMove()
        }
    }

    fun activateAbilitySelection() {
        val s = _state.value
        if (s.level >= 4 && s.currentPlayer == Player.X && !s.isXAbilityUsed && s.winner == null && !s.isDraw) {
            _state.value = s.copy(isAbilitySelectionMode = true)
        }
    }

    private fun onAbilityCellSelected(row: Int, col: Int) {
        val s = _state.value
        if (!s.isAbilitySelectionMode) return
        if (s.currentPlayer != Player.X || s.isXAbilityUsed) {
            _state.value = s.copy(isAbilitySelectionMode = false)
            return
        }
        if (s.board[row][col] != Player.X) {
            _state.value = s.copy(isAbilitySelectionMode = false)
            return
        }

        val newBoard = s.board.toMutableList().apply {
            this[row] = this[row].toMutableList().apply {
                this[col] = null
            }
        }.toList()

        _state.value = s.copy(
            board = newBoard,
            isXAbilityUsed = true,
            isAbilitySelectionMode = false,
            currentPlayer = Player.O
        )

        if (_state.value.level >= 5 && _state.value.currentPlayer == Player.O) {
            aiMove()
        }
    }

    fun restartGame() {
        startNewGame(state.level, state.highestUnlockedLevel)
    }

    fun playSelectedLevel(level: Int) {
        viewModelScope.launch {
            val currentHighestUnlocked = repo.highestUnlockedLevel.first()
            startNewGame(level, currentHighestUnlocked)
            _state.value = _state.value.copy(currentAppScreen = AppScreen.GAME)
        }
    }

    fun restartCurrentGameOrNextLevel(isWin: Boolean) {
        viewModelScope.launch {
            if (isWin) {
                val newLevel = state.level + 1
                repo.setCurrentGameLevel(newLevel)
                if (newLevel > state.highestUnlockedLevel) {
                    repo.setHighestUnlockedLevel(newLevel)
                    _state.value = _state.value.copy(highestUnlockedLevel = newLevel)
                }
                startNewGame(newLevel, _state.value.highestUnlockedLevel)
            } else {
                startNewGame(state.level, state.highestUnlockedLevel)
            }
        }
    }

    fun navigateToLevelSelect() {
        _state.value = _state.value.copy(currentAppScreen = AppScreen.LEVEL_SELECT)
    }

    fun navigateToGame() {
        _state.value = _state.value.copy(currentAppScreen = AppScreen.GAME)
    }

    private fun onGameOver(winner: Player?) {
        viewModelScope.launch {
            if (winner != null) {
                val newLevelForCurrentGame = state.level + 1
                repo.setCurrentGameLevel(newLevelForCurrentGame)
                if (newLevelForCurrentGame > state.highestUnlockedLevel) {
                    repo.setHighestUnlockedLevel(newLevelForCurrentGame)
                    _state.value = _state.value.copy(highestUnlockedLevel = newLevelForCurrentGame)
                }
                startNewGame(newLevelForCurrentGame, _state.value.highestUnlockedLevel)
            } else if (state.isDraw) {
                startNewGame(state.level, state.highestUnlockedLevel)
            }
        }
    }

    private fun startNewGame(levelToStart: Int, initialHighestUnlocked: Int) {
        val size = if (levelToStart >= 2) 4 else 3
        val board = List(size) { List<Player?>(size) { null } }
        val blocked = generateBlockedCells(levelToStart, size)
        _state.value = GameState(
            level = levelToStart,
            boardSize = size,
            board = board,
            blocked = blocked,
            currentPlayer = Player.X,
            winner = null,
            isDraw = false,
            isXAbilityUsed = false,
            isAbilitySelectionMode = false,
            highestUnlockedLevel = initialHighestUnlocked,
            currentAppScreen = AppScreen.GAME
        )
    }

    private fun generateBlockedCells(level: Int, size: Int): Set<Pair<Int, Int>> {
        // Determine how many cells should be blocked based on the current level.
        val numBlocked = when {
            level < 3 -> 0                 // Lv.1-2: no blocked cells
            level < 5 -> 2                 // Lv.3-4: 2 blocked cells
            level < 7 -> 3                 // Lv.5-6: 3 blocked cells
            else -> 4                      // Lv.7+: 4 blocked cells
        }

        if (numBlocked == 0) return emptySet()

        val cells = mutableSetOf<Pair<Int, Int>>()
        val random = Random(System.currentTimeMillis())
        while (cells.size < numBlocked) {
            val r = random.nextInt(size)
            val c = random.nextInt(size)
            cells += r to c
        }
        return cells
    }

    private fun checkWinner(size: Int, board: List<List<Player?>>): Player? {
        for (i in 0 until size) {
            if (board[i].all { it == Player.X }) return Player.X
            if (board[i].all { it == Player.O }) return Player.O
            if ((0 until size).all { board[it][i] == Player.X }) return Player.X
            if ((0 until size).all { board[it][i] == Player.O }) return Player.O
        }
        if ((0 until size).all { board[it][it] == Player.X }) return Player.X
        if ((0 until size).all { board[it][it] == Player.O }) return Player.O
        if ((0 until size).all { board[it][size - it - 1] == Player.X }) return Player.X
        if ((0 until size).all { board[it][size - it - 1] == Player.O }) return Player.O
        return null
    }

    private fun isBoardFull(board: List<List<Player?>>, blocked: Set<Pair<Int, Int>>): Boolean {
        for (r in board.indices) {
            for (c in board[r].indices) {
                if ((r to c) !in blocked && board[r][c] == null) return false
            }
        }
        return true
    }

    private fun minimax(
        board: List<MutableList<Player?>>, blocked: Set<Pair<Int, Int>>, size: Int,
        maximizing: Boolean, depth: Int
    ): Int {
        val winner = checkWinner(size, board)
        if (winner == Player.O) return 10 - depth
        if (winner == Player.X) return depth - 10
        if (isBoardFull(board, blocked)) return 0

        return if (maximizing) {
            var best = Int.MIN_VALUE
            for (r in 0 until size) {
                for (c in 0 until size) {
                    if (board[r][c] == null && (r to c) !in blocked) {
                        board[r][c] = Player.O
                        val score = minimax(board, blocked, size, false, depth + 1)
                        board[r][c] = null
                        best = max(best, score)
                    }
                }
            }
            best
        } else {
            var best = Int.MAX_VALUE
            for (r in 0 until size) {
                for (c in 0 until size) {
                    if (board[r][c] == null && (r to c) !in blocked) {
                        board[r][c] = Player.X
                        val score = minimax(board, blocked, size, true, depth + 1)
                        board[r][c] = null
                        best = min(best, score)
                    }
                }
            }
            best
        }
    }

    private fun findBestMove(board: List<MutableList<Player?>>, blocked: Set<Pair<Int, Int>>, size: Int): Pair<Int, Int>? {
        var bestScore = Int.MIN_VALUE
        var move: Pair<Int, Int>? = null
        for (r in 0 until size) {
            for (c in 0 until size) {
                if (board[r][c] == null && (r to c) !in blocked) {
                    board[r][c] = Player.O
                    val score = minimax(board, blocked, size, false, 0)
                    board[r][c] = null
                    if (score > bestScore) {
                        bestScore = score
                        move = r to c
                    }
                }
            }
        }
        return move
    }

    private fun aiMove() {
        val s = _state.value
        val currentMutableBoard = s.board.map { it.toMutableList() }.toMutableList()
        val move = findBestMove(currentMutableBoard, s.blocked, s.boardSize)

        if (move == null) {
            val isDraw = s.winner == null && isBoardFull(s.board, s.blocked)
            _state.value = s.copy(isDraw = isDraw)
            if (isDraw) onGameOver(null)
            return
        }

        val newBoard = s.board.toMutableList().apply {
            this[move.first] = this[move.first].toMutableList().apply {
                this[move.second] = Player.O
            }
        }.toList()

        val winner = checkWinner(s.boardSize, newBoard)
        val isDraw = winner == null && isBoardFull(newBoard, s.blocked)

        _state.value = s.copy(
            board = newBoard,
            currentPlayer = Player.X,
            winner = winner,
            isDraw = isDraw
        )

        if (winner != null || isDraw) {
            onGameOver(winner)
        }
    }
}
