package jp.ac.ynu.pl2017.groupj.util

/**
 * Twitterのユーザデータを保存するクラス。
 */
data class User(
        val accessToken: String,
        val accessTokenSecret: String,
        val userId: String,
        val screenName: String
) {
    companion object {
        lateinit var user: User
    }
}