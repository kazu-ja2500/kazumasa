package com.example.othello

class AIPlayer(private val difficulty: Difficulty) {
    enum class Difficulty { BEGINNER, EASY, MEDIUM, HARD }

    fun chooseMove(board: GameBoard, player: GameBoard.Cell): Pair<Int, Int>? {
        val moves = board.getValidMoves(player)
        if (moves.isEmpty()) return null
        return when (difficulty) {
            Difficulty.BEGINNER -> chooseBestFlip(board, moves, player)
            Difficulty.EASY -> minimaxMove(board, player, depth = 2, useAlphaBeta = false)
            Difficulty.MEDIUM -> minimaxMove(board, player, depth = 3, useAlphaBeta = true)
            Difficulty.HARD -> minimaxMove(board, player, depth = 4, useAlphaBeta = true)
        }
    }

    private fun chooseBestFlip(board: GameBoard, moves: List<Pair<Int, Int>>, player: GameBoard.Cell): Pair<Int, Int> {
        return moves.maxByOrNull { board.flippedPieces(it.first, it.second, player) }!!
    }

    private fun minimaxMove(board: GameBoard, player: GameBoard.Cell, depth: Int, useAlphaBeta: Boolean): Pair<Int, Int> {
        var bestScore = Int.MIN_VALUE
        var bestMove = board.getValidMoves(player).first()
        var alpha = Int.MIN_VALUE
        var beta = Int.MAX_VALUE
        for ((x, y) in board.getValidMoves(player)) {
            val newBoard = board.copy()
            newBoard.placePiece(x, y, player)
            val score = minimax(newBoard, opposite(player), depth - 1, false, alpha, beta, useAlphaBeta)
            if (score > bestScore) {
                bestScore = score
                bestMove = x to y
            }
            if (useAlphaBeta) {
                alpha = maxOf(alpha, bestScore)
                if (beta <= alpha) break
            }
        }
        return bestMove
    }

    private fun minimax(board: GameBoard, player: GameBoard.Cell, depth: Int, maximizing: Boolean, alphaInit: Int, betaInit: Int, useAlphaBeta: Boolean): Int {
        if (depth == 0 || board.getValidMoves(GameBoard.Cell.BLACK).isEmpty() && board.getValidMoves(GameBoard.Cell.WHITE).isEmpty()) {
            return evaluate(board, player)
        }

        var alpha = alphaInit
        var beta = betaInit
        val moves = board.getValidMoves(player)
        if (moves.isEmpty()) {
            return minimax(board, opposite(player), depth - 1, !maximizing, alpha, beta, useAlphaBeta)
        }

        if (maximizing) {
            var maxEval = Int.MIN_VALUE
            for ((x, y) in moves) {
                val newBoard = board.copy()
                newBoard.placePiece(x, y, player)
                val eval = minimax(newBoard, opposite(player), depth - 1, false, alpha, beta, useAlphaBeta)
                maxEval = maxOf(maxEval, eval)
                if (useAlphaBeta) {
                    alpha = maxOf(alpha, eval)
                    if (beta <= alpha) break
                }
            }
            return maxEval
        } else {
            var minEval = Int.MAX_VALUE
            for ((x, y) in moves) {
                val newBoard = board.copy()
                newBoard.placePiece(x, y, player)
                val eval = minimax(newBoard, opposite(player), depth - 1, true, alpha, beta, useAlphaBeta)
                minEval = minOf(minEval, eval)
                if (useAlphaBeta) {
                    beta = minOf(beta, eval)
                    if (beta <= alpha) break
                }
            }
            return minEval
        }
    }

    private fun evaluate(board: GameBoard, player: GameBoard.Cell): Int {
        val myCount = board.countPieces(player)
        val oppCount = board.countPieces(opposite(player))
        return myCount - oppCount
    }

    private fun opposite(cell: GameBoard.Cell): GameBoard.Cell =
        if (cell == GameBoard.Cell.BLACK) GameBoard.Cell.WHITE else GameBoard.Cell.BLACK
}
