import org.junit.jupiter.api.*
import tictactoe.*

private class DummyPlayer : IPlayer {
    override val name = "Dummy"

    override fun turn(game: ITicTacToe) {}
}

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class TicTacToeTest {
    private val player = DummyPlayer()
    private val ticTacToe = TicTacToe(player, DummyPlayer())

    @Test
    fun `Cannot set cell which is already set`() {
        ticTacToe.turnFor(player, 1)
        assertThrows<InvalidTurn> { ticTacToe.turnFor(player, 1) }
    }
}
