package jp.ac.ynu.pl2017.groupj.net

import com.google.gson.Gson
import com.google.gson.JsonObject
import jp.ac.ynu.pl2017.groupj.util.Token
import jp.ac.ynu.pl2017.groupj.util.User
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
    private val params = sortedMapOf(
            "oauth_consumer_key" to encode(apiKey),
            "oauth_signature_method" to encode("HMAC-SHA1"),
            "oauth_timestamp" to encode((System.currentTimeMillis() / 1000).toString()),
            "oauth_nonce" to encode(System.nanoTime().toString()),
            "oauth_version" to encode("1.0"))   // paramsは使いまわすので、新しく追加したものは削除する必要がある

    /**
     * Twitterにリクエストトークンを要求する。
     * @return リクエストトークン[Token]
     */
    @JvmStatic fun loadRequestToken(): Token {
        params.put("oauth_signature", encode(generateSignature(requestUrl, "", "POST")))

        val header = params.map { (key, value) -> "$key=$value" }.joinToString(separator = ",")
        val res = HttpUtils.doPost(requestUrl, header).split("&")

        params.remove("oauth_signature")
        return Token(token = res[0].split("=")[1], tokenSecret = res[1].split("=")[1])
    }

    /**
     * コールバックで戻ってきたURLを解析し、トークンを生成する。
     * @param callbackUrl OAuth認証のコールバックのURL
     * @return オーソライズトークン(oauth_verifierをtokenSecretに入れたもの)[Token] 。認証が拒否されたならnull
     */
    @JvmStatic fun loadAuthorizeToken(callbackUrl: String): Token? {
        // 認証または拒否された場合で分岐
        if (callbackUrl.startsWith("${this.callbackUrl}?oauth_token=")) {
            val res = callbackUrl.substringAfter("${this.callbackUrl}?").split("&")
            return Token(token = res[0].split("=")[1], tokenSecret = res[1].split("=")[1])
        }
        else {
            return null
        }
    }

    /**
     * Twitterにアクセストークンを要求する。オーソライズトークンは一度しか使えないことに注意する。
     * @param token オーソライズトークン(oauth_verifierをtokenSecretに入れたもの)
     * @return アクセストークン[Token]
     */
    @JvmStatic fun loadAccessToken(token: Token): Token {
        params.apply {
            put("oauth_token", encode(token.token))
            put("oauth_verifier", encode(token.tokenSecret))
            put("oauth_signature", encode(generateSignature(accessUrl, "", "POST")))
        }

        val header = params.map { (key, value) -> "$key=$value" }.joinToString(separator = ",")
        val res = HttpUtils.doPost(accessUrl, header).split("&")

        params.apply {
            remove("oauth_token")
            remove("oauth_verifier")
            remove("oauth_signature")
        }
        return Token(token = res[0].split("=")[1], tokenSecret = res[1].split("=")[1])
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
     * @param token アクセストークン
     * @return ユーザデータ[User]
     */
    @JvmStatic fun loadUser(token: Token): User {
        params.apply {
            put("oauth_token", encode(token.token))
            put("oauth_signature", encode(generateSignature(accountUrl, token.tokenSecret, "GET")))
        }

        val header = params.map { (key, value) -> "$key=$value" }.joinToString(separator = ",")
        val res = HttpUtils.doGet(accountUrl, header)
        val json = Gson().fromJson(res, JsonObject::class.java)

        params.apply {
            remove("oauth_token")
            remove("oauth_signature")
        }
        return User(json.get("name").asString,
                    json.get("screen_name").asString,
                    HttpUtils.downloadImage(json.get("profile_image_url_https").asString)!!)
    }

    @JvmStatic fun tweet(status: String) {
        TODO("未実装")
    }

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

    private fun encode(s: String): String {
        return URLEncoder.encode(s, "UTF-8")
    }
}