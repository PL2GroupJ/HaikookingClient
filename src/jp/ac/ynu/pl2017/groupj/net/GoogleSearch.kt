package jp.ac.ynu.pl2017.groupj.net

import com.google.gson.Gson
import com.google.gson.JsonObject
import jp.ac.ynu.pl2017.groupj.util.SearchResult

/**
 * GoogleAPIのCustomSearchでパブリックドメインの画像を検索するクラス。
 * CustomSearchは一日に100件しか検索できないので、使いすぎに注意する。
 */
object GoogleSearch {
    private val apiKey: String = "AIzaSyD-ScaQf_PGrtne5ZU4SGdtAqjsdF7uUrs"
    private val engineId: String = "011887155026765679509:hu43zsi7b_m"
    private val apiUrl: String = "https://www.googleapis.com/customsearch/v1?"
    private val color: String = "color"             // color, gray, mono
    private val rights: String = "cc_publicdomain"  // クリエイティブ・コモンズ・ライセンスに準拠

    @JvmStatic fun search(query: String): List<SearchResult> {
        val urlString = apiUrl +
                "key=$apiKey" +
                "&cx=$engineId" +
                "&q=$query" +
                "&searchType=image" +
                "&imgColorType=$color" +
                "&rights=$rights" +
                "&lr=lang_ja"

        val response = HttpUtils.doGet(urlString)
        val gson = Gson()

        // itemsの部分だけ取り出して、リストで返す
        return gson.fromJson(response, JsonObject::class.java)
                .get("items")
                .asJsonArray
                .map { gson.fromJson(it, SearchResult::class.java) }
    }
}