package jp.ac.ynu.pl2017.groupj.gui.twitter.tweet

import javafx.beans.binding.Bindings
import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.scene.control.Alert
import javafx.scene.control.Label
import javafx.scene.control.TextArea
import javafx.scene.control.ToggleButton
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.paint.Color
import jp.ac.ynu.pl2017.groupj.gui.HidePane
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
    @FXML lateinit var attachButton: ToggleButton
    @FXML lateinit var restLabel: Label
    private val noImage = Image(javaClass.classLoader.getResourceAsStream("image/no_image.png"))
    private val model = TweetModel()

    override fun initialize(location: URL?, resources: ResourceBundle?) {
        model.run {
            tweetText.bind(tweetArea.textProperty())
            attaches.bind(attachButton.selectedProperty())
        }

        userName.text = User.twitter!!.name
        tweetArea.text = haiku + System.lineSeparator() + System.lineSeparator() + "#haikooking"
        if (season != Season.DEFAULT) tweetArea.text += " #$season"
        thumbnail.imageProperty().bind(Bindings.`when`(model.attaches).then(image).otherwise(noImage))
        restLabel.textProperty().bind(Bindings.concat("残り", model.restWord, "文字"))
        restLabel.textFillProperty().bind(Bindings.`when`(model.restWord.greaterThanOrEqualTo(0))
                                                  .then(Color.BLACK)
                                                  .otherwise(Color.RED))
    }

    @FXML fun onClickCancel() {
        hide()
    }

    @FXML fun onClickTweet() {
        val tweeted = model.tweet(thumbnail.image)
        if (tweeted) {
            Alert(Alert.AlertType.INFORMATION).apply {
                title = "完了"
                headerText = null
                contentText = "ツイートが完了しました"
                show()
            }
            hide()
        }
        else {
            Alert(Alert.AlertType.WARNING).apply {
                title = "ツイートできません"
                headerText = null
                contentText = "文字数を140文字以内にして下さい。"
                show()
            }
        }
    }
}