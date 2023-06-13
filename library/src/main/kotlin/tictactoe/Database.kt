import org.sqlite.SQLiteConnection
import java.nio.file.Path
import java.sql.Connection
import java.sql.DriverManager

class Database(private val home: Path, uri: String) {
    private val connection = DriverManager.getConnection("jdbc:sqlite:${uri}?foreign_keys=on") as SQLiteConnection

    init {
        when (connection.getVersion()) {
            0 -> initialize()
            /* Place for migrations */
        }
    }

    private fun initialize() {
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
