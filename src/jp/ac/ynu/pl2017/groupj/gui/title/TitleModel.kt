package jp.ac.ynu.pl2017.groupj.gui.title

import javafx.animation.AnimationTimer
import javafx.scene.canvas.GraphicsContext
import jp.ac.ynu.pl2017.groupj.anim.Particle
import jp.ac.ynu.pl2017.groupj.util.Season
import jp.ac.ynu.pl2017.groupj.util.User
import jp.ac.ynu.pl2017.groupj.util.getSeason
import java.util.*
import jp.ac.ynu.pl2017.groupj.gui.MainApp.Companion.HEIGHT as h
import jp.ac.ynu.pl2017.groupj.gui.MainApp.Companion.WIDTH as w

/**
 * タイトル画面のモデル。
 */
class TitleModel {
    private lateinit var anim: AnimationTimer

    /**
     * canvasの描画処理を行う
     */
    fun startAnimation(g: GraphicsContext) {
        val season = if (User.season == Season.DEFAULT) Calendar.getInstance().getSeason() else User.season
        when (season) {
            Season.SPRING, Season.AUTUMN, Season.WINTER -> drawParticle(g)
            Season.SUMMER -> anim = object : AnimationTimer() { override fun handle(now: Long) {} }
            else -> anim = object : AnimationTimer() { override fun handle(now: Long) {} }
        }
        anim.start()
    }

    fun stopAnimation() {
        anim.stop()
    }

    private fun drawParticle(g: GraphicsContext) {
        val particleList = mutableListOf<Particle>()
        val random = Random()
        init(particleList, random)
        anim = object : AnimationTimer() {
            override fun handle(now: Long) {
                g.clearRect(0.0, 0.0, w, h)
                drop(g, particleList, random)
            }
        }
    }

    private fun init(particleList: MutableList<Particle>, random: Random) {
        repeat(20) { particleList.add(Particle(random.nextDouble() * w - 40 to random.nextDouble() * h -40.0,
                random.nextDouble() * 0.8 - 0.4 to random.nextDouble() * 1.5 + 0.4,
                0.001 to (random.nextDouble() + 0.5) * 0.01,
                random.nextDouble() + 0.8,
                User.season))}
    }

    private fun drop(g: GraphicsContext, particleList: MutableList<Particle>, random: Random) {
        if (random.nextDouble() < 0.05) {
            particleList.add(Particle(
                    random.nextDouble() * w - 40 to -40.0,
                    random.nextDouble() * 0.8 - 0.4 to random.nextDouble() * 1.5 + 0.4,
                    0.001 to (random.nextDouble() + 0.5) * 0.01,
                    random.nextDouble() + 0.8,
                    User.season))
        }
        var deleteI = -1
        particleList.forEachIndexed { i, petal ->
            petal.nextFrame()
            petal.draw(g)
            if (petal.canDelete(h)) deleteI = i
        }
        if (deleteI != -1) particleList.removeAt(deleteI)
    }
}
