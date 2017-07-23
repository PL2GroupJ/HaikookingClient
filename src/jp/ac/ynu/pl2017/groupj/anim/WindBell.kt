package jp.ac.ynu.pl2017.groupj.anim

import javafx.scene.canvas.GraphicsContext
import jp.ac.ynu.pl2017.groupj.util.getResourceAsImage
import jp.ac.ynu.pl2017.groupj.util.pushPop

class WindBell(
        val da: Double,
        val waveExpr: (Double) -> Double
) {
    private var arg = 0.0
    private var angle = 0.0

    fun nextFrame() {
        arg += da
        angle = waveExpr(arg)
    }

    fun draw(g: GraphicsContext) {
        g.pushPop {
            translate(50.0, 60.0)
            rotate(angle)
            drawImage(windBell[1], 0.0, 0.0)
        }
        g.pushPop {
            translate(0.0, -80.0)
            rotate(angle / 7)
            drawImage(windBell[0], 0.0, 0.0)
        }
    }

    companion object {
        private val windBell = arrayOf(
                "image/anim/wind_bell1.png".getResourceAsImage(),
                "image/anim/wind_bell2.png".getResourceAsImage())
    }
}