package jp.ac.ynu.pl2017.groupj.gui.setting

import javafx.beans.property.SimpleBooleanProperty
import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.scene.control.*
import javafx.scene.image.ImageView
import javafx.scene.text.Font
import jp.ac.ynu.pl2017.groupj.gui.MainApp
import jp.ac.ynu.pl2017.groupj.gui.TransitionModalPane
import jp.ac.ynu.pl2017.groupj.gui.TransitionPane
import jp.ac.ynu.pl2017.groupj.gui.credit.Credit
import jp.ac.ynu.pl2017.groupj.gui.haiku.Haiku
import jp.ac.ynu.pl2017.groupj.gui.twitter.oauth.OAuth
import jp.ac.ynu.pl2017.groupj.net.TwitterAPI
import jp.ac.ynu.pl2017.groupj.util.User
import jp.ac.ynu.pl2017.groupj.util.delete
import java.net.URL
import java.util.*

/**
 * 設定画面のコントローラー。
 */
class Setting : Initializable, TransitionPane, TransitionModalPane {
    override lateinit var setPane: (Any) -> Unit
    override lateinit var newPane: (Any) -> Unit
    @FXML lateinit var toggleGroup: ToggleGroup
    @FXML lateinit var advice: ToggleButton
    @FXML lateinit var twitterButton: Button
    @FXML lateinit var nameLabel: Label
    @FXML lateinit var screenNameLabel: Label
    @FXML lateinit var icon: ImageView
    @FXML lateinit var fontField: TextField
    @FXML lateinit var fontCombo: ComboBox<String>
    private val model = SettingModel()
    private val login = SimpleBooleanProperty()    // OAuthの完了をバインドで検知する

    override fun initialize(location: URL?, resources: ResourceBundle?) {
        model.selectedRadio.bind(toggleGroup.selectedToggleProperty())
        model.selectedFont.bind(fontCombo.selectionModel.selectedItemProperty())
        model.selectedFont.addListener { _, _, newValue -> fontField.font = Font.font(newValue, 18.0) }
        toggleGroup.selectToggle(toggleGroup.toggles[User.season.ordinal])
        fontCombo.run {
            items.addAll(Font.getFamilies())
            selectionModel.select(User.font)
        }

        advice.run {
            isSelected = User.advice
            text = if (User.advice) "ON" else "OFF"
            selectedProperty().addListener { _, _, newValue -> text = if (newValue) "ON" else "OFF" }
            model.advice.bind(selectedProperty())
        }

        // twitterのログインの有無で状態を変える。twitterだけは保存ボタン関係なしに即時反映
        if (User.twitter != null) {
            nameLabel.text = User.twitter?.name
            screenNameLabel.text = User.twitter?.screenName
            icon.image = User.twitter?.profileImage
            twitterButton.apply {
                text = "連携解除"
                setOnAction {
                    Properties().delete(MainApp.PROP_NAME, MainApp.TOKEN, MainApp.TOKEN_SECRET)
                    User.twitter = null
                    setPane(Setting())
                }
            }
        }
        else {
            twitterButton.run {
                text = "連携"
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
                // とりあえず設定ページを再読込
                setPane(Setting())
            }
        }
    }

    @FXML fun onClickSave() {
        model.save()
        setPane(Haiku())
    }

    @FXML fun onClickCredit() {
        newPane(Credit())
    }
}