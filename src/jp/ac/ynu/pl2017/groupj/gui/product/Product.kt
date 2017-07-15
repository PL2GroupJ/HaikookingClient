package jp.ac.ynu.pl2017.groupj.gui.product

import javafx.beans.binding.Bindings
import javafx.beans.property.SimpleBooleanProperty
import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.scene.control.ToggleButton
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.input.Clipboard
import javafx.scene.input.DataFormat
import jp.ac.ynu.pl2017.groupj.gui.SaveImagePane
import jp.ac.ynu.pl2017.groupj.gui.TransitionModalPane
import jp.ac.ynu.pl2017.groupj.gui.TransitionPane
import jp.ac.ynu.pl2017.groupj.gui.haiku.Haiku
import jp.ac.ynu.pl2017.groupj.gui.twitter.oauth.OAuth
import jp.ac.ynu.pl2017.groupj.gui.twitter.tweet.Tweet
import jp.ac.ynu.pl2017.groupj.net.TwitterAPI
import jp.ac.ynu.pl2017.groupj.util.Season
import jp.ac.ynu.pl2017.groupj.util.User
import java.net.URL
import java.util.*


/**
 * 俳句確認画面のコントローラー。出来上がった画像の確認、保存、ツイートができる。
 */
class Product(val haiku: String, val season: Season, val image: Image, val imageWithHaiku: Image) : Initializable, TransitionPane, TransitionModalPane, SaveImagePane {
    override lateinit var setPane: (Any) -> Unit
    override lateinit var newPane: (Any) -> Unit
    override lateinit var saveImage: (Image, String) -> Boolean
    @FXML lateinit var haikuImage: ImageView
    @FXML lateinit var textOn: ToggleButton
    private val login = SimpleBooleanProperty()    // OAuthの完了をバインドで検知する

    override fun initialize(location: URL?, resources: ResourceBundle?) {
        haikuImage.imageProperty().bind(Bindings.`when`(textOn.selectedProperty()).then(image).otherwise(imageWithHaiku))
    }

    @FXML fun onClickTweet() {
        if (User.twitter == null) {
            val oauth = OAuth(TwitterAPI.loadAuthorizeUrl())
            login.bind(oauth.finish)
            newPane(oauth)

            // 正しく認証が完了された時、newValueがtrueになる
            login.addListener { _, _, newValue ->
                if (newValue) {
                    // 再び呼び出して、ツイート画面を表示する
                    onClickTweet()
                }
            }
        }
        else {
            newPane(Tweet(haiku, season, haikuImage.image))
        }
    }

    @FXML fun onClickReturn() = setPane(Haiku())

    @FXML fun onClickSaveImageWithHaiku() = saveImage(imageWithHaiku, haiku.split(System.lineSeparator())[0])

    @FXML fun onClickSaveImage() = saveImage(image, haiku.split(System.lineSeparator())[0])

    @FXML fun onClickCopyImageWithHaiku() = Clipboard.getSystemClipboard().setContent(mapOf(DataFormat.IMAGE to imageWithHaiku))

    @FXML fun onClickCopyImage() = Clipboard.getSystemClipboard().setContent(mapOf(DataFormat.IMAGE to image))
}