package jp.ac.ynu.pl2017.groupj.util

import javafx.scene.image.Image

/**
 * Twitterのユーザデータを保存するクラス。
 */
data class TwitterUser(
    val name: String,
    val screenName: String,
    val profileImage: Image,
    val tokenPair: TokenPair
)