import javafx.application.Application
import javafx.scene.Scene
import javafx.stage.Stage
import tictactoe.PersistedPlayers
import views.AppWindow
import views.models.AppWindowViewModel
import kotlin.io.path.Path

fun main(args: Array<String>) {
    Application.launch(TicTacToeApp::class.java, *args)
}

class TicTacToeApp : Application() {
    private lateinit var database: Database
    private lateinit var players: PersistedPlayers

    override fun init() {
        super.init()
        database = Database.at(Path(""))
        players = PersistedPlayers(database)
    }

    override fun start(primaryStage: Stage) {
        primaryStage.title = "Tic-tac-toe"
        primaryStage.minWidth = 300.0
        primaryStage.minHeight = 400.0

        primaryStage.setOnCloseRequest {
            database.close()
        }

        val scene = Scene(AppWindow(AppWindowViewModel(players)))
        primaryStage.scene = scene
        primaryStage.show()
    }
}
