package jp.ac.ynu.pl2017.groupj.anim

import javafx.scene.canvas.GraphicsContext
import jp.ac.ynu.pl2017.groupj.util.Season
import jp.ac.ynu.pl2017.groupj.util.getResourceAsImage
import jp.ac.ynu.pl2017.groupj.util.plus
import java.util.*

class Particle(
        private var pos: Pair<Double, Double>,
        private val dp: Pair<Double, Double>,
        private val da: Pair<Double, Double>,
        private val scale: Double,
        private val season: Season
) {
    private val particleI: Int
    private val kindI: Int
    private var angle = 0.0 to 0.0

    init {
        when (season) {
            Season.SPRING -> particleI = 0
            Season.AUTUMN -> particleI = 1
            Season.WINTER -> particleI = 2
            else -> error("$season のパーティクルは存在しません")
        }
        val rand = Random().nextDouble()
        if (rand < 0.6)       kindI = 0
        else if (rand < 0.85) kindI = 1
        else                  kindI = 2
    }

    fun nextFrame() {
        pos += dp
        angle += da
    }

    fun draw(g: GraphicsContext) {
        val dw = w.rotate(angle.first) * scale
        val dh = h.rotate(angle.second) * scale
        g.drawImage(particle[particleI][kindI], pos.first - dw / 2, pos.second - dh / 2, dw / 2, dh / 2)
    }

    fun canDelete(limitH: Double): Boolean {
        return pos.second > limitH + 10
    }

    private fun Double.rotate(angle: Double): Double = this * Math.cos(angle)

    companion object {
        private val petal = arrayOf(
                "image/anim/petal1.png".getResourceAsImage(),
                "image/anim/petal2.png".getResourceAsImage(),
                "image/anim/petal3.png".getResourceAsImage())
        private val leaf = arrayOf(
                "image/anim/leaf1.png".getResourceAsImage(),
                "image/anim/leaf2.png".getResourceAsImage(),
                "image/anim/leaf3.png".getResourceAsImage())
        private val snow = arrayOf(
                "image/anim/snow1.png".getResourceAsImage(),
                "image/anim/snow2.png".getResourceAsImage(),
                "image/anim/snow3.png".getResourceAsImage())
        private val particle = arrayOf(petal, leaf, snow)
        private val w = petal[0].width
        private val h = petal[0].height
    }
}
