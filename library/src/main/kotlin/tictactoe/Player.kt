package tictactoe

import Database
import GUEST_PLAYER_ID

interface IPlayer {
    val id: Int
    val name: String
    fun turn(game: ITicTacToe)
}

/* CPU player adapted from https://github.com/mariscallzn/KotlinTicTacToe */
class ComputerPlayer(private val playerId: Int) : IPlayer {
    override val id: Int = 1
    override val name: String = "CPU"

    private val opposingPlayer = if (playerId == 1) 2 else 1
    private var cells = arrayOf(
        arrayOf(0, 0, 0),
        arrayOf(0, 0, 0),
        arrayOf(0, 0, 0),
    )

    override fun turn(game: ITicTacToe) {
        setCells(game)
        val position = minimax(7, playerId)
        game.turnFor(this, 3 * position[1] + position[2] + 1)
    }

    private fun setCells(game: ITicTacToe) {
        cells = arrayOf(
            arrayOf(game.board[0], game.board[1], game.board[2]),
            arrayOf(game.board[3], game.board[4], game.board[5]),
            arrayOf(game.board[6], game.board[7], game.board[8])
        )
    }

    private fun minimax(depth: Int, player: Int): Array<Int> {
        val nextMoves: MutableList<Array<Int>> = generateMoves()

        var bestScore = when (playerId) {
            player -> Int.MIN_VALUE
            else -> Int.MAX_VALUE
        }
        var currentScore: Int
        var bestRow = -1
        var bestCol = -1

        if (nextMoves.isEmpty() || depth == 0) {
            bestScore = evaluate()
        } else {
            nextMoves.forEach {
                cells[it[0]][it[1]] = player

                if (player == playerId) {
                    currentScore = minimax(depth - 1, opposingPlayer)[0]
                    if (currentScore > bestScore) {
                        bestScore = currentScore
                        bestRow = it[0]
                        bestCol = it[1]
                    }
                } else {
                    currentScore = minimax(depth - 1, playerId)[0]
                    if (currentScore < bestScore) {
                        bestScore = currentScore
                        bestRow = it[0]
                        bestCol = it[1]
                    }
                }
                cells[it[0]][it[1]] = 0
            }
        }

        return arrayOf(bestScore, bestRow, bestCol)
    }

    private fun generateMoves(): MutableList<Array<Int>> {
        val nextMoves: MutableList<Array<Int>> = mutableListOf()

        val linearBoard = arrayOf(
            cells[0][0], cells[0][1], cells[0][2],
            cells[1][0], cells[1][1], cells[1][2],
            cells[2][0], cells[2][1], cells[2][2]
        )

        if (TicTacToe.hasWon(playerId, linearBoard) || TicTacToe.hasWon(opposingPlayer, linearBoard)) {
            return nextMoves
        }

        for (row in 0 until 3) {
            (0 until 3)
                .asSequence()
                .filter { cells[row][it] == 0 }
                .forEach { nextMoves.add(arrayOf(row, it)) }
        }
        return nextMoves
    }

    private fun evaluate(): Int {
        var score = 0
        score += evaluateLine(0, 0, 0, 1, 0, 2)  // row 0
        score += evaluateLine(1, 0, 1, 1, 1, 2)  // row 1
        score += evaluateLine(2, 0, 2, 1, 2, 2)  // row 2
        score += evaluateLine(0, 0, 1, 0, 2, 0)  // col 0
        score += evaluateLine(0, 1, 1, 1, 2, 1)  // col 1
        score += evaluateLine(0, 2, 1, 2, 2, 2)  // col 2
        score += evaluateLine(0, 0, 1, 1, 2, 2)  // diagonal
        score += evaluateLine(0, 2, 1, 1, 2, 0)  // alternate diagonal
        return score
    }

    private fun evaluateLine(row1: Int, col1: Int, row2: Int, col2: Int, row3: Int, col3: Int): Int {
        var score = 0

        if (cells[row1][col1] == playerId) {
            score = 1
        } else if (cells[row1][col1] == opposingPlayer) {
            score = -1
        }

        if (cells[row2][col2] == playerId) {
            if (score == 1) {
                score = 10
            } else if (score == -1) {
                return 0
            } else {
                score = 1
            }
        } else if (cells[row2][col2] == opposingPlayer) {
            if (score == -1) {
                score = -10
            } else if (score == 1) {
                return 0
            } else {
                score = -1
            }
        }

        if (cells[row3][col3] == playerId) {
            if (score > 0) {
                score *= 10
            } else if (score < 0) {
                return 0
            } else {
                score = 1
            }
        } else if (cells[row3][col3] == opposingPlayer) {
            if (score < 0) {
                score *= 10
            } else if (score > 1) {
                return 0
            } else {
                score = -1
            }
        }

        return score
    }
}

class PersistedPlayers(private val database: Database) {
    operator fun get(id: Int): String {
        return database.statement("""SELECT name FROM player WHERE id=?""").use {
            it.setInt(1, id)
            it.executeQuery().use {
                it.getString(1)
            }
        }
    }

    fun create(name: String): Int {
        val newId = database.statement("""INSERT INTO player (name) VALUES (?)""").use {
            it.setString(1, name)
            it.executeUpdate()
            it.generatedKeys.use {
                it.getInt(1)
            }
        }

        database.statement("""INSERT INTO leaderboard (player_id, wins) VALUES (?, ?)""").use {
            it.setInt(1, newId)
            it.setInt(2, 0)
            it.executeUpdate()
        }

        return newId
    }

    fun all(): List<Int> {
        /* CPU and a "Guest" player is part of the player table by default, so skip them here */
        return database.statement("""SELECT id FROM player WHERE id>?""").use {
            it.setInt(1, GUEST_PLAYER_ID)
            it.executeQuery().use {
                it.use {
                    generateSequence {
                        if (it.next()) it.getInt(1) else null
                    }.toList()
                }
            }
        }
    }

    fun table(): String {
        val players = database.statement("SELECT id, name FROM player WHERE id>?").use {
            it.setInt(1, GUEST_PLAYER_ID)
            it.executeQuery().use {
                it.use {
                    generateSequence {
                        if (it.next()) Pair(it.getInt(1), it.getString(2)) else null
                    }.toMap()
                }
            }
        }

        return players.map { "${it.key}: ${it.value}" }.joinToString("\n")
    }
}
