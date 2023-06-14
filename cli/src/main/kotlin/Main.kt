import tictactoe.Leaderboard
import tictactoe.PersistedGames
import kotlin.io.path.Path

fun main() {
    println("Welcome to a game of Tic-tac-toe!")

    /* Create database in current working directory */
    Database.at(Path("")).use { database ->
        val previousGames = PersistedGames(database)
        val leaderboard = Leaderboard(database)

        while (true) {
            println("")
            println("What would you like to do?")
            println("p: (P)lay game")
            println("l: Show (l)eaderboard")
            println("q: (Q)uit")

            when (readlnOrNull()) {
                "p", "P" -> {
                    val game = SetupRepl(previousGames, leaderboard, database).setup()
                    TicTacToeRepl(game).start()
                }
                "l", "L" -> println(leaderboard)
                "q", "Q" -> break
            }
        }
    }
}
