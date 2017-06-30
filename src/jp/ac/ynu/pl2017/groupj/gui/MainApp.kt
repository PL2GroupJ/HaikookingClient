package jp.ac.ynu.pl2017.groupj.gui

import javafx.application.Application
import javafx.fxml.FXMLLoader
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.stage.Modality
import javafx.stage.Stage
import javafx.stage.StageStyle
import jp.ac.ynu.pl2017.groupj.gui.title.Title
import jp.ac.ynu.pl2017.groupj.net.TwitterAPI
import jp.ac.ynu.pl2017.groupj.util.Token
import jp.ac.ynu.pl2017.groupj.util.User
import java.io.FileInputStream
import java.util.*

/**
 * GUIのベースのクラス。Swingで言うところのJFrame。
 */
class MainApp : Application() {
    private lateinit var stage: Stage
    private lateinit var modalStage: Stage

    override fun start(primaryStage: Stage) {
        stage = primaryStage
        setPane(Title())
        stage.show()
        readProperties()
        println(User.user)
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
        stage.scene = Scene(parent, WIDTH, HEIGHT)

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
        modalStage = Stage().apply {
            initModality(Modality.APPLICATION_MODAL)
            initStyle(StageStyle.UTILITY)
            initOwner(stage)
            title = className
            scene = Scene(parent, width, height)
            show()
        }

        if (controller is HidePane) {
            controller.hide = modalStage::hide
        }
    }

    private fun readProperties() {
        val p = Properties()
        FileInputStream(PROP).use {
            p.load(it)
            // Twitterのプロパティ
            if (p.containsKey(TOKEN) && p.containsKey(TOKEN_SECRET)) {
                val token = Token(p.getProperty(TOKEN), p.getProperty(TOKEN_SECRET))
                User.user = TwitterAPI.loadUser(token)
                User.token = token
            }
        }
    }

    companion object {
        val WIDTH = 450.0
        val HEIGHT = 600.0
        val PROP = "haikooking.properties"
        val TOKEN = "token"
        val TOKEN_SECRET = "tokenSecret"
    }
}

interface TransitionPane {
    var setPane: ((Any) -> Unit)
}

interface TransitionModalPane {
    var newPane: ((Any) -> Unit)
}

interface HidePane {
    var hide: () -> Unit
}

fun main(args: Array<String>) {
    Application.launch(MainApp::class.java, *args)
}