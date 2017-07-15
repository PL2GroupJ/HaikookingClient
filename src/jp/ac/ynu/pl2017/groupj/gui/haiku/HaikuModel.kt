package jp.ac.ynu.pl2017.groupj.gui.haiku

import javafx.beans.property.SimpleStringProperty
import javafx.scene.image.Image
import jp.ac.ynu.pl2017.groupj.util.ImageUtil
import jp.ac.ynu.pl2017.groupj.util.getResourceAsImage
import java.util.*

/**
 * 俳句作成画面のモデル。
 */
class HaikuModel {
    val haiku = SimpleStringProperty()
    private lateinit var nounList: List<String>
    lateinit var advice: String

    /**
     * サーバに俳句を送信し、解析結果を受け取る。このクラスの他のメソッドの利用前にかならず呼び出す必要がある。
     */
    fun analyze() {
        // 自サーバとの通信
//        val con = HaikookingConnection()
//        con.writeHaiku(haiku.value)
        nounList = listOf("image/test/test_back.jpg", "image/test/test1.jpg", "image/test/test2.jpg")//con.readNounList()
        advice = "これはアドバイスです"//con.readAdvice()
//        con.closeConnection()
    }

    /**
     * 俳句付き(と無し)の二枚の画像を生成する。Google検索をしていない今は処理速度は気にならない。
     */
    fun generateImage(): Pair<Image, Image> {
        val random = Random()
//        nounList  名詞のリストの加工。[背景, その他名詞複数]になるように
//        val backLink = GoogleSearch.search(nounList[0])[random.nextInt(10)].link
        val backImage = nounList[0].getResourceAsImage()//Image(backLink)
        val images = nounList.drop(1).map {
//            val link = GoogleSearch.search(it)[random.nextInt(10)].link
            it.getResourceAsImage()//Image(link)
        }
        ImageUtil.createHaikuImage(200, backImage, images.toTypedArray(), haiku.value.split(System.lineSeparator()).toTypedArray())
        return ImageUtil.getHaikuimg() to ImageUtil.gethaikuimgWithstr()
    }
}