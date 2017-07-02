package jp.ac.ynu.pl2017.groupj.util

import jp.ac.ynu.pl2017.groupj.gui.MainApp
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.*

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