import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import tictactoe.PersistedPlayers
import kotlin.test.assertEquals

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class DatabaseTests {
    private lateinit var database: Database
    private lateinit var players: PersistedPlayers

    @BeforeEach
    fun setupDb() {
        database = Database.memory()
        players = PersistedPlayers(database)
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
}
