package jp.ac.ynu.pl2017.groupj.anim

import javafx.scene.canvas.GraphicsContext
import jp.ac.ynu.pl2017.groupj.util.getResourceAsImage
import jp.ac.ynu.pl2017.groupj.util.plus
import java.util.*

class Petal(
        private var pos: Pair<Double, Double>,
        private val dp: Pair<Double, Double>,
        private val da: Pair<Double, Double>,
        private val scale: Double
) {
    private val index: Int
    private var angle = 0.0 to 0.0

    init {
        val rand = Random().nextDouble()
        if (rand < 0.6)       index = 0
        else if (rand < 0.85) index = 1
        else                  index = 2
    }

    fun nextFrame() {
        pos += dp
        angle += da
    }

    fun draw(g: GraphicsContext) {
        val dw = w.rotate(angle.first) * scale
        val dh = h.rotate(angle.second) * scale
        g.drawImage(petal[index], pos.first - dw / 2, pos.second - dh / 2, dw / 2, dh / 2)
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
        private val w = petal[0].width
        private val h = petal[0].height
    }
}
