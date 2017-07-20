package jp.ac.ynu.pl2017.groupj.gui.title

import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.scene.canvas.Canvas
import javafx.scene.image.ImageView
import jp.ac.ynu.pl2017.groupj.gui.TransitionPane
import jp.ac.ynu.pl2017.groupj.gui.haiku.Haiku
import jp.ac.ynu.pl2017.groupj.util.getResourceAsImage
import java.net.URL
import java.util.*

/**
 * タイトル画面のコントローラー。アニメーションを背景に持つ。
 */
class Title : Initializable, TransitionPane {
    override lateinit var setPane: (Any) -> Unit
    @FXML lateinit var titleImage: ImageView
    @FXML lateinit var canvas: Canvas
    private val model: TitleModel by lazy { TitleModel(canvas.graphicsContext2D) }

    override fun initialize(location: URL?, resources: ResourceBundle?) {
        titleImage.image = "image/title.png".getResourceAsImage()
        model.draw()
    }

    /**
     * 俳句作成画面に遷移する
     */
    @FXML fun onClickStart() {
        setPane(Haiku())
    }
}