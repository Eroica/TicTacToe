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

abstract class PersistedPlayer(
    val id: Int,
    private val database: Database,
    player: IPlayer
) : IPlayer by player
