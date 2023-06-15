package views.models

import javafx.beans.property.ReadOnlyBooleanProperty
import javafx.beans.property.ReadOnlyObjectProperty
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleIntegerProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.collections.FXCollections
import tictactoe.ComputerPlayer
import tictactoe.IPlayer
import tictactoe.ITicTacToe
import tictactoe.TicTacToe

object GameYield : Exception()

enum class TicTacToeCell(val index: Int) {
    CELL_1(1), CELL_2(2), CELL_3(3),
    CELL_4(4), CELL_5(5), CELL_6(6),
    CELL_7(7), CELL_8(8), CELL_9(9);
}

class GuiPlayer(
    override val id: Int,
    override val name: String,
) : IPlayer {
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
        TicTacToeCell.CELL_1 to "    ",
        TicTacToeCell.CELL_2 to "    ",
        TicTacToeCell.CELL_3 to "    ",
        TicTacToeCell.CELL_4 to "    ",
        TicTacToeCell.CELL_5 to "    ",
        TicTacToeCell.CELL_6 to "    ",
        TicTacToeCell.CELL_7 to "    ",
        TicTacToeCell.CELL_8 to "    ",
        TicTacToeCell.CELL_9 to "    ",
    ))

    private val currentPlayer = SimpleObjectProperty(player1)
    fun getCurrentPlayer() = currentPlayer.get()
    fun currentPlayerProperty(): ReadOnlyObjectProperty<IPlayer> = currentPlayer

    private val isGameFinished = SimpleBooleanProperty(false)
    fun getIsGameFinished() = isGameFinished.get()
    fun isGameFinishedProperty(): ReadOnlyBooleanProperty = isGameFinished

    fun select(cell: TicTacToeCell) {
        (getCurrentPlayer() as? GuiPlayer)?.select(cell, game)
        currentPlayer.set(if (currentPlayer.get() == player1) player2 else player1)
        updateBoard()
        loop()
    }

    fun start() {
        loop()
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
        }
    }
}
