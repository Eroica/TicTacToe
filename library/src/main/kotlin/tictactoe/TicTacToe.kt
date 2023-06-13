package tictactoe

import Database
import org.json.JSONArray
import org.json.JSONObject

object InvalidTurn: Exception()
object InvalidPlayer: Exception()
object GameDraw: Exception()
class GameEnd(val winner: IPlayer): Exception()

interface ITicTacToe {
    val player1: IPlayer
    val player2: IPlayer
    val board: Array<Int>

    fun serialize(): JSONObject
    fun checkGameEnd()
    fun turnFor(player: IPlayer, position: Int)
    fun availableCells(): List<Int>
}

class TicTacToe(
    override val player1: IPlayer,
    override val player2: IPlayer
) : ITicTacToe {
    val playerSigns = mapOf(
        player1 to 1,
        player2 to 2
    )

    /* 0 | 1 | 2
     * 3 | 4 | 5
     * 6 | 7 | 8 */
    override val board = arrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0)

    override fun serialize(): JSONObject {
        return JSONObject(mapOf(board to JSONArray(board)))
    }

    override fun checkGameEnd() {
        if (hasWon(player1)) {
            throw GameEnd(player1)
        } else if (hasWon(player2)) {
            throw GameEnd(player2)
        } else if (isDraw()) {
            throw GameDraw
        }
    }

    override fun turnFor(player: IPlayer, position: Int) {
        if (position !in availableCells()) {
            throw InvalidTurn
        }

        board[position - 1] = playerSigns[player] ?: throw InvalidTurn
        checkGameEnd()
    }

    override fun availableCells(): List<Int> {
        return (1..9).filter { board[it - 1] == 0 }
    }

    private fun isDraw(): Boolean {
        /* Simply check whether every cell has been set, this means that isDraw() should be called
         * after checking each player's win condition. */
        return board.all { it != 0 }
    }

    private fun hasWon(player: IPlayer): Boolean {
        val sign = playerSigns[player]
        return arrayOf(0, 1, 2).all { board[it] == sign }
            || arrayOf(3, 4, 5).all { board[it] == sign }
            || arrayOf(6, 7, 8).all { board[it] == sign }
            || arrayOf(0, 3, 6).all { board[it] == sign }
            || arrayOf(1, 4, 7).all { board[it] == sign }
            || arrayOf(2, 5, 8).all { board[it] == sign }
            || arrayOf(0, 4, 8).all { board[it] == sign }
            || arrayOf(2, 4, 6).all { board[it] == sign }
    }
}

class PersistedTicTacToe(
    private val game: ITicTacToe,
    private val database: Database
) : ITicTacToe by game {
}
