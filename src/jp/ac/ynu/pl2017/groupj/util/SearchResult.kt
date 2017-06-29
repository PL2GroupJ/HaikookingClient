package jp.ac.ynu.pl2017.groupj.util

/**
 * CustomSearchの結果を保存するクラス。
 */
data class SearchResult(
    val kind: String,
    val title: String,
    val htmlTitle: String,
    val link: String,
    val displayLink: String,
    val snippet: String,
    val htmlSnippet: String,
    val mime: String,
    val image: Image
) {
    data class Image(
        val contextLink: String,
        val height: Int,
        val width: Int,
        val byteSize: Int,
        val thumbnailLink: String,
        val thumbnailHeight: Int,
        val thumbnailWidth: Int
    )
}