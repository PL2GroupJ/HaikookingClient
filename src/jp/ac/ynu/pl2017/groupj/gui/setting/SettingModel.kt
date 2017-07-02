package jp.ac.ynu.pl2017.groupj.gui.setting

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
    val selected = SimpleObjectProperty<Toggle>()

    fun save() {
        val season: Season = when((selected.value as RadioButton).text) {
            "春" -> Season.SPRING
            "夏" -> Season.SUMMER
            "秋" -> Season.AUTUMN
            "冬" -> Season.WINTER
            else -> Season.DEFAULT
        }
        User.season = season
        Properties().write(MainApp.PROP, MainApp.SEASON to season.name)
    }
}