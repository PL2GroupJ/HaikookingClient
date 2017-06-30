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
import java.io.FileInputStream
import java.io.FileOutputStream
import java.net.URL
import java.util.*

class OAuth(val url: String) : Initializable, HidePane {
    override lateinit var hide: () -> Unit
    @FXML lateinit var webView: WebView
    val finish: BooleanProperty = SimpleBooleanProperty()   // trueになった時に、バインドしているコントローラーがウィンドウを閉じる

    override fun initialize(location: URL?, resources: ResourceBundle?) {
        webView.engine.load(url)
        webView.engine.locationProperty().addListener { _, _, newValue ->
            val token = TwitterAPI.loadAuthorizeToken(newValue)
            if (token != null) {
                User.token = TwitterAPI.loadAccessToken(token)
                writeProperties(MainApp.TOKEN to User.token!!.token, MainApp.TOKEN_SECRET to User.token!!.tokenSecret)
                finish.set(true)
                hide()
            }
        }
    }

    // 再び認証のURLを開く
    @FXML fun onClickReload() {
        webView.engine.load(url)
    }

    // アクセストークンをプロパティに書き込む
    private fun writeProperties(vararg content: Pair<String, String>) {
        val p = Properties()
        FileInputStream(MainApp.PROP).use {
            p.load(it)
            content.forEach { (key, value) -> p.setProperty(key, value) }
            p.store(FileOutputStream(MainApp.PROP), "twitter")
        }
    }
}