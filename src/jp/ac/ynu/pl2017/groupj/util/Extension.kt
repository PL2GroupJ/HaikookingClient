package jp.ac.ynu.pl2017.groupj.util

import javafx.embed.swing.SwingFXUtils
import javafx.scene.control.Alert
import javafx.scene.control.Tooltip
import javafx.scene.image.Image
import jp.ac.ynu.pl2017.groupj.gui.MainApp
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.*
import javax.imageio.ImageIO

/**
 * プロパティファイルを読み込んで、リストとして返す。
 * @param propertyFile 対象のプロパティファイルの名前
 * @param keys 読み込みたいプロパティのキーの可変長配列
 * @return keysに対応した順で読み込んだ、プロパティの値のマップ
 */
fun Properties.read(propertyFile: String, vararg keys: String): Map<String, String> {
    FileInputStream(propertyFile).use {
        load(it)
        return keys.associate { it to getProperty(it) }
    }
}

/**
 * プロパティファイルに値を書き込む(新規or更新)
 * @param propertyFile 対象のプロパティファイルの名前
 * @param keyAndValues 書き込みたいプロパティの、キーと値のペアの可変長配列
 */
fun Properties.write(propertyFile: String, vararg keyAndValues: Pair<String, String>) {
    FileInputStream(MainApp.PROP_NAME).use {
        load(it)
        keyAndValues.forEach { (key, value) -> setProperty(key, value) }
        FileOutputStream(propertyFile).use { store(it, "add or update:${keyAndValues.map { (key, _) -> key }.joinToString(separator = ", ")}") }
    }
}

/**
 * プロパティファイルから要素を削除する。
 * @param propertyFile 対象のプロパティファイルの名前
 * @param keys 削除対象のキーの可変長配列
 */
fun Properties.delete(propertyFile: String, vararg keys: String) {
    FileInputStream(propertyFile).use {
        load(it)
        keys.forEach { remove(it) }
        FileOutputStream(MainApp.PROP_NAME).use { store(it, "delete:${keys.joinToString(separator = ", ")}") }
    }
}

/**
 * カレンダーの月に対応する[Season]を生成する。
 * @return カレンダーの月に対応した[Season]
 */
fun Calendar.getSeason(): Season {
    return when (get(Calendar.MONTH) + 1) {
        in 3..5  -> Season.SPRING
        in 6..8  -> Season.SUMMER
        in 9..11 -> Season.AUTUMN
        12, 1, 2 -> Season.WINTER
        else     -> Season.DEFAULT
    }
}

/**
 * JavaFXの[Image]をバイト列に変換する。
 * @param format 画像のフォーマット。デフォルトはpng
 * @return イメージを表すバイト列
 */
fun Image.toByteArray(format: String = "png"): ByteArray {
    val bufferedImage = SwingFXUtils.fromFXImage(this, null)
    val out = ByteArrayOutputStream()
    ImageIO.write(bufferedImage, format, out)
    return out.toByteArray()
}

/**
 * バイト列をJavaFXの[Image]に変換する。
 * @return バイト列が表すイメージ
 */
fun ByteArray.toImage(): Image {
    val bufferedImage = ImageIO.read(ByteArrayInputStream(this))
    return SwingFXUtils.toFXImage(bufferedImage, null)
}

/**
 * 文字列が示すリソースを[Image]に変換する
 * @return リソースが示すイメージ
 */
fun String.getResourceAsImage(): Image = MainApp::class.java.classLoader.getResourceAsStream(this).use { Image(it) }

/**
 * [Tooltip]に非表示機能が無いため追加。内部ではopacityを変化させることで、表示・非表示を切り替えている
 */
var Tooltip.isVisible: Boolean
    get() = opacity == 1.0
    set(value) = if (value) opacity = 1.0 else opacity = 0.0

/**
 * 自サーバに接続できない時に表示するダイアログの初期設定をする。
 */
fun Alert.showConnectionError() {
    this.apply {
        title = "インターネット接続がありません"
        headerText = null
        contentText = "サーバーに接続できませんでした。${System.lineSeparator()}インターネット接続環境を確認後、もう一度お試しください。"
        show()
    }
}
