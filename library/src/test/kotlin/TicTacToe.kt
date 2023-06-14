import org.junit.jupiter.api.*
import tictactoe.*
import kotlin.test.assertEquals

private class DummyPlayer(
    override val id: Int,
    override val name: String
) : IPlayer {
    override fun turn(game: ITicTacToe) {}
}

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class TicTacToeTest {
    private lateinit var player1: DummyPlayer
    private lateinit var player2: DummyPlayer
    private var playerId = 0
    private lateinit var ticTacToe: ITicTacToe

    private lateinit var database: Database
    private lateinit var players: PersistedPlayers
    private lateinit var games: PersistedGames
    private lateinit var leaderboard: Leaderboard

    @BeforeAll
    fun setupDatabase() {
        database = Database.memory()
        players = PersistedPlayers(database)
        games = PersistedGames(database)
        leaderboard = Leaderboard(database)
        playerId = players.create("Player 1")
        val player2Id = players.create("Player 2")
        player1 = DummyPlayer(playerId, "")
        player2 = DummyPlayer(player2Id, "")
    }

    @BeforeEach
    fun setup() {
        ticTacToe = PersistedTicTacToe(
            playerId,
            database,
            TicTacToe(player1, player2),
            games,
            leaderboard
        )
    }

    @AfterAll
    fun closeDatabase() {
        database.close()
    }

    @Test
    fun `Cannot set cell which is already set`() {
        ticTacToe.turnFor(player1, 1)
        assertThrows<InvalidTurn> { ticTacToe.turnFor(player1, 1) }
    }

    @Test
    fun `Winning a game increases leaderboard score`() {
        try {
            ticTacToe.turnFor(player2, 1)
            ticTacToe.turnFor(player1, 9)
            ticTacToe.turnFor(player2, 2)
            ticTacToe.turnFor(player1, 8)
            ticTacToe.turnFor(player2, 3)
        } catch (e: GameEnd) {
            assertEquals(1, leaderboard[e.winner.id].wins)
            assertEquals(-1, leaderboard[e.loser.id].wins)
        }
    }
}
