package jp.ac.ynu.pl2017.groupj.gui.haiku

import javafx.fxml.FXML
import javafx.fxml.Initializable
import jp.ac.ynu.pl2017.groupj.gui.TransitionModalPane
import jp.ac.ynu.pl2017.groupj.gui.TransitionPane
import jp.ac.ynu.pl2017.groupj.gui.jp.ac.ynu.pl2017.groupj.gui.word.WordCloud
import jp.ac.ynu.pl2017.groupj.gui.product.Product
import jp.ac.ynu.pl2017.groupj.gui.setting.Setting
import java.net.URL
import java.util.*

/**
 * 俳句作成画面のコントローラー。
 */
class Haiku : Initializable, TransitionPane, TransitionModalPane {
    override lateinit var setPane: (Any) -> Unit
    override lateinit var newPane: (Any) -> Unit


    override fun initialize(location: URL?, resources: ResourceBundle?) {

    }

    @FXML fun onClickSetting() {
        setPane(Setting())
    }

    @FXML fun onClickGenerate() {
        setPane(Product())
    }

    @FXML fun onClickWordCloud() {
        newPane(WordCloud())
    }
}