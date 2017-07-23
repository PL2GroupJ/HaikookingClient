package jp.ac.ynu.pl2017.groupj.util

/**
 * ユーザデータを保存するオブジェクト。
 */
object User {
    var twitter: TwitterUser? = null
    var season: Season = Season.DEFAULT
    var advice: Boolean = true
    var font = ""
    var ipAddress: String = "localhost"
}
