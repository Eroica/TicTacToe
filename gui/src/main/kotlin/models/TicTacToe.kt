package models

object GameYield : Exception()

enum class TicTacToeCell(val index: Int) {
    CELL_1(1), CELL_2(2), CELL_3(3),
    CELL_4(4), CELL_5(5), CELL_6(6),
    CELL_7(7), CELL_8(8), CELL_9(9);
}
