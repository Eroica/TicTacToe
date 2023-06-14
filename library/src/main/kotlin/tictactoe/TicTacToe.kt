package tictactoe

import Database
import org.json.JSONArray
import org.json.JSONObject

object InvalidTurn: Exception()
object InvalidPlayer: Exception()
object GameDraw: Exception()
class GameEnd(val winner: IPlayer, val loser: IPlayer): Exception()

interface ITicTacToe {
    val player1: IPlayer
    val player2: IPlayer
    var currentPlayer: IPlayer
    val board: Array<Int>

    fun serialize(): JSONObject
    fun checkGameEnd()
    fun turnFor(player: IPlayer, position: Int)
    fun availableCells(): List<Int>
    fun finish() = Unit
}

class TicTacToe(
    override val player1: IPlayer,
    override val player2: IPlayer,
    override var currentPlayer: IPlayer,
    override val board: Array<Int>
) : ITicTacToe {
    companion object {
        fun hasWon(player: Int, board: Array<Int>): Boolean {
            return arrayOf(0, 1, 2).all { board[it] == player }
                || arrayOf(3, 4, 5).all { board[it] == player }
                || arrayOf(6, 7, 8).all { board[it] == player }
                || arrayOf(0, 3, 6).all { board[it] == player }
                || arrayOf(1, 4, 7).all { board[it] == player }
                || arrayOf(2, 5, 8).all { board[it] == player }
                || arrayOf(0, 4, 8).all { board[it] == player }
                || arrayOf(2, 4, 6).all { board[it] == player }
        }
    }

    /* Constructor for new games (no cell set) */
    constructor(player1: IPlayer, player2: IPlayer): this(
        player1,
        player2,
        player1,
        /* 0 | 1 | 2
         * 3 | 4 | 5
         * 6 | 7 | 8 */
        arrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0)
    )

    private val playerSigns = mapOf(
        player1 to 1,
        player2 to 2
    )

    override fun serialize(): JSONObject {
        return JSONObject(mapOf(
            "player" to playerSigns.getValue(currentPlayer),
            "board" to JSONArray(board))
        )
    }

    override fun checkGameEnd() {
        if (hasWon(playerSigns.getValue(player1), board)) {
            throw GameEnd(player1, player2)
        } else if (hasWon(playerSigns.getValue(player2), board)) {
            throw GameEnd(player2, player1)
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

        currentPlayer = if (currentPlayer == player1) player2 else player1
    }

    override fun availableCells(): List<Int> {
        return (1..9).filter { board[it - 1] == 0 }
    }

    private fun isDraw(): Boolean {
        /* Simply check whether every cell has been set, this means that isDraw() should be called
         * after checking each player's win condition. */
        return board.all { it != 0 }
    }
}

class PersistedTicTacToe(
    private val playerId: Int,
    private val database: Database,
    private val game: ITicTacToe,
    private val persistedGames: PersistedGames,
    private val leaderboard: Leaderboard
) : ITicTacToe by game {
    override fun turnFor(player: IPlayer, position: Int) {
        try {
            game.turnFor(player, position)
        } catch (e: GameEnd) {
            leaderboard[e.winner.id].wins += 1
            leaderboard[e.loser.id].wins -= 1
            throw e
        }

        database.statement("""INSERT OR IGNORE INTO game (id, player_id, state) VALUES (?, ?, ?)""").use {
            it.setInt(1, playerId)
            it.setInt(2, playerId)
            it.setString(3, game.serialize().toString())
            it.executeUpdate()
        }
        database.statement("""UPDATE game SET state=? WHERE player_id=?""").use {
            it.setString(1, game.serialize().toString())
            it.setInt(2, playerId)
            it.executeUpdate()
        }
    }

    override fun finish() {
        super.finish()
        persistedGames.delete(playerId)
    }
}

data class GameState(val currentPlayer: Int, val board: Array<Int>)

class PersistedGames(
    private val database: Database
) {
    operator fun contains(playerId: Int): Boolean {
        return database.statement("""SELECT EXISTS(SELECT 1 FROM game WHERE player_id=?)""").use {
            it.setInt(1, playerId)
            it.executeQuery().use {
                it.getInt(1) == 1
            }
        }
    }

    operator fun get(playerId: Int): GameState {
        return database.statement("""SELECT state FROM game WHERE player_id=?""").use {
            it.setInt(1, playerId)
            it.executeQuery().use {
                val jsonState = JSONObject(it.getString(1))
                GameState(
                    jsonState.getInt("player"),
                    jsonState.getJSONArray("board").map { it.toString().toInt() }.toTypedArray()
                )
            }
        }
    }

    fun delete(playerId: Int) {
        database.statement("""DELETE FROM game WHERE player_id=?""").use {
            it.setInt(1, playerId)
            it.executeUpdate()
        }
    }
}
