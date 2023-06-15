package views.models

import javafx.beans.property.ReadOnlyBooleanProperty
import javafx.beans.property.ReadOnlyObjectProperty
import javafx.beans.property.ReadOnlyStringProperty
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import javafx.collections.FXCollections
import models.GameYield
import models.TicTacToeCell
import tictactoe.*

class GuiPlayer(
    override val id: Int,
    override val name: String,
) : IPlayer {
    /* When playing the game in a GUI, the game loop is "event-based" instead of relying on
     * polling (i.e. request the user's next move). So in this case, `turn` basically tells the
     * game loop to do nothing (and wait) until it can be resumed with another interaction. */
    override fun turn(game: ITicTacToe) {
        throw GameYield
    }

    fun select(cell: TicTacToeCell, game: ITicTacToe) {
        game.turnFor(this, cell.index)
    }
}

class TicTacToeViewModel(
    private val player1: IPlayer,
    private val player2: IPlayer,
    private val game: ITicTacToe
) {
    val cells = FXCollections.observableMap(mapOf(
        TicTacToeCell.CELL_1 to "0",
        TicTacToeCell.CELL_2 to "0",
        TicTacToeCell.CELL_3 to "0",
        TicTacToeCell.CELL_4 to "0",
        TicTacToeCell.CELL_5 to "0",
        TicTacToeCell.CELL_6 to "0",
        TicTacToeCell.CELL_7 to "0",
        TicTacToeCell.CELL_8 to "0",
        TicTacToeCell.CELL_9 to "0",
    ))

    private val currentPlayer = SimpleObjectProperty(player1)
    fun getCurrentPlayer() = currentPlayer.get()
    fun currentPlayerProperty(): ReadOnlyObjectProperty<IPlayer> = currentPlayer

    private val isGameFinished = SimpleBooleanProperty(false)
    fun getIsGameFinished() = isGameFinished.get()
    fun isGameFinishedProperty(): ReadOnlyBooleanProperty = isGameFinished

    private val gameEndMessage = SimpleStringProperty("")
    fun getGameEndMessage() = gameEndMessage.get()
    fun gameEndMessageProperty(): ReadOnlyStringProperty = gameEndMessage

    fun select(cell: TicTacToeCell) {
        try {
            (getCurrentPlayer() as? GuiPlayer)?.select(cell, game)
            currentPlayer.set(if (currentPlayer.get() == player1) player2 else player1)
            updateBoard()
            loop()
        } catch (e: GameEndState) {
            setGameEnd(e)
        }
    }

    fun start() {
        loop()
    }

    private fun setGameEnd(e: GameEndState) {
        updateBoard()
        isGameFinished.set(true)
        gameEndMessage.set(e.message)
    }

    private fun updateBoard() {
        TicTacToeCell.values().forEach {
            cells[it] = game.board[it.index - 1].toString()
        }
    }

    private fun loop() {
        try {
            while (true) {
                currentPlayer.get().turn(game)
                updateBoard()
                currentPlayer.set(if (currentPlayer.get() == player1) player2 else player1)
            }
        } catch (_: GameYield) {
        } catch (e: GameEndState) {
            setGameEnd(e)
        }
    }
}
