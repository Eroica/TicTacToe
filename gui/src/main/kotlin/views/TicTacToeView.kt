package views

import javafx.beans.binding.Bindings
import javafx.beans.property.SimpleObjectProperty
import javafx.collections.FXCollections
import javafx.collections.ObservableMap
import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.scene.control.Button
import javafx.scene.layout.GridPane
import javafx.scene.layout.VBox
import views.models.GuiPlayer
import views.models.TicTacToeCell
import views.models.TicTacToeViewModel

class TicTacToeView(val viewModel: TicTacToeViewModel) : VBox() {
    @FXML
    private lateinit var gridPane: GridPane

    private val currentPlayer = Bindings.selectString(viewModel, "currentPlayer", "name").concat("’s turn.")
    fun getCurrentPlayer() = currentPlayer.get()
    fun currentPlayerProperty() = currentPlayer

    init {
        FXMLLoader(javaClass.getResource("TicTacToeView.fxml")).apply {
            setRoot(this@TicTacToeView)
            setController(this@TicTacToeView)
            load()
        }

        (0..2).forEach { i ->
            (0..2).forEach { j ->
                val cell = TicTacToeCell.valueOf("CELL_${3 * i + j + 1}")
                val button = Button("").apply {
                    userData = cell
                    setOnAction { onCellClick(it) }
                    textProperty().bind(Bindings.stringValueAt(viewModel.cells, cell).map {
                        when (it) {
                            "1" -> "❌"
                            "2" -> "⭕"
                            else -> ""
                        }
                    })
                    disableProperty().bind(Bindings.stringValueAt(viewModel.cells, cell).isNotEqualTo("0"))
                }

                gridPane.add(button, j, i)
            }
        }
    }

    @FXML
    private fun onCellClick(event: ActionEvent) {
        val cell = event.target as Button
        viewModel.select(cell.userData as TicTacToeCell)
    }
}
