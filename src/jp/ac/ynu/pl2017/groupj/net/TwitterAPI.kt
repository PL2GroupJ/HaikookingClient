package jp.ac.ynu.pl2017.groupj.net

import com.google.gson.Gson
import com.google.gson.JsonObject
import javafx.scene.image.Image
import jp.ac.ynu.pl2017.groupj.util.TokenPair
import jp.ac.ynu.pl2017.groupj.util.TwitterUser
import jp.ac.ynu.pl2017.groupj.util.toByteArray
import java.net.URLEncoder
import java.util.*
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

object TwitterAPI {
    private val apiKey = "bVGE7lvasH8CgKwZo8CwwpSId"
    private val apiSecret = "BQ9GL8jb3QU1CxUD4GuJjhawLMUzbCOvYCiKeCKRAwB8VTGR46"
    private val callbackUrl = "https://twitter.com/pl2groupj2"
    private val requestUrl = "https://api.twitter.com/oauth/request_token"
    private val authorizeUrl = "https://api.twitter.com/oauth/authorize"
    private val accessUrl = "https://api.twitter.com/oauth/access_token"
    private val accountUrl = "https://api.twitter.com/1.1/account/verify_credentials.json"
    private val tweetUrl = "https://api.twitter.com/1.1/statuses/update.json"
    private val mediaUrl = "https://upload.twitter.com/1.1/media/upload.json"
    private val params = sortedMapOf(
            "oauth_consumer_key" to encode(apiKey),
            "oauth_signature_method" to encode("HMAC-SHA1"),
            "oauth_timestamp" to encode((System.currentTimeMillis() / 1000).toString()),    // 通信時ごとに良いかもしれない
            "oauth_nonce" to encode(System.nanoTime().toString()),
            "oauth_version" to encode("1.0"))   // paramsは使いまわすので、新しく追加したものは削除する必要がある

    /**
     * Twitterにリクエストトークンを要求する。
     * @return リクエストトークン[TokenPair]
     */
    @JvmStatic fun loadRequestToken(): TokenPair {
        params.put("oauth_signature", encode(generateSignature(requestUrl, "", "POST")))

        val oauth = params.map { (key, value) -> "$key=$value" }.joinToString(separator = ",")
        val res = HttpUtils.doPost(requestUrl, listOf("Authorization" to "OAuth $oauth")).split("&")

        params.remove("oauth_signature")
        return TokenPair(token = res[0].split("=")[1], tokenSecret = res[1].split("=")[1])
    }

    /**
     * コールバックで戻ってきたURLを解析し、トークンを生成する。
     * @param callbackUrl OAuth認証のコールバックのURL
     * @return オーソライズトークン(oauth_verifierをtokenSecretに入れたもの)[TokenPair] 。認証が拒否されたならnull
     */
    @JvmStatic fun loadAuthorizeToken(callbackUrl: String): TokenPair? {
        // 認証または拒否された場合で分岐
        if (callbackUrl.startsWith("${this.callbackUrl}?oauth_token=")) {
            val res = callbackUrl.substringAfter("${this.callbackUrl}?").split("&")
            return TokenPair(token = res[0].split("=")[1], tokenSecret = res[1].split("=")[1])
        }
        else {
            return null
        }
    }

    /**
     * Twitterにアクセストークンを要求する。オーソライズトークンは一度しか使えないことに注意する。
     * @param tokenPair オーソライズトークン(oauth_verifierをtokenSecretに入れたもの)
     * @return アクセストークン[TokenPair]
     */
    @JvmStatic fun loadAccessToken(tokenPair: TokenPair): TokenPair {
        params.apply {
            put("oauth_token", encode(tokenPair.token))
            put("oauth_verifier", encode(tokenPair.tokenSecret))
            put("oauth_signature", encode(generateSignature(accessUrl, "", "POST")))
        }

        val oauth = params.map { (key, value) -> "$key=$value" }.joinToString(separator = ",")
        val res = HttpUtils.doPost(accessUrl, listOf("Authorization" to "OAuth $oauth")).split("&")

        params.apply {
            remove("oauth_token")
            remove("oauth_verifier")
            remove("oauth_signature")
        }
        return TokenPair(token = res[0].split("=")[1], tokenSecret = res[1].split("=")[1])
    }

    /**
     * OAuth認証を行うためのURLを取得する。
     * @return OAuth認証を行うURL
     */
    @JvmStatic fun loadAuthorizeUrl(): String {
        return "$authorizeUrl?oauth_token=${loadRequestToken().token}"
    }

