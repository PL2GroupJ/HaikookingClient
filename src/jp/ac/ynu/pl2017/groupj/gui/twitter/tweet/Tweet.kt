package jp.ac.ynu.pl2017.groupj.gui.twitter.tweet

import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.scene.control.Label
import javafx.scene.control.TextArea
import jp.ac.ynu.pl2017.groupj.gui.HidePane
import jp.ac.ynu.pl2017.groupj.util.User
import java.net.URL
import java.util.*

/**
 * ツイート確認画面のコントローラー。ツイートの内容を編集できる。
 */
class Tweet(val haiku: String) : Initializable, HidePane {
    override lateinit var hide: () -> Unit
    @FXML lateinit var userName: Label
    @FXML lateinit var tweetArea: TextArea  // Wrap textをオンにすると文字の位置がおかしくなる

    override fun initialize(location: URL?, resources: ResourceBundle?) {
        userName.text = User.twitter!!.screenName
        tweetArea.text = haiku
    }

    @FXML fun onClickCancel() {
        hide()
    }

    @FXML fun onClickTweet() {
    }
}