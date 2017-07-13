package jp.ac.ynu.pl2017.groupj.util

/**
 * 季節を表すenum。(SPRING, SUMMER, AUTUMN, WINTER, DEFAULT, NEW_YEAR)
 */
enum class Season {
    SPRING, SUMMER, AUTUMN, WINTER, DEFAULT, NEW_YEAR;

    override fun toString(): String {
        return when (this) {
            SPRING -> "春"
            SUMMER -> "夏"
            AUTUMN -> "秋"
            WINTER -> "冬"
            DEFAULT -> "四季"
            NEW_YEAR -> "新年"
        }
    }
}