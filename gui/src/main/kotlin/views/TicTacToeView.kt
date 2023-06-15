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

class TicTacToeView() : VBox() {
    @FXML
    private lateinit var gridPane: GridPane

    private val viewModel = SimpleObjectProperty<TicTacToeViewModel?>(null)
    fun getViewModel() = viewModel.get()
    fun setViewModel(value: TicTacToeViewModel) = viewModel.set(value)
    fun viewModelProperty(): SimpleObjectProperty<TicTacToeViewModel?> = viewModel

    private val isInactive = viewModel.isNull()
    fun getIsActive() = isInactive.get()
    fun isActiveProperty() = isInactive

    private val currentPlayer = Bindings.`when`(viewModel.isNull)
        .then("")
        .otherwise(Bindings.selectString(viewModel, "currentPlayer", "name").concat("’s turn."))

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
                    textProperty().bind(
                        Bindings.`when`(viewModel.isNull)
                            .then("")
                            .otherwise(Bindings.createStringBinding({
                                getViewModel()?.cells?.get(cell)
                            }, viewModel, Bindings.select<ObservableMap<TicTacToeCell, String>>(viewModel, "cells")))
                    )
                }

                gridPane.add(button, i, j)
            }
        }
    }

    @FXML
    private fun onCellClick(event: ActionEvent) {
        val cell = event.target as Button
        getViewModel()?.select(cell.userData as TicTacToeCell)
        cell.isDisable = true
    }
}
