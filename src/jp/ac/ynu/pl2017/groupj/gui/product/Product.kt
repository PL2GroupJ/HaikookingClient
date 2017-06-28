package jp.ac.ynu.pl2017.groupj.gui.product

import javafx.fxml.FXML
import javafx.fxml.Initializable
import jp.ac.ynu.pl2017.groupj.gui.TransitionPane
import jp.ac.ynu.pl2017.groupj.gui.haiku.Haiku
import java.net.URL
import java.util.*

/**
 * 俳句確認画面のコントローラー。出来上がった画像の確認、保存、ツイートができる。
 */
class Product : Initializable, TransitionPane {
    override lateinit var setPane: (Any) -> Unit

    override fun initialize(location: URL?, resources: ResourceBundle?) {
    }

    @FXML fun onClickReturn () {
        setPane(Haiku())
    }
}