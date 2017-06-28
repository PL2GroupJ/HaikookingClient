package jp.ac.ynu.pl2017.groupj.gui.setting

import javafx.fxml.FXML
import javafx.fxml.Initializable
import jp.ac.ynu.pl2017.groupj.gui.TransitionPane
import jp.ac.ynu.pl2017.groupj.gui.haiku.Haiku
import java.net.URL
import java.util.*

/**
 * 設定画面のコントローラー。
 */
class Setting : Initializable, TransitionPane {
    override lateinit var setPane: (Any) -> Unit

    override fun initialize(location: URL?, resources: ResourceBundle?) {
    }

    @FXML fun onClickSave() {
        setPane(Haiku())
    }
}