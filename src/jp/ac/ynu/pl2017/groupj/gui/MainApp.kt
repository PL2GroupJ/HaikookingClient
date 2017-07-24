package jp.ac.ynu.pl2017.groupj.gui

import javafx.application.Application
import javafx.beans.property.SimpleDoubleProperty
import javafx.embed.swing.SwingFXUtils
import javafx.fxml.FXMLLoader
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.scene.image.Image
import javafx.stage.FileChooser
import javafx.stage.Modality
import javafx.stage.Stage
import jp.ac.ynu.pl2017.groupj.gui.title.Title
import jp.ac.ynu.pl2017.groupj.net.TwitterAPI
import jp.ac.ynu.pl2017.groupj.util.*
import java.awt.image.BufferedImage
import java.io.File
import java.util.*
import javax.imageio.ImageIO

/**
 * GUIのベースのクラス。Swingで言うところのJFrame。
 */
class MainApp : Application() {
    private lateinit var stage: Stage
    private lateinit var modalStage: Stage
    private val styles = arrayOf(javaClass.classLoader.getResource("css/spring.css").toString(),
                                javaClass.classLoader.getResource("css/summer.css").toString(),
                                javaClass.classLoader.getResource("css/autumn.css").toString(),
                                javaClass.classLoader.getResource("css/winter.css").toString())
    private val defaultIndex = Calendar.getInstance().getSeason().ordinal

    override fun start(primaryStage: Stage) {
        System.setProperty("file.encoding", "UTF-8")    // エンコーディングをここで指定しないと文字化けする
        readProperties()
        stage = primaryStage
        setPane(Title())
        stage.show()
        println("${User.twitter}")
        println("season = ${User.season}")
        println("advice = ${User.advice}")
        println("font = ${User.font}")
        println("ipAddress = ${User.ipAddress}")
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
            title = APP_NAME
            scene = Scene(parent, WIDTH, HEIGHT).apply { stylesheets.add(styles[styleIndex]) }
        }

        if (controller is TransitionPane) {
            controller.setPane = this::setPane
        }
        if (controller is TransitionModalPane) {
            controller.newPane = this::newPane
        }
        if (controller is SaveImagePane) {
            controller.saveImage = this::saveImage
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
            title = APP_NAME
            scene = Scene(parent)
            show()
        }

        if (controller is HidePane) {
            controller.hide = modalStage::hide
        }
        if (controller is ResizePane) {
            controller.widthProperty.bind(modalStage.scene.widthProperty())
            controller.heightProperty.bind(modalStage.scene.heightProperty())
        }
    }

    /**
     * FileChooserを表示し、指定されたパスに画像を保存する。
     * @param image 保存対象の画像[Image]
     * @param name 保存時のデフォルトの名前
     * @return 保存の可否
     */
    fun saveImage(image: Image, name: String): Boolean {
        val extensionList = arrayOf("jpg", "png", "gif", "jpeg", "bmp") // wbmpは対応しているみたいだが、保存できなかった
        val file = FileChooser().run {
            title = "画像の保存"
            initialFileName = name
            extensionFilters.addAll(extensionList.map { FileChooser.ExtensionFilter(it.toUpperCase(), "*.$it") })
            showSaveDialog(stage)
        }
        file ?: return false

        if (file.extension in extensionList) {
            val w = image.width.toInt()
            val h = image.height.toInt()
            // 透明度情報を消すためにBufferedImageに再書き込み
            val oldBI = SwingFXUtils.fromFXImage(image, null)
            val newBI = BufferedImage(w, h, BufferedImage.TYPE_3BYTE_BGR)
            newBI.createGraphics().drawImage(oldBI, 0, 0, w, h, null)
            ImageIO.write(newBI, file.extension, file)
        }
        return true
    }

    private fun readProperties() {
        if (!File(PROP_NAME).exists()) {
            File(PROP_NAME).createNewFile()
            Properties().write(PROP_NAME, IP_ADDRESS to "localhost")
        }
        val propMap = Properties().read(PROP_NAME, TOKEN, TOKEN_SECRET, SEASON, ADVICE, FONT, IP_ADDRESS)
        if (!propMap[TOKEN].isNullOrEmpty() && !propMap[TOKEN_SECRET].isNullOrEmpty()) {
            val tokenPair = TokenPair(propMap[TOKEN]!!, propMap[TOKEN_SECRET]!!)
            User.twitter = TwitterAPI.loadUser(tokenPair)
        }
        if (!propMap[SEASON].isNullOrEmpty()) {
            User.season = Season.valueOf(propMap[SEASON]!!)
        }
        if (!propMap[ADVICE].isNullOrEmpty()) {
            User.advice = propMap[ADVICE]!!.toBoolean()
        }
        if (!propMap[FONT].isNullOrEmpty()) {
            User.font = propMap[FONT]!!
        }
        if (!propMap[IP_ADDRESS].isNullOrEmpty()) {
            User.ipAddress = propMap[IP_ADDRESS]!!
        }
    }

    companion object {
        val WIDTH = 450.0
        val HEIGHT = 600.0
        val APP_NAME = "Haikooking"
        val PROP_NAME = "haikooking.properties"
        val TOKEN = "token"
        val TOKEN_SECRET = "tokenSecret"
        val SEASON = "season"
        val ADVICE = "advice"
        val FONT = "font"
        val IP_ADDRESS = "ipAddress"
    }
}

interface TransitionPane {
    var setPane: (Any) -> Unit
}

interface TransitionModalPane {
    var newPane: (Any) -> Unit
}

interface SaveImagePane {
    var saveImage: (Image, String) -> Boolean
}

interface HidePane {
    var hide: () -> Unit
}

interface ResizePane {
    val widthProperty: SimpleDoubleProperty
    val heightProperty: SimpleDoubleProperty
}

fun main(args: Array<String>) {
    Application.launch(MainApp::class.java, *args)
}