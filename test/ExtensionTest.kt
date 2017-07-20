import jp.ac.ynu.pl2017.groupj.util.*
import org.junit.Test
import java.util.*
import kotlin.test.assertEquals

class ExtensionTest {
    @Test fun testGetSeason() {
        val calendar = Calendar.getInstance()

        calendar.set(Calendar.MONTH, 0)
        assertEquals(calendar.getSeason(), Season.WINTER)
        calendar.set(Calendar.MONTH, 1)
        assertEquals(calendar.getSeason(), Season.WINTER)
        calendar.set(Calendar.MONTH, 2)
        assertEquals(calendar.getSeason(), Season.SPRING)
        calendar.set(Calendar.MONTH, 3)
        assertEquals(calendar.getSeason(), Season.SPRING)
        calendar.set(Calendar.MONTH, 4)
        assertEquals(calendar.getSeason(), Season.SPRING)
        calendar.set(Calendar.MONTH, 5)
        assertEquals(calendar.getSeason(), Season.SUMMER)
        calendar.set(Calendar.MONTH, 6)
        assertEquals(calendar.getSeason(), Season.SUMMER)
        calendar.set(Calendar.MONTH, 7)
        assertEquals(calendar.getSeason(), Season.SUMMER)
        calendar.set(Calendar.MONTH, 8)
        assertEquals(calendar.getSeason(), Season.AUTUMN)
        calendar.set(Calendar.MONTH, 9)
        assertEquals(calendar.getSeason(), Season.AUTUMN)
        calendar.set(Calendar.MONTH, 10)
        assertEquals(calendar.getSeason(), Season.AUTUMN)
        calendar.set(Calendar.MONTH, 11)
        assertEquals(calendar.getSeason(), Season.WINTER)
    }

    @Test fun testToByteArrayAndToImage() {
        val image = "image/title.png".getResourceAsImage()

        val expected = image.toByteArray().toImage()

        assertEquals(expected.width, image.width)
        assertEquals(expected.height, image.height)
    }

    @Test fun testSplit() {
        val sizeList = listOf(10, 4000, 33, 902)
        val totalSize = sizeList.sum()
        val bytes = ByteArray(totalSize)

        val expected = bytes.split(sizeList.toIntArray()).sumBy { it.size }

        assertEquals(expected, totalSize)
    }
}