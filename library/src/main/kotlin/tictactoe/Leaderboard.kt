package tictactoe

import Database
import GUEST_PLAYER_ID

class LeaderboardPlayer(
    private val id: Int,
    wins: Int,
    private val database: Database
) {
    var wins: Int = wins
        set(value) {
            field = value
            database.statement("""UPDATE leaderboard SET wins=? WHERE player_id=?""").use {
                it.setInt(1, value)
                it.setInt(2, id)
            }
        }
}

class Leaderboard(private val database: Database) {
    operator fun get(playerId: Int): LeaderboardPlayer {
        if (1 != database.statement("""SELECT EXISTS(SELECT 1 FROM leaderboard WHERE player_id=?)""").use {
            it.setInt(1, playerId)
            it.executeQuery().use { it.getInt(1) }
        }) {
            throw InvalidPlayer
        }

        val wins = database.statement("""SELECT wins FROM leaderboard WHERE player_id=?""").use {
            it.setInt(1, playerId)
            it.executeQuery().use {
                it.getInt(1)
            }
        }

        return LeaderboardPlayer(playerId, wins, database)
    }

    override fun toString(): String {
        val players = database.statement("""SELECT id, name, wins FROM player LEFT JOIN leaderboard l on player.id = l.player_id""").use {
            it.executeQuery().use {
                generateSequence {
                    if (it.next()) Triple(it.getInt(1), it.getString(2), it.getInt(3)) else null
                }.toList()
            }
        }.associateBy { it.first }

        /* Skip the guest player */
        return players.filter { it.key != GUEST_PLAYER_ID }
            .map { "(${it.value.first}) ${it.value.second}: ${it.value.third}" }
            .joinToString("\n")
    }
}
