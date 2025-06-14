# OthelloAndroid

This is a minimal Android project implementing the beginnings of an Othello (Reversi) game. It now includes a start screen where you can select the AI difficulty before starting the game.

AI difficulty levels:

| Level | Algorithm |
|-------|-----------|
| Beginner | Score based |
| Easy | Score based + Minimax |
| Medium | Minimax with alpha–beta pruning |
| Hard | Score based + Minimax + alpha–beta pruning |

To build the project, open it with Android Studio and sync Gradle. The game logic is located in `GameBoard.kt`. The AI strategies are implemented in `AIPlayer.kt`.