    /**
     * ユーザデータをロードする。
     * @param tokenPair アクセストークン
     * @return ユーザデータ[TwitterUser]
     */
    @JvmStatic fun loadUser(tokenPair: TokenPair): TwitterUser {
        params.apply {
            put("oauth_token", encode(tokenPair.token))
            put("oauth_signature", encode(generateSignature(accountUrl, tokenPair.tokenSecret, "GET")))
        }

        val oauth = params.map { (key, value) -> "$key=$value" }.joinToString(separator = ",")
        val res = HttpUtils.doGet(accountUrl, listOf("Authorization" to "OAuth $oauth"))
        val json = Gson().fromJson(res, JsonObject::class.java)

        params.apply {
            remove("oauth_token")
            remove("oauth_signature")
        }
        return TwitterUser(json.get("name").asString,
                           json.get("screen_name").asString,
                           HttpUtils.downloadImage(json.get("profile_image_url_https").asString)!!,
                           tokenPair)
    }

    /**
     * ツイートをする。140文字まで。同じツイートを連続して行うとすると403が返ってくる。
     * @param tokenPair アクセストークン
     * @param status ツイート内容
     * @return ツイート成功の可否
     */
    @JvmStatic fun tweet(tokenPair: TokenPair, status: String, mediaId: String = ""): Boolean {
        params.apply {
            put("oauth_token", encode(tokenPair.token))
            put("status", encode(status))
            if (mediaId.isNotEmpty())
                put("media_ids", encode(mediaId))
            put("oauth_signature", encode(generateSignature(tweetUrl, tokenPair.tokenSecret, "POST")))
        }

        val oauth = params.map { (key, value) -> "$key=$value" }.joinToString(separator = ",")
        val body = "status=${encode(status)}" + if (mediaId.isNotEmpty()) "&media_ids=${encode(mediaId)}" else ""
        val res = HttpUtils.doPost(tweetUrl, listOf("Authorization" to "OAuth $oauth"), body)

        params.apply {
            remove("oauth_token")
            remove("status")
            if (mediaId.isNotEmpty())
                remove("media_ids")
            remove("oauth_signature")
        }
        return res.isNotEmpty() // レスポンスコードが200で無いならresは空になるため
    }

    /**
     * 画像をアップロードする。画像つきでツイートするためには、まずこのメソッドで画像をアップロードする必要がある。
     * @param tokenPair アクセストークン
     * @return メディアID
     */
    @JvmStatic fun uploadImage(tokenPair: TokenPair, image: Image): String {
        params.apply {
            put("oauth_token", encode(tokenPair.token))
            put("oauth_signature", encode(generateSignature(mediaUrl, tokenPair.tokenSecret, "POST")))
        }

        val boundary = "*---------------123454321"
        val body = generateBody(image.toByteArray(), boundary)
        val oauth = params.map { (key, value) -> "$key=$value" }.joinToString(separator = ",")
        val headers = listOf("Authorization" to "OAuth $oauth",
                             "Content-Type"  to "multipart/form-data; boundary=$boundary")
        val res = HttpUtils.doPost(mediaUrl, headers, body)
        val json = Gson().fromJson(res, JsonObject::class.java)

        params.apply {
            remove("oauth_token")
            remove("oauth_signature")
        }
        return json.get("media_id").asString
    }

    // 電子著名の作成
    private fun generateSignature(url: String, oauthTokenSecret: String, postType: String): String {
        val requestParams = params.map { (key, value) -> "$key=$value" }.joinToString(separator = "&")
        val signatureKey = "${encode(apiSecret)}&${encode(oauthTokenSecret)}"
        val signatureData = "${encode(postType)}&${encode(url)}&${encode(requestParams)}"
        val signingKey = SecretKeySpec(signatureKey.toByteArray(), "HmacSHA1")
        val rawHmac  = Mac.getInstance(signingKey.algorithm).run {
            init(signingKey)
            doFinal(signatureData.toByteArray())
        }
        return Base64.getEncoder().encodeToString(rawHmac)
    }

    // Http通信のボディを作成。MultipartPOSTに利用
    private fun generateBody(data: ByteArray, boundary: String): String {
        val hyphen = "--"
        val newLine = "\r\n"
        return  "$hyphen$boundary$newLine" +
                "Content-Disposition: form-data; name=\"media_data\"; $newLine$newLine" +
                "${Base64.getEncoder().encodeToString(data)}$newLine" +
                "$hyphen$boundary$hyphen$newLine"
    }

    // URLエンコード。半角スペースと~は+と%7Eにエンコードされてしまうので、適切に変換し直す
    private fun encode(s: String): String = URLEncoder.encode(s, "UTF-8").replace("+", "%20").replace("%7E", "~")
}