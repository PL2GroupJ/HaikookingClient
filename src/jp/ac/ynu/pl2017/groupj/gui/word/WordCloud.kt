package jp.ac.ynu.pl2017.groupj.gui.word

import javafx.application.Platform
import javafx.concurrent.Task
import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.scene.control.Alert
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import jp.ac.ynu.pl2017.groupj.gui.HidePane
import jp.ac.ynu.pl2017.groupj.net.HaikookingConnection
import jp.ac.ynu.pl2017.groupj.util.showConnectionError
import java.net.URL
import java.util.*
import java.util.concurrent.Executors

/**
 * WordCloudの表示画面のコントローラー。この画面はモーダルで表示する。
 */
class WordCloud : Initializable, HidePane {
    override lateinit var hide: () -> Unit
    @FXML lateinit var total: ImageView
    @FXML lateinit var week: ImageView
    @FXML lateinit var month: ImageView
    @FXML lateinit var spring: ImageView
    @FXML lateinit var summer: ImageView
    @FXML lateinit var autumn: ImageView
    @FXML lateinit var winter: ImageView
    @FXML lateinit var newYear: ImageView

    override fun initialize(location: URL?, resources: ResourceBundle?) {
        val imageViews = arrayOf(total, week, month, spring, summer, autumn, winter, newYear)
        val con = HaikookingConnection()
        if (!con.openConnection()) {
            Alert(Alert.AlertType.INFORMATION).showConnectionError()
            // hideが初期化されるのを待つために、スレッドで少し遅延させる。UIスレッドで動かす必要がある
            Platform.runLater {
                Thread.sleep(100)
                hide()
            }
            return
        }

        val task = object : Task<Array<Image>>() {
            override fun call(): Array<Image> = con.readImages()

            override fun succeeded() {
                value.forEachIndexed { i, image -> imageViews[i].image = image }
                con.closeConnection()
            }
        }
        Executors.newSingleThreadExecutor().run {
            submit(task)
            shutdown()
        }
    }
}