import tictactoe.GameDraw
import tictactoe.GameEnd

class REPL(private val game: CliTicTacToe) {
    fun start() {
        try {
            while (true) {
                println("Current board:")
                println("")
                println(game.draw())
                println("")
                game.player1.turn(game)
                println("")

                println("Current board:")
                println("")
                println(game.draw())
                println("")
                game.player2.turn(game)
                println("")
            }
        } catch (e: GameEnd) {
            println("Current board:")
            println("")
            println(game.draw())
            println("")
            println("${e.winner.name} won!")
        } catch (e: GameDraw) {
            println("Current board:")
            println("")
            println(game.draw())
            println("")
            println("The game has ended in a draw.")
        }
    }
}
