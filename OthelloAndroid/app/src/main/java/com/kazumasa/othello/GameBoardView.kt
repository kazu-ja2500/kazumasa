package com.kazumasa.othello

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View

/**
 * Simple view to draw the Othello board and pieces.
 */
class GameBoardView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : View(context, attrs) {
    var board: GameBoard = GameBoard()
        set(value) {
            field = value
            invalidate()
        }

    var onCellTap: ((Int, Int) -> Unit)? = null

    private val linePaint = Paint().apply {
        color = Color.BLACK
        strokeWidth = 2f
    }
    private val blackPaint = Paint().apply { color = Color.BLACK }
    private val whitePaint = Paint().apply { color = Color.WHITE }
    private val boardPaint = Paint().apply { color = Color.parseColor("#008000") }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val size = minOf(width, height).toFloat()
        val cell = size / 8f
        // draw board background
        canvas.drawRect(0f, 0f, size, size, boardPaint)
        // grid lines
        for (i in 0..8) {
            val pos = i * cell
            canvas.drawLine(pos, 0f, pos, size, linePaint)
            canvas.drawLine(0f, pos, size, pos, linePaint)
        }
        // pieces
        for (y in 0 until 8) {
            for (x in 0 until 8) {
                val cellVal = board.getCell(x, y)
                val cx = x * cell + cell / 2
                val cy = y * cell + cell / 2
                val radius = cell * 0.4f
                when (cellVal) {
                    GameBoard.Cell.BLACK -> canvas.drawCircle(cx, cy, radius, blackPaint)
                    GameBoard.Cell.WHITE -> canvas.drawCircle(cx, cy, radius, whitePaint)
                    else -> {}
                }
            }
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
            val cellSize = minOf(width, height) / 8f
            val x = (event.x / cellSize).toInt()
            val y = (event.y / cellSize).toInt()
            if (x in 0..7 && y in 0..7) {
                onCellTap?.invoke(x, y)
            }
            return true
        }
        return super.onTouchEvent(event)
    }
}
