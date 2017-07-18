package jp.ac.ynu.pl2017.groupj.gui.setting

import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.scene.control.RadioButton
import javafx.scene.control.Toggle
import jp.ac.ynu.pl2017.groupj.gui.MainApp
import jp.ac.ynu.pl2017.groupj.util.Season
import jp.ac.ynu.pl2017.groupj.util.User
import jp.ac.ynu.pl2017.groupj.util.write
import java.util.*

/**
 * 設定画面のモデル。
 */
class SettingModel {
    val selected = SimpleObjectProperty<Toggle>()   // 選択された季節のトグルボタン
    val advice = SimpleBooleanProperty()            // アドバイスのオンオフ

    /**
     * Twitter以外の設定を、Userオブジェクトとプロパティファイルに反映する。
     */
    fun save() {
        val season: Season = when((selected.value as RadioButton).text) {
            "春" -> Season.SPRING
            "夏" -> Season.SUMMER
            "秋" -> Season.AUTUMN
            "冬" -> Season.WINTER
            else -> Season.DEFAULT
        }

        User.season = season
        User.advice = advice.value

        Properties().write(MainApp.PROP_NAME,
                MainApp.SEASON to season.name,
                MainApp.ADVICE to advice.value.toString())
    }
}