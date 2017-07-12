package jp.ac.ynu.pl2017.groupj.gui.twitter.tweet

import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleIntegerProperty
import javafx.beans.property.SimpleStringProperty
import javafx.scene.image.Image
import jp.ac.ynu.pl2017.groupj.net.TwitterAPI
import jp.ac.ynu.pl2017.groupj.util.User

/**
 * ツイート確認画面のモデル
 */
class TweetModel {
    val restWord = SimpleIntegerProperty()  // ツイートできる残り文字数
    val tweetText = SimpleStringProperty()  // ツイートする文字列
    val attaches = SimpleBooleanProperty()  // 画像を添付するかどうか

    init {
        restWord.bind(tweetText.length().subtract(140).negate())
    }

    fun tweet(image: Image): Boolean {
        // 文字数制限を満たしているならツイート
        if (restWord.value >= 0) {
            val mediaId = if (attaches.value) TwitterAPI.uploadImage(User.twitter!!.tokenPair, image)
                          else ""
            TwitterAPI.tweet(User.twitter!!.tokenPair, tweetText.value, mediaId)
            return true
        }
        else {
            return false
        }
    }
}