package jp.ac.ynu.pl2017.groupj.util

import javafx.scene.image.Image

/**
 * Twitterのユーザデータを保存するクラス。
 */
data class User(
    val name: String,
    val screenName: String,
    val profileImage: Image
) {
    companion object {
        var user: User? = null
        var token: Token? = null
    }
}