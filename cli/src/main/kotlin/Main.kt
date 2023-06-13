import tictactoe.TicTacToe

fun main() {
    val game = CliTicTacToe(TicTacToe(CliPlayer("Player 1"), CliPlayer("Player 2")))
    val repl = REPL(game)

    println("Welcome to a game of Tic-tac-toe!")
    repl.start()
}
