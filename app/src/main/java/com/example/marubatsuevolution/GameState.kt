package com.example.marubatsuevolution

// Enum representing players
enum class Player(val symbol: String) {
    X("X"), O("O")
}

// Enum for current screen of the app
enum class AppScreen {
    GAME,
    LEVEL_SELECT
}

// Holds all game related state
data class GameState(
    val level: Int = 1,
    val boardSize: Int = 3,
    val board: List<List<Player?>> = List(3) { List<Player?>(3) { null } },
    val blocked: Set<Pair<Int, Int>> = emptySet(),
    val currentPlayer: Player = Player.X,
    val winner: Player? = null,
    val isDraw: Boolean = false,
    val isXAbilityUsed: Boolean = false,
    val isAbilitySelectionMode: Boolean = false,
    val highestUnlockedLevel: Int = 1,
    val currentAppScreen: AppScreen = AppScreen.GAME
)
