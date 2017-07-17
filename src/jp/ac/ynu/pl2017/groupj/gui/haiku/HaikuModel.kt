package jp.ac.ynu.pl2017.groupj.gui.haiku

import javafx.beans.property.SimpleDoubleProperty
import javafx.beans.property.SimpleStringProperty
import javafx.scene.control.Alert
import javafx.scene.image.Image
import jp.ac.ynu.pl2017.groupj.net.GoogleSearch
import jp.ac.ynu.pl2017.groupj.net.HaikookingConnection
import jp.ac.ynu.pl2017.groupj.net.HttpUtils
import jp.ac.ynu.pl2017.groupj.util.ImageUtil
import jp.ac.ynu.pl2017.groupj.util.Season
import jp.ac.ynu.pl2017.groupj.util.getResourceAsImage
import jp.ac.ynu.pl2017.groupj.util.showConnectionError
import java.util.*

/**
 * 俳句作成画面のモデル。
 */
class HaikuModel {
    val haiku = SimpleStringProperty()
    val progress = SimpleDoubleProperty()
    lateinit var advice: String
    lateinit var season: Season
    private lateinit var nounList: List<String>

    /**
     * サーバに俳句を送信し、解析結果を受け取る。このクラスの他のメソッドの利用前にかならず呼び出す必要がある。
     * @return 解析完了の可否
     */
    fun analyze(): Boolean {
        // 自サーバとの通信
        val con = HaikookingConnection()
        if (!con.openConnection()) return false
        con.writeHaiku(haiku.value)
        nounList = con.readNounList()
        advice = con.readAdvice()
        season = con.readSeason()
        con.closeConnection()
        return true
    }

    /**
     * 俳句付き(と無し)の二枚の画像を生成する。Google検索をしていない今は処理速度は気にならない。
     */
    fun generateImage(): Pair<Image, Image> {
        // TODO: 画像がダウンロード出来なかった時の処理を書く
//        val random = Random()
//        val backLink = GoogleSearch.search(nounList[0])[random.nextInt(10)].link
//        val backImage = HttpUtils.downloadImage(backLink)
//        progressBar.value += 0.25
//        val images = nounList.drop(1).take(2).map {
//            val link = GoogleSearch.search(it)[random.nextInt(10)].link
//            val image = HttpUtils.downloadImage(link)
//            progressBar.value += 0.25
//            image
//        }
        val backImage = "image/test/test_back.jpg".getResourceAsImage()
        Thread.sleep(1000)
        progress.value += 0.3
        val images = arrayOf("image/test/test1.jpg", "image/test/test2.jpg").map {
            Thread.sleep(1000)
            progress.value += 0.3
            it.getResourceAsImage()
        }
        ImageUtil.createHaikuImage(200, backImage, images.toTypedArray(), haiku.value.split(System.lineSeparator()).toTypedArray())
        progress.value = 1.0
        return ImageUtil.getHaikuimg() to ImageUtil.gethaikuimgWithstr()
    }
}