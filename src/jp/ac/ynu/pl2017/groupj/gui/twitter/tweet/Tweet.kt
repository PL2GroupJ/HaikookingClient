package jp.ac.ynu.pl2017.groupj.gui.twitter.tweet

import javafx.beans.binding.Bindings
import javafx.beans.property.IntegerProperty
import javafx.beans.property.SimpleIntegerProperty
import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.scene.control.*
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.paint.Color
import jp.ac.ynu.pl2017.groupj.gui.HidePane
import jp.ac.ynu.pl2017.groupj.net.TwitterAPI
import jp.ac.ynu.pl2017.groupj.util.Season
import jp.ac.ynu.pl2017.groupj.util.User
import java.net.URL
import java.util.*

/**
 * ツイート確認画面のコントローラー。ツイートの内容を編集できる。
 */
class Tweet(val haiku: String, val season: Season, val image: Image) : Initializable, HidePane {
    override lateinit var hide: () -> Unit
    @FXML lateinit var userName: Label
    @FXML lateinit var tweetArea: TextArea  // Wrap textをオンにすると文字の位置がおかしくなる
    @FXML lateinit var thumbnail: ImageView
    @FXML lateinit var attachImage: ToggleButton
    @FXML lateinit var restLabel: Label
    private val restWord = SimpleIntegerProperty()
    private val noImage = Image(javaClass.classLoader.getResourceAsStream("image/no_image.png"))

    override fun initialize(location: URL?, resources: ResourceBundle?) {
        restWord.bind(tweetArea.textProperty().length().subtract(140).negate())
        userName.text = User.twitter!!.name
        tweetArea.text = haiku + System.lineSeparator() + System.lineSeparator() + "#haikooking #$season"
        thumbnail.imageProperty().bind(Bindings.`when`(attachImage.selectedProperty()).then(image).otherwise(noImage))
        restLabel.textProperty().bind(Bindings.concat("残り", restWord, "文字"))
        restLabel.textFillProperty().bind(Bindings.`when`(restWord.greaterThanOrEqualTo(0))
                                                  .then(Color.BLACK)
                                                  .otherwise(Color.RED))
    }

    @FXML fun onClickCancel() {
        hide()
    }

    @FXML fun onClickTweet() {
        // ツイート上限文字数の140かどうかで処理を分岐
        if (restWord.value >= 0) {
            var mediaId = ""
            if (attachImage.isSelected)
                mediaId = TwitterAPI.uploadImage(User.twitter!!.tokenPair, image)
            TwitterAPI.tweet(User.twitter!!.tokenPair, tweetArea.text, mediaId)
            Alert(Alert.AlertType.INFORMATION).apply {
                contentText = "ツイートが完了しました"
                headerText = null
                title = "完了"
                show()
            }
            hide()
        }
        else {
            Alert(Alert.AlertType.WARNING).apply {
                contentText = "文字数を140文字以内にして下さい。"
                headerText = null
                title = "ツイートできません"
                show()
            }
        }
    }
}