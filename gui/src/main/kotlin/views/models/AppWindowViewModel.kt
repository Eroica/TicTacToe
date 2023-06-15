package views.models

import javafx.collections.FXCollections
import tictactoe.IPlayer
import tictactoe.PersistedPlayers

class AppWindowViewModel(persistedPlayers: PersistedPlayers) {
    /* This currently reads the database twice (instead of fetching ID and name together), but
     * unfortunately `all` is currently designed to only return the ID. */
    val players = FXCollections.observableArrayList<IPlayer>(persistedPlayers.all().map {
        GuiPlayer(it, persistedPlayers[it])
    })

    init {
        players.add(0, GuiPlayer(2, "Guest"))
    }
}
