package jp.ac.ynu.pl2017.groupj.gui.haiku

import javafx.beans.binding.Bindings
import javafx.beans.property.SimpleIntegerProperty
import javafx.concurrent.Task
import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.scene.control.*
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent
import javafx.scene.layout.VBox
import jp.ac.ynu.pl2017.groupj.gui.TransitionModalPane
import jp.ac.ynu.pl2017.groupj.gui.TransitionPane
import jp.ac.ynu.pl2017.groupj.gui.product.Product
import jp.ac.ynu.pl2017.groupj.gui.setting.Setting
import jp.ac.ynu.pl2017.groupj.gui.word.WordCloud
import jp.ac.ynu.pl2017.groupj.util.getResourceAsImage
import jp.ac.ynu.pl2017.groupj.util.showConnectionError
import java.net.URL
import java.util.*
import java.util.concurrent.Executors
import kotlin.test.fail

/**
 * 俳句作成画面のコントローラー。
 */
class Haiku : Initializable, TransitionPane, TransitionModalPane {
    override lateinit var setPane: (Any) -> Unit
    override lateinit var newPane: (Any) -> Unit
    @FXML lateinit var input: TextField
    @FXML lateinit var output1: Label
    @FXML lateinit var output2: Label
    @FXML lateinit var output3: Label
    @FXML lateinit var mark1: ImageView
    @FXML lateinit var mark2: ImageView
    @FXML lateinit var mark3: ImageView
    @FXML lateinit var box1: VBox
    @FXML lateinit var box2: VBox
    @FXML lateinit var box3: VBox
    @FXML lateinit var left: Button
    @FXML lateinit var right: Button
    @FXML lateinit var settingImage: ImageView
    @FXML lateinit var progressBar: ProgressBar
    private val index = SimpleIntegerProperty(0)
    private val model = HaikuModel()

    override fun initialize(location: URL?, resources: ResourceBundle?) {
        val outputs = arrayOf(output1, output2, output3)
        val marks = arrayOf(mark1, mark2, mark3)
        val boxes = arrayOf(box1, box2, box3)
        val brush = "image/brush.png".getResourceAsImage()
        boxes.forEachIndexed { i, box -> box.setOnMouseClicked { index.value = i } }
        marks.forEachIndexed { i, mark ->
            mark.visibleProperty().bind(index.isEqualTo(i))
            mark.image = brush
        }
        left.disableProperty().bind(index.isEqualTo(2))
        right.disableProperty().bind(index.isEqualTo(0))
        left.setOnAction { index.value += 1 }
        right.setOnAction { index.value -= 1 }
        settingImage.image = "image/gear.png".getResourceAsImage()
        progressBar.progressProperty().bind(model.progress)
        input.textProperty().bindBidirectional(output1.textProperty())
        index.addListener { _, oldValue, newValue ->
            input.textProperty().unbindBidirectional(outputs[oldValue.toInt()].textProperty())
            input.textProperty().bindBidirectional(outputs[newValue.toInt()].textProperty())
            input.positionCaret(outputs[newValue.toInt()].text.length)
        }
        model.haiku.bind(Bindings.concat(output1.textProperty(), System.lineSeparator(),
                                         output2.textProperty(), System.lineSeparator(),
                                         output3.textProperty()))
    }

    @FXML fun onKeyPressed(event: KeyEvent) {
        if (event.code == KeyCode.ENTER) {
            if (index.value < 2)
                index.value += 1
        }
    }

    @FXML fun onClickSetting() {
        setPane(Setting())
    }

    @FXML fun onClickGenerate() {
        progressBar.isVisible = true
        // 画像生成処理を非同期で行う
        val task = object : Task<Pair<Image, Image>>() {

            override fun call(): Pair<Image, Image> {
                if(!model.analyze()) fail()
                return model.generateImage()
            }

            override fun failed() {
                progressBar.isVisible = false
                Alert(Alert.AlertType.INFORMATION).showConnectionError()
            }

            override fun succeeded() {
                setPane(Product(model.haiku.value, model.season, model.advice, value.first, value.second))
            }
        }
        Executors.newSingleThreadExecutor().run {
            submit(task)
            shutdown()
        }
    }

    @FXML fun onClickWordCloud() {
        newPane(WordCloud())
    }
}