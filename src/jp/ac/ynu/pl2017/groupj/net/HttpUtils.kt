package jp.ac.ynu.pl2017.groupj.net

import javafx.scene.image.Image
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.InputStreamReader
import java.io.OutputStreamWriter
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
     * @param headers GETのヘッダーの(key, value)のリスト
     * @return GETのレスポンスの文字列
     */
    @JvmStatic fun doGet(urlString: String, headers: List<Pair<String, String>> = emptyList()): String {
        val url = URL(urlString)
        val con = when (url.protocol) {
            "http" -> url.openConnection() as HttpURLConnection
            "https" -> url.openConnection() as HttpsURLConnection
            else -> error("プロトコルにはhttpまたはhttpsを指定してください")
        }.apply {
            requestMethod = "GET"
            headers.forEach { (key, value) -> setRequestProperty(key, value) }
            instanceFollowRedirects = false
            readTimeout = 10000
            connectTimeout = 20000
            connect()
        }

        if (con.responseCode != HttpURLConnection.HTTP_OK) {
            println("通信失敗:${con.responseCode}")
            return ""
        }
        val res = BufferedReader(InputStreamReader(con.inputStream)).use { input -> input.readText() }
        con.disconnect()
        return res
    }

    /**
     * Http通信でPOSTを行う。
     * @param urlString POSTを行う対象のURLの文字列
     * @param headers POSTのヘッダーの(key, value)のリスト
     * @param body POSTのボディ
     * @return POSTのレスポンスの文字列
     */
    @JvmStatic fun doPost(urlString: String, headers: List<Pair<String, String>> = emptyList(), body: String = ""): String {
        val url = URL(urlString)
        val con = when (url.protocol) {
            "http" -> url.openConnection() as HttpURLConnection
            "https" -> url.openConnection() as HttpsURLConnection
            else -> error("プロトコルにはhttpまたはhttpsを指定してください")
        }.apply {
            requestMethod = "POST"
            doOutput = body.isNotEmpty()
            headers.forEach { (key, value) -> setRequestProperty(key, value) }
            instanceFollowRedirects = false
            readTimeout = 10000
            connectTimeout = 20000
            connect()
        }

        if (body.isNotEmpty())
            BufferedWriter(OutputStreamWriter(con.outputStream)).use { out -> out.write(body) }

        if (con.responseCode != HttpURLConnection.HTTP_OK) {
            println("通信失敗:${con.responseCode}")
            return ""
        }

        val res = BufferedReader(InputStreamReader(con.inputStream)).use { input -> input.readText() }
        con.disconnect()
        return res
    }

    /**
     * URLの画像をダウンロードする。URLによってはダウンロードできない場合もあることに注意。
     * @param urlString ダウンロードする画像のURLの文字列
     * @return その画像の[Image]のインスタンス
     */
    @JvmStatic fun downloadImage(urlString: String): Image? {
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
            return null
        }

        val image = con.inputStream.use { Image(it) }
        con.disconnect()
        return image
    }
}