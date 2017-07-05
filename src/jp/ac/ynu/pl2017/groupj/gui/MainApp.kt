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
import jp.ac.ynu.pl2017.groupj.util.*
import java.io.File
import java.nio.file.Paths
import java.util.*

/**
 * GUIのベースのクラス。Swingで言うところのJFrame。
 */
class MainApp : Application() {
    private lateinit var stage: Stage
    private lateinit var modalStage: Stage
    private val styles = arrayOf(Paths.get("res/css/spring.css").toUri().toString(),
                                Paths.get("res/css/summer.css").toUri().toString(),
                                Paths.get("res/css/autumn.css").toUri().toString(),
                                Paths.get("res/css/winter.css").toUri().toString())
    private val defaultIndex = Calendar.getInstance().getSeason().ordinal

    override fun start(primaryStage: Stage) {
        readProperties()
        stage = primaryStage
        setPane(Title())
        stage.show()
        println("${User.twitter}")
        println("${User.season}")
        println("advice = ${User.advice}")
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
        val styleIndex = if (User.season == Season.DEFAULT) defaultIndex else User.season.ordinal
        stage.run {
            title = className
            scene = Scene(parent, WIDTH, HEIGHT).apply { stylesheets.add(styles[styleIndex]) }
        }

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
            initOwner(stage)
            title = className
            scene = Scene(parent)
            show()
        }

        if (controller is HidePane) {
            controller.hide = modalStage::hide
        }
    }

    private fun readProperties() {
        if (!File(PROP).exists()) {
            File(PROP).createNewFile()
        }
        val props = Properties().read(PROP, TOKEN, TOKEN_SECRET, SEASON, ADVICE)
        if (!props[0].isNullOrEmpty() && !props[1].isNullOrEmpty()) {
            val tokenPair = TokenPair(props[0]!!, props[1]!!)
            User.twitter = TwitterAPI.loadUser(tokenPair)
        }
        if (!props[2].isNullOrEmpty()) {
            User.season = Season.valueOf(props[2]!!)
        }
        if (!props[3].isNullOrEmpty()) {
            User.advice = props[3]!!.toBoolean()
        }
    }

    companion object {
        val WIDTH = 450.0
        val HEIGHT = 600.0
        val PROP = "haikooking.properties"
        val TOKEN = "token"
        val TOKEN_SECRET = "tokenSecret"
        val SEASON = "season"
        val ADVICE = "advice"
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