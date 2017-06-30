package jp.ac.ynu.pl2017.groupj.gui.setting

import javafx.beans.property.BooleanProperty
import javafx.beans.property.SimpleBooleanProperty
import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.image.ImageView
import jp.ac.ynu.pl2017.groupj.gui.MainApp
import jp.ac.ynu.pl2017.groupj.gui.TransitionModalPane
import jp.ac.ynu.pl2017.groupj.gui.TransitionPane
import jp.ac.ynu.pl2017.groupj.gui.haiku.Haiku
import jp.ac.ynu.pl2017.groupj.gui.twitter.oauth.OAuth
import jp.ac.ynu.pl2017.groupj.net.TwitterAPI
import jp.ac.ynu.pl2017.groupj.util.Token
import jp.ac.ynu.pl2017.groupj.util.User
import java.io.FileInputStream
import java.io.FileOutputStream
import java.net.URL
import java.util.*

/**
 * 設定画面のコントローラー。
 */
class Setting : Initializable, TransitionPane, TransitionModalPane {
    override lateinit var setPane: (Any) -> Unit
    override lateinit var newPane: (Any) -> Unit
    private val login: BooleanProperty = SimpleBooleanProperty()    // OAuthの完了をバインドで検知する
    @FXML lateinit var twitterButton: Button
    @FXML lateinit var nameLabel: Label
    @FXML lateinit var screenNameLabel: Label
    @FXML lateinit var icon: ImageView

    override fun initialize(location: URL?, resources: ResourceBundle?) {
        // TODO: バインドで書き直す。再読込処理が多すぎるかも
        // twitterボタンはログインの有無で状態を変える
        if (User.user != null) {
            nameLabel.text = User.user?.name
            screenNameLabel.text = User.user?.name
            icon.image = User.user?.profileImage
            twitterButton.apply {
                text = "ログアウトする"
                setOnAction {
                    deleteProperties(MainApp.TOKEN, MainApp.TOKEN_SECRET)
                    User.user = null
                    setPane(Setting())
                }
            }
        }
        else {
            twitterButton.run {
                text = "ログインする"
                setOnAction {
                    val oauth = OAuth(TwitterAPI.loadAuthorizeUrl())
                    login.bind(oauth.finish)
                    newPane(oauth)
                }
            }
        }

        // 正しく認証が完了された時、newValueがtrueになる
        login.addListener { _, _, newValue ->
            if (newValue) {
                User.user = TwitterAPI.loadUser(User.token!!)
                // とりあえず設定ページを再読込
                setPane(Setting())
            }
        }
    }

    @FXML fun onClickSave() {
        setPane(Haiku())
    }

    private fun deleteProperties(vararg keys: String) {
        val p = Properties()
        FileInputStream(MainApp.PROP).use {
            p.load(it)
            keys.forEach { p.remove(it) }
            p.store(FileOutputStream(MainApp.PROP), "delete")
        }
    }
}