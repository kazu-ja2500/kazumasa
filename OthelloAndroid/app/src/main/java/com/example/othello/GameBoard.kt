package com.example.othello

/**
 * Simple Othello game logic for an 8x8 board.
 */
class GameBoard {
    enum class Cell { EMPTY, BLACK, WHITE }

    private val board: Array<Array<Cell>> = Array(8) { Array(8) { Cell.EMPTY } }

    init {
        // Initialize the four center pieces
        board[3][3] = Cell.WHITE
        board[3][4] = Cell.BLACK
        board[4][3] = Cell.BLACK
        board[4][4] = Cell.WHITE
    }

    fun getCell(x: Int, y: Int): Cell = board[y][x]

    fun copy(): GameBoard {
        val newBoard = GameBoard()
        for (y in 0 until 8) {
            for (x in 0 until 8) {
                newBoard.board[y][x] = board[y][x]
            }
        }
        return newBoard
    }

    fun getValidMoves(player: Cell): List<Pair<Int, Int>> {
        val moves = mutableListOf<Pair<Int, Int>>()
        for (y in 0 until 8) {
            for (x in 0 until 8) {
                if (isValidMove(x, y, player)) moves.add(x to y)
            }
        }
        return moves
    }

    fun countPieces(player: Cell): Int {
        var count = 0
        for (y in 0 until 8) for (x in 0 until 8) if (board[y][x] == player) count++
        return count
    }

    fun flippedPieces(x: Int, y: Int, player: Cell): Int {
        if (!isValidMove(x, y, player)) return 0
        val opponent = if (player == Cell.BLACK) Cell.WHITE else Cell.BLACK
        var flipped = 0
        for (dx in -1..1) for (dy in -1..1) {
            if (dx == 0 && dy == 0) continue
            var nx = x + dx
            var ny = y + dy
            var count = 0
            while (nx in 0..7 && ny in 0..7 && board[ny][nx] == opponent) {
                count++
                nx += dx
                ny += dy
            }
            if (nx in 0..7 && ny in 0..7 && board[ny][nx] == player) {
                flipped += count
            }
        }
        return flipped
    }

    fun isValidMove(x: Int, y: Int, player: Cell): Boolean {
        if (board[y][x] != Cell.EMPTY) return false
        val opponent = if (player == Cell.BLACK) Cell.WHITE else Cell.BLACK
        for (dx in -1..1) for (dy in -1..1) {
            if (dx == 0 && dy == 0) continue
            var nx = x + dx
            var ny = y + dy
            var foundOpponent = false
            while (nx in 0..7 && ny in 0..7 && board[ny][nx] == opponent) {
                foundOpponent = true
                nx += dx
                ny += dy
            }
            if (foundOpponent && nx in 0..7 && ny in 0..7 && board[ny][nx] == player) {
                return true
            }
        }
        return false
    }

    fun placePiece(x: Int, y: Int, player: Cell): Boolean {
        if (!isValidMove(x, y, player)) return false
        val opponent = if (player == Cell.BLACK) Cell.WHITE else Cell.BLACK
        board[y][x] = player
        for (dx in -1..1) for (dy in -1..1) {
            if (dx == 0 && dy == 0) continue
            var nx = x + dx
            var ny = y + dy
            val toFlip = mutableListOf<Pair<Int, Int>>()
            while (nx in 0..7 && ny in 0..7 && board[ny][nx] == opponent) {
                toFlip.add(nx to ny)
                nx += dx
                ny += dy
            }
            if (nx in 0..7 && ny in 0..7 && board[ny][nx] == player) {
                for ((fx, fy) in toFlip) board[fy][fx] = player
            }
        }
        return true
    }
}
