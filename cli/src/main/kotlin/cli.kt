import tictactoe.IPlayer
import tictactoe.ITicTacToe
import tictactoe.InvalidTurn

class CliTicTacToe(private val game: ITicTacToe) : ITicTacToe by game {
    val signs = mapOf(
        1 to "x",
        2 to "o"
    )

    fun draw(): String {
        return """
                 |     |
              ${positionSign(1)}  |  ${positionSign(2)}  |  ${positionSign(3)}
            _____|_____|_____
                 |     |
              ${positionSign(4)}  |  ${positionSign(5)}  |  ${positionSign(6)}
            _____|_____|_____
                 |     |
              ${positionSign(7)}  |  ${positionSign(8)}  |  ${positionSign(9)}
                 |     |
        """.trimIndent()
    }

    private fun positionSign(position: Int): String {
        return signs[game.board[position - 1]] ?: "$position"
    }
}

class CliPlayer(override val name: String = "Guest") : IPlayer {
    override fun turn(game: ITicTacToe) {
        turn(game as CliTicTacToe)
    }

    private fun turn(game: CliTicTacToe) {
        println("Current player is $name.")

        while (true) {
            println("Choose a cell (${game.availableCells().joinToString()}):")

            try {
                val cell = readlnOrNull()?.toInt()
                cell?.let { game.turnFor(this, it) }
                break
            } catch (_: InvalidTurn) {
                println("This is not a valid cell!")
            } catch (_: NumberFormatException) {
                println("This is not a valid cell!")
            }
        }
    }
}
