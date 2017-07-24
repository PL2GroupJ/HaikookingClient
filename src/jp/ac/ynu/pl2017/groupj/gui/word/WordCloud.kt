package jp.ac.ynu.pl2017.groupj.gui.word

import javafx.application.Platform
import javafx.beans.binding.Bindings
import javafx.beans.property.SimpleDoubleProperty
import javafx.concurrent.Task
import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.scene.control.Alert
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import jp.ac.ynu.pl2017.groupj.gui.HidePane
import jp.ac.ynu.pl2017.groupj.gui.ResizePane
import jp.ac.ynu.pl2017.groupj.net.HaikookingConnection
import jp.ac.ynu.pl2017.groupj.util.showConnectionError
import java.net.URL
import java.util.*
import java.util.concurrent.Executors

/**
 * WordCloudの表示画面のコントローラー。この画面はモーダルで表示する。
 */
class WordCloud : Initializable, HidePane, ResizePane {
    override lateinit var hide: () -> Unit
    override val heightProperty = SimpleDoubleProperty()
    override val widthProperty  = SimpleDoubleProperty()
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
        imageViews.forEach { imageView ->
            val minSize = Bindings.`when`(widthProperty.lessThan(heightProperty.subtract(20)))
                    .then(widthProperty)
                    .otherwise(heightProperty.subtract(20))
            imageView.fitWidthProperty().bind(minSize)
            imageView.fitHeightProperty().bind(minSize)
        }
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

        val task = object : Task<List<Image>>() {
            override fun call(): List<Image> {
                val images = con.readImages()
                con.closeConnection()
                return images
            }

            override fun succeeded() = value.forEachIndexed { i, image -> imageViews[i].image = image }
        }
        Executors.newSingleThreadExecutor().run {
            submit(task)
            shutdown()
        }
    }
}