import tictactoe.PersistedGames
import kotlin.io.path.Path

fun main() {
    println("Welcome to a game of Tic-tac-toe!")

    Database(Path(""), "tictactoe.db").use { database ->
        val previousGames = PersistedGames(database)
        val game = SetupRepl(previousGames, database).setup()

        TicTacToeRepl(game).start()
    }
}
