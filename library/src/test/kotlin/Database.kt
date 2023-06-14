import org.junit.jupiter.api.*
import tictactoe.Leaderboard
import tictactoe.PersistedPlayers
import kotlin.test.assertEquals

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class DatabaseTests {
    private lateinit var database: Database
    private lateinit var players: PersistedPlayers
    private lateinit var leaderboard: Leaderboard

    @BeforeEach
    fun setupDb() {
        database = Database.memory()
        players = PersistedPlayers(database)
        leaderboard = Leaderboard(database)
    }

    @AfterEach
    fun closeDb() {
        database.close()
    }

    @Test
    fun `Adding new player`() {
        assertEquals(0, players.all().size)
        players.create("Test player")
        assertEquals(1, players.all().size)
    }

    @Test
    fun `Adding new player adds entry to leaderboard`() {
        val id = players.create("Test player")
        assertDoesNotThrow { leaderboard[id] }
    }
}
