package com.kazumasa.othello

import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class GameActivity : AppCompatActivity() {
    private lateinit var boardView: GameBoardView
    private lateinit var blackCount: TextView
    private lateinit var whiteCount: TextView
    private lateinit var ai: AIPlayer
    private var board = GameBoard()
    private var currentPlayer = GameBoard.Cell.BLACK

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)

        val difficultyName = intent.getStringExtra("difficulty") ?: "BEGINNER"
        ai = AIPlayer(AIPlayer.Difficulty.valueOf(difficultyName))

        val info: TextView = findViewById(R.id.gameInfo)
        info.text = "Playing vs $difficultyName AI"

        boardView = findViewById(R.id.boardView)
        boardView.board = board
        boardView.onCellTap = { x, y -> handlePlayerMove(x, y) }

        blackCount = findViewById(R.id.blackCount)
        whiteCount = findViewById(R.id.whiteCount)
        updateCounts()
    }

    private fun handlePlayerMove(x: Int, y: Int) {
        if (currentPlayer != GameBoard.Cell.BLACK) return
        if (board.placePiece(x, y, currentPlayer)) {
            boardView.invalidate()
            nextTurn()
        }
    }

    private fun nextTurn() {
        currentPlayer = if (currentPlayer == GameBoard.Cell.BLACK) GameBoard.Cell.WHITE else GameBoard.Cell.BLACK
        updateCounts()
        if (currentPlayer == GameBoard.Cell.WHITE) {
            aiMove()
        }
        checkGameEnd()
    }

    private fun aiMove() {
        val move = ai.chooseMove(board, GameBoard.Cell.WHITE)
        if (move != null) {
            board.placePiece(move.first, move.second, GameBoard.Cell.WHITE)
            boardView.invalidate()
        }
        currentPlayer = GameBoard.Cell.BLACK
        updateCounts()
    }

    private fun checkGameEnd() {
        val blackMoves = board.getValidMoves(GameBoard.Cell.BLACK)
        val whiteMoves = board.getValidMoves(GameBoard.Cell.WHITE)
        if (blackMoves.isEmpty() && whiteMoves.isEmpty()) {
            val black = board.countPieces(GameBoard.Cell.BLACK)
            val white = board.countPieces(GameBoard.Cell.WHITE)
            val message = when {
                black > white -> "Black wins!"
                white > black -> "White wins!"
                else -> "Draw!"
            }
            Toast.makeText(this, message, Toast.LENGTH_LONG).show()
        }
    }

    private fun updateCounts() {
        blackCount.text = "Black: ${board.countPieces(GameBoard.Cell.BLACK)}"
        whiteCount.text = "White: ${board.countPieces(GameBoard.Cell.WHITE)}"
    }
}
