import jp.ac.ynu.pl2017.groupj.gui.twitter.tweet.TweetModel
import jp.ac.ynu.pl2017.groupj.util.getResourceAsImage
import junit.framework.TestCase
import org.junit.Before
import org.junit.Test

class TweetModelTest : TestCase() {

    private lateinit var model: TweetModel

    @Before override public fun setUp() {
        model = TweetModel()
    }

    @Test fun testOver140Chars() {
        val sb = StringBuilder()
        repeat(141) { sb.append("c")}   // 141文字の文字列を作成
        model.tweetText.value = sb.toString()

        // 引数になんでも良いのでImageが必要なので、タイトル画像を指定
        val tweeted = model.tweet("image/title.png".getResourceAsImage())

        assert(!tweeted)
    }
}