package tictactoe

import Database

interface IPlayer {
    val name: String
    fun turn(game: ITicTacToe)
}

class ComputerPlayer : IPlayer {
    override val name: String = "CPU"

    override fun turn(game: ITicTacToe) {
        TODO("Not yet implemented")
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
        return database.statement("""INSERT INTO player (name) VALUES (?)""").use {
            it.setString(1, name)
            it.executeUpdate()
            it.generatedKeys.use {
                it.getInt(1)
            }
        }
    }

    fun all(): List<Int> {
        return database.statement("""SELECT id FROM player""").use {
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
        val players = database.statement("SELECT id, name FROM player").use {
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
