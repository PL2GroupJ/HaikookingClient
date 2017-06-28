package jp.ac.ynu.pl2017.groupj.gui

import javafx.application.Application
import javafx.fxml.FXMLLoader
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.stage.Modality
import javafx.stage.Stage
import jp.ac.ynu.pl2017.groupj.gui.title.Title

/**
 * GUIのベースのクラス。Swingで言うところのJFrame。
 */
class MainApp : Application() {
    private lateinit var stage: Stage

    override fun start(primaryStage: Stage) {
        stage = primaryStage
        setPane(Title())
        stage.show()
    }

    /**
     * 受け取ったコントローラーに対応するペインをロードし、現在のステージにセットする。
     * @param controller コントローラーのインスタンス
     */
    fun setPane(controller: Any) {
        val classPath = controller.javaClass.name
        val className = controller.javaClass.simpleName
        val loader = FXMLLoader(Class.forName(classPath).getResource("$className.fxml"))
                .apply { setController(controller) }
        val parent = loader.load<Parent>()
        stage.title = className
        stage.scene = Scene(parent, width, height)

        if (controller is TransitionPane) {
            controller.setPane = this::setPane
        }
        if (controller is TransitionModalPane) {
            controller.newPane = this::newPane
        }
    }

    /**
     * 受け取ったコントローラーに対応するペインをロードし、新しいステージをモーダルで表示する。
     * @param controller コントローラーのインスタンス
     */
    fun newPane(controller: Any) {
        val classPath = controller.javaClass.name
        val className = controller.javaClass.simpleName
        val loader = FXMLLoader(Class.forName(classPath).getResource("$className.fxml"))
                .apply { setController(controller) }
        val parent = loader.load<Parent>()
        val modalStage = Stage()
        modalStage.apply {
            initModality(Modality.APPLICATION_MODAL)
            initOwner(stage)
            title = className
            scene = Scene(parent, width, height)
            show()
        }
    }

    companion object {
        val width = 450.0
        val height = 600.0
    }
}

interface TransitionPane {
    var setPane: ((Any) -> Unit)
}

interface TransitionModalPane {
    var newPane: ((Any) -> Unit)
}

fun main(args: Array<String>) {
    Application.launch(MainApp::class.java, *args)
}