package views.models

import javafx.beans.property.SimpleObjectProperty
import javafx.collections.FXCollections
import tictactoe.IPlayer
import tictactoe.PersistedPlayers

class AppWindowViewModel(persistedPlayers: PersistedPlayers) {
    val players = FXCollections.observableArrayList<IPlayer>(persistedPlayers.all().map {
        GuiPlayer(it, persistedPlayers[it])
    })

    init {
        players.add(0, GuiPlayer(2, "Guest"))
    }
}
