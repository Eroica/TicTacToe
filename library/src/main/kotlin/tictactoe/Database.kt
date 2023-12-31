import org.sqlite.SQLiteConnection
import tictactoe.PersistedPlayers
import java.nio.file.Path
import java.sql.Connection
import java.sql.DriverManager
import java.sql.PreparedStatement

private const val DB_NAME = "tictactoe.db"
const val CPU_PLAYER_ID = 1
const val GUEST_PLAYER_ID = 2

class Database private constructor(uri: String): AutoCloseable {
    companion object {
        fun at(dir: Path) = Database(dir.resolve(DB_NAME).toUri().toString())
        fun memory() = Database(":memory:")
    }

    private val connection = DriverManager.getConnection("jdbc:sqlite:${uri}?foreign_keys=on") as SQLiteConnection

    init {
        when (connection.getVersion()) {
            0 -> initialize()
            /* Place for migrations */
        }
    }

    override fun close() {
        connection.close()
    }

    fun statement(sql: String): PreparedStatement {
        return connection.prepareStatement(sql)
    }

    private fun initialize() {
        connection.createStatement().use {
            it.execute("""
                CREATE TABLE player (
                    id INTEGER PRIMARY KEY,
                    name VARCHAR NOT NULL
                )
            """.trimIndent())
        }
        connection.createStatement().use {
            it.execute("""
                CREATE TABLE game (
                    id INTEGER PRIMARY KEY,
                    player_id INTEGER NOT NULL,
                    state VARCHAR NOT NULL,
                    updated_on DATETIME default (datetime(current_timestamp)),
                    FOREIGN KEY (player_id) REFERENCES player (id)
                        ON DELETE CASCADE
                )
            """.trimIndent())
        }
        connection.createStatement().use {
            it.execute("""
                CREATE TABLE leaderboard (
                    player_id INTEGER NOT NULL,
                    wins INTEGER NOT NULL DEFAULT 0,
                    FOREIGN KEY (player_id) REFERENCES player (id)
                        ON DELETE CASCADE
                )
            """.trimIndent())
        }

        /* This currently adds 2 default players with ID 1 and 2, although these IDs are
         * theoretically not enforced and only relies on the first SQLite key being 1. */
        val players = PersistedPlayers(this)
        players.create("CPU")
        players.create("Guest")
        connection.setVersion(1)
    }
}

private fun Connection.getVersion(): Int {
    return createStatement().use {
        it.executeQuery("""PRAGMA user_version""").use {
            it.getInt(1)
        }
    }
}

private fun Connection.setVersion(version: Int) {
    createStatement().use {
        it.executeUpdate(String.format("PRAGMA user_version = %d;", version))
    }
}
