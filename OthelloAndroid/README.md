# OthelloAndroid

This Android project implements a simple Othello (Reversi) game. The app starts with a screen where you can choose the AI difficulty and then begins a match on an 8×8 board.

AI difficulty levels:

| Level | Algorithm |
|-------|-----------|
| Beginner | Score based |
| Easy | Score based + Minimax |
| Medium | Minimax with alpha–beta pruning |
| Hard | Score based + Minimax + alpha–beta pruning |

The board is drawn using a custom `GameBoardView` and supports tapping on cells to place a piece. The human player uses black pieces and the AI uses white. A simple score display shows the number of black and white pieces as the game progresses.

To build the project, open it with Android Studio and sync Gradle. The core game logic resides in `GameBoard.kt` and AI strategies in `AIPlayer.kt`.
