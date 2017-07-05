package jp.ac.ynu.pl2017.groupj.gui.haiku

import javafx.beans.property.SimpleStringProperty

/**
 * 俳句作成画面のモデル。
 */
class HaikuModel {
    val haiku = SimpleStringProperty()

    fun generateImage() {
        // TODO: 進捗を表示できるようにする
        println(haiku.value)
    }
}