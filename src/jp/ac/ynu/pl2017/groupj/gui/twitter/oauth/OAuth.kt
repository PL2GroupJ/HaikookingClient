package jp.ac.ynu.pl2017.groupj.gui.twitter.oauth

import javafx.beans.property.BooleanProperty
import javafx.beans.property.SimpleBooleanProperty
import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.scene.web.WebView
import jp.ac.ynu.pl2017.groupj.gui.HidePane
import jp.ac.ynu.pl2017.groupj.gui.MainApp
import jp.ac.ynu.pl2017.groupj.net.TwitterAPI
import jp.ac.ynu.pl2017.groupj.util.User
import jp.ac.ynu.pl2017.groupj.util.write
import java.net.URL
import java.util.*

/**
 * TwitterのOAuth認証を行う画面のコントローラー。
 */
class OAuth(val url: String) : Initializable, HidePane {
    override lateinit var hide: () -> Unit
    @FXML lateinit var webView: WebView
    val finish: BooleanProperty = SimpleBooleanProperty()   // trueになった時に、バインドしているコントローラーがウィンドウを閉じる

    override fun initialize(location: URL?, resources: ResourceBundle?) {
        webView.engine.load(url)
        webView.engine.locationProperty().addListener { _, _, newValue ->
            val tokenPair = TwitterAPI.loadAuthorizeToken(newValue)
            // newValueがcallbackURLだった時にtokenPairが戻るので、処理を完了させる。
            if (tokenPair != null) {
                User.twitter = TwitterAPI.loadUser(TwitterAPI.loadAccessToken(tokenPair))
                Properties().write(MainApp.PROP,
                        MainApp.TOKEN to User.twitter!!.tokenPair.token,
                        MainApp.TOKEN_SECRET to User.twitter!!.tokenPair.tokenSecret)
                finish.set(true)
                hide()
            }
        }
    }

    // 再び認証のURLを開く
    @FXML fun onClickReload() {
        webView.engine.load(url)
    }
}