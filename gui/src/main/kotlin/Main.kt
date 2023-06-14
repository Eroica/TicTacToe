import javafx.application.Application
import javafx.stage.Stage

fun main(args: Array<String>) {
    Application.launch(TicTacToeApp::class.java, *args)
}

class TicTacToeApp : Application() {
    override fun init() {
        super.init()
    }

    override fun start(primaryStage: Stage) {
        primaryStage.show()
    }
}
