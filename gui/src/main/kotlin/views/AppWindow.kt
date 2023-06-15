package views

import javafx.beans.InvalidationListener
import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.scene.control.Button
import javafx.scene.control.ComboBox
import javafx.scene.layout.VBox
import javafx.util.StringConverter
import tictactoe.ComputerPlayer
import tictactoe.IPlayer
import tictactoe.TicTacToe
import views.models.AppWindowViewModel
import views.models.GuiPlayer
import views.models.TicTacToeViewModel

class AppWindow(val viewModel: AppWindowViewModel) : VBox() {
    @FXML
    private lateinit var player1Selection: ComboBox<IPlayer>

    @FXML
    private lateinit var player2Selection: ComboBox<IPlayer>

    @FXML
    private lateinit var startButton: Button

    private val playerSelectionListener = InvalidationListener {
        val player1 = player1Selection.value
        val player2 = player2Selection.value

        if ((player1 is ComputerPlayer && player2 is GuiPlayer)
            || (player2 is ComputerPlayer && player1 is GuiPlayer)
            || (player1 is ComputerPlayer && player2 is ComputerPlayer)
        ) {
            startButton.isDisable = false
        } else if (player1 is GuiPlayer && player2 is GuiPlayer) {
            startButton.isDisable = player1.id == player2.id
        } else {
            startButton.isDisable = true
        }
    }

    val playerConverter = object : StringConverter<IPlayer>() {
        override fun toString(`object`: IPlayer?): String {
            return `object`?.name ?: ""
        }

        override fun fromString(string: String?): IPlayer {
            TODO("Not yet implemented")
        }
    }

    init {
        FXMLLoader(javaClass.getResource("AppWindow.fxml")).apply {
            setRoot(this@AppWindow)
            setController(this@AppWindow)
            load()
        }

        player1Selection.items.addAll(viewModel.players)
        player2Selection.items.addAll(viewModel.players)
        player1Selection.valueProperty().addListener(playerSelectionListener)
        player2Selection.valueProperty().addListener(playerSelectionListener)
    }

    @FXML
    private fun onStart(event: ActionEvent) {
        startButton.isDisable = true
        player1Selection.isDisable = true
        player2Selection.isDisable = true

        val player1 = player1Selection.value
        val player2 = player2Selection.value
        val game = TicTacToe(player1, player2)
        val viewModel = TicTacToeViewModel(player1, player2, game)
        children.add(TicTacToeView(viewModel))
        viewModel.start()
    }
}
