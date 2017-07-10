package jp.ac.ynu.pl2017.groupj.util

import javafx.embed.swing.SwingFXUtils
import javafx.scene.image.Image
import jp.ac.ynu.pl2017.groupj.gui.MainApp
import java.io.ByteArrayOutputStream
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.*
import javax.imageio.ImageIO

/**
 * プロパティファイルを読み込んで、リストとして返す。
 * @param propertyFile 対象のプロパティファイルの名前
 * @param keys 読み込みたいプロパティのキーの可変長配列
 * @return keysに対応した順で読み込んだ、プロパティの値のリスト
 */
fun Properties.read(propertyFile: String, vararg keys: String): List<String?> {
    FileInputStream(propertyFile).use {
        load(it)
        return keys.map { getProperty(it) }
    }
}

/**
 * プロパティファイルに値を書き込む(新規or更新)
 * @param propertyFile 対象のプロパティファイルの名前
 * @param keyAndValues 書き込みたいプロパティの、キーと値のペアの可変長配列
 */
fun Properties.write(propertyFile: String, vararg keyAndValues: Pair<String, String>) {
    FileInputStream(MainApp.PROP).use {
        load(it)
        keyAndValues.forEach { (key, value) -> setProperty(key, value) }
        store(FileOutputStream(propertyFile),
                "add or update:${keyAndValues.map { (key, _) -> key }.joinToString(separator = ", ")}")
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
        store(FileOutputStream(MainApp.PROP), "delete:${keys.joinToString(separator = ", ")}")
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
 * @return バイト列
 */
fun Image.toByteArray(format: String = "png"): ByteArray {
    val bufferedImage = SwingFXUtils.fromFXImage(this, null)
    val out = ByteArrayOutputStream()
    ImageIO.write(bufferedImage, format, out)
    return out.toByteArray()
}