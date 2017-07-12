package jp.ac.ynu.pl2017.groupj.gui.haiku

import javafx.beans.binding.Bindings
import javafx.beans.property.SimpleIntegerProperty
import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.control.TextField
import javafx.scene.image.Image
import jp.ac.ynu.pl2017.groupj.gui.TransitionModalPane
import jp.ac.ynu.pl2017.groupj.gui.TransitionPane
import jp.ac.ynu.pl2017.groupj.gui.product.Product
import jp.ac.ynu.pl2017.groupj.gui.setting.Setting
import jp.ac.ynu.pl2017.groupj.gui.word.WordCloud
import jp.ac.ynu.pl2017.groupj.util.Season
import java.net.URL
import java.util.*

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
    @FXML lateinit var mark1: Label
    @FXML lateinit var mark2: Label
    @FXML lateinit var mark3: Label
    @FXML lateinit var left: Button
    @FXML lateinit var right: Button
    private lateinit var outputs: Array<Label>
    private val index = SimpleIntegerProperty(0)
    private val model = HaikuModel()

    override fun initialize(location: URL?, resources: ResourceBundle?) {
        outputs = arrayOf(output1, output2, output3)
        input.textProperty().bindBidirectional(output1.textProperty())
        output1.font
        mark1.visibleProperty().bind(index.isEqualTo(0))
        mark2.visibleProperty().bind(index.isEqualTo(1))
        mark3.visibleProperty().bind(index.isEqualTo(2))
        left.disableProperty().bind(index.isEqualTo(2))
        right.disableProperty().bind(index.isEqualTo(0))
        left.setOnAction { index.value += 1 }
        right.setOnAction { index.value -= 1 }
        index.addListener { _, oldValue, newValue ->
            input.textProperty().unbindBidirectional(outputs[oldValue.toInt()].textProperty())
            input.textProperty().bindBidirectional(outputs[newValue.toInt()].textProperty())
        }
        model.haiku.bind(Bindings.concat(output1.textProperty(), System.lineSeparator(),
                                         output2.textProperty(), System.lineSeparator(),
                                         output3.textProperty()))
    }

    @FXML fun onClickSetting() {
        setPane(Setting())
    }

    @FXML fun onClickGenerate() {
        // TODO: 進捗を表示できるようにする
        model.analyze()
        val (image, imageWithHaiku) = model.generateImage()
        // とりあえず夏
        setPane(Product(model.haiku.value, Season.SUMMER, image, imageWithHaiku))
    }

    @FXML fun onClickWordCloud() {
        newPane(WordCloud())
    }
}