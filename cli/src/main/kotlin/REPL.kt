import tictactoe.*

class SetupRepl(
    private val games: PersistedGames,
    private val database: Database
) {
    fun setup(): CliTicTacToe {
        println("Choose Player #1:")
        val player1 = LoginRepl(database).login(1)

        var player2: IPlayer
        while (true) {
            println("Choose Player #2:")
            player2 = LoginRepl(database).login(2)

            /* Continue if the two players are different. Note that ComputerPlayer is compared
             * by reference, so they are always unequal here, and can play against each other */
            if (player2 != player1) {
                break
            }
        }

        println("Tic-tac-toe battle between ${player1.name} and ${player2.name}.")

        return when (player1) {
            is CliPlayer -> {
                if (player1.id in games) {
                    println("There is a previous game for player ${player1.name}. Restart it? (y/n)")

                    when (readlnOrNull()) {
                        "y", "Y" -> CliTicTacToe(
                            PersistedTicTacToe(
                                player1.id,
                                database,
                                TicTacToe(player1, player2, games[player1.id]),
                                games
                            )
                        )
                        else -> CliTicTacToe(
                            PersistedTicTacToe(
                                player1.id,
                                database,
                                TicTacToe(player1, player2),
                                games
                            )
                        )
                    }
                } else {
                    /* New game for player */
                    CliTicTacToe(
                        PersistedTicTacToe(
                            player1.id,
                            database,
                            TicTacToe(player1, player2),
                            games
                        )
                    )
                }
            }
            /* CPU against CPU game, no need to persist anything */
            else -> {
                CliTicTacToe(TicTacToe(player1, player2))
            }
        }
    }
}

class LoginRepl(database: Database) {
    private val players = PersistedPlayers(database)

    fun login(playerIndex: Int): IPlayer {
        while (players.all().isEmpty()) {
            println("There are no players yet.")
            create()
        }

        while (true) {
            println("Log in as a player or create a new player:")
            println(players.table())
            println("c: (C)PU")
            println("n: Create (n)ew player")

            when (val playerInput = readlnOrNull()) {
                "n" -> create()
                "c" -> return ComputerPlayer(playerIndex)
                else -> {
                    playerInput?.toInt()?.let {
                        if (it in players.all()) {
                            return CliPlayer(it, players[it])
                        }
                    }
                }
            }
        }
    }

    private fun create() {
        println("Create a new player (name):")
        val name = readlnOrNull() ?: "Player 1"
        players.create(name)
    }
}

class TicTacToeRepl(
    private val game: CliTicTacToe,
) {
    fun start() {
        try {
            while (true) {
                println("Current board:")
                println("")
                println(game.draw())
                println("")
                println("Current player is ${game.player1.name}.")
                game.player1.turn(game)
                println("")

                println("Current board:")
                println("")
                println(game.draw())
                println("")
                println("Current player is ${game.player2.name}.")
                game.player2.turn(game)
                println("")
            }
        } catch (e: GameEnd) {
            println("Current board:")
            println("")
            println(game.draw())
            println("")
            println("${e.winner.name} won!")
            game.finish()

        } catch (e: GameDraw) {
            println("Current board:")
            println("")
            println(game.draw())
            println("")
            println("The game has ended in a draw.")
            game.finish()
        }
    }
}
