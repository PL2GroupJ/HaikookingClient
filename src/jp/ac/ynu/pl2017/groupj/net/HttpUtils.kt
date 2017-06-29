package jp.ac.ynu.pl2017.groupj.net

import javafx.scene.image.Image
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import javax.net.ssl.HttpsURLConnection

/**
 * Http通信を行うクラス。POSTとGETに利用する。
 */
object HttpUtils {

    /**
     * Http通信でGETを行う。
     * @param urlString GETを行う対象のURLの文字列
     * @return GETのレスポンスの文字列
     */
    @JvmStatic fun doGet(urlString: String): String {
        val url = URL(urlString)
        val con = when (url.protocol) {
            "http" -> url.openConnection() as HttpURLConnection
            "https" -> url.openConnection() as HttpsURLConnection
            else -> error("プロトコルにはhttpまたはhttpsを指定してください")
        }.apply {
            requestMethod = "GET"
            instanceFollowRedirects = false
            readTimeout = 10000
            connectTimeout = 20000
            connect()
        }

        if (con.responseCode != HttpURLConnection.HTTP_OK) {
            println("通信失敗:${con.responseCode}")
            return ""
        }

        val input = BufferedReader(InputStreamReader(con.inputStream))
        return input.readText()
    }

    /**
     * URLの画像をダウンロードする。
     * @param urlString ダウンロードする画像のURLの文字列
     * @return その画像の[Image]のインスタンス
     */
    @JvmStatic fun downloadImage(urlString: String): Image? {
        val url = URL(urlString)
        // TODO: Httpの場合に正しく読み込まれないバグあり。要修正
        val con = when (url.protocol) {
            "http" -> url.openConnection() as HttpURLConnection
            "https" -> url.openConnection() as HttpsURLConnection
            else -> error("プロトコルにはhttpまたはhttpsを指定してください")
        }.apply {
            requestMethod = "GET"
            instanceFollowRedirects = false
            readTimeout = 10000
            connectTimeout = 20000
            connect()
        }

        if (con.responseCode != HttpURLConnection.HTTP_OK) {
            println("通信失敗:${con.responseCode}")
            return null
        }

        return Image(con.inputStream)
    }

    /**
     * Http通信でPOSTを行う。
     * @param urlString POSTを行う対象のURLの文字列
     * @param header POSTのヘッダー(今のところOAuth専用)
     * @return POSTのレスポンスの文字列
     */
    @JvmStatic fun doPost(urlString: String, header: String): String {
        val url = URL(urlString)
        val con = when (url.protocol) {
            "http" -> url.openConnection() as HttpURLConnection
            "https" -> url.openConnection() as HttpsURLConnection
            else -> error("プロトコルにはhttpまたはhttpsを指定してください")
        }.apply {
            requestMethod = "POST"
            setRequestProperty("Authorization", "OAuth $header")
            instanceFollowRedirects = false
            readTimeout = 10000
            connectTimeout = 20000
            connect()
        }

        if (con.responseCode != HttpURLConnection.HTTP_OK) {
            println("通信失敗:${con.responseCode}")
            return ""
        }

        val input = BufferedReader(InputStreamReader(con.inputStream))
        return input.readText()
    }
}