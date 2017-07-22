package jp.ac.ynu.pl2017.groupj.gui.title

import javafx.animation.AnimationTimer
import javafx.scene.canvas.GraphicsContext
import jp.ac.ynu.pl2017.groupj.anim.Petal
import java.util.*
import jp.ac.ynu.pl2017.groupj.gui.MainApp.Companion.HEIGHT as h
import jp.ac.ynu.pl2017.groupj.gui.MainApp.Companion.WIDTH as w

/**
 * タイトル画面のモデル。
 */
class TitleModel {
    private lateinit var anim: AnimationTimer
    private val random = Random()
    private val petalList = mutableListOf<Petal>()

    /**
     * canvasの描画処理を行う
     */
    fun startAnimation(g: GraphicsContext) {
        init()
        anim = object : AnimationTimer() {
            override fun handle(now: Long) {
                g.clearRect(0.0, 0.0, w, h)
                drop(g)
            }
        }
        anim.start()
    }

    fun stopAnimation() {
        anim.stop()
    }

    private fun init() {
        repeat(20) { petalList.add(Petal(random.nextDouble() * w - 40 to random.nextDouble() * h -40.0,
                random.nextDouble() * 0.8 - 0.4 to random.nextDouble() * 1.5 + 0.4,
                0.001 to (random.nextDouble() + 0.5) * 0.01,
                random.nextDouble() + 0.8))}
    }

    private fun drop(g: GraphicsContext) {
        if (random.nextDouble() < 0.05) {
            petalList.add(Petal(
                    random.nextDouble() * w - 40 to -40.0,
                    random.nextDouble() * 0.8 - 0.4 to random.nextDouble() * 1.5 + 0.4,
                    0.001 to (random.nextDouble() + 0.5) * 0.01,
                    random.nextDouble() + 0.8))
        }
        var deleteI = -1
        petalList.forEachIndexed { i, petal ->
            petal.nextFrame()
            petal.draw(g)
            if (petal.canDelete(h)) deleteI = i
        }
        if (deleteI != -1) petalList.removeAt(deleteI)
    }
}
