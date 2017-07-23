package jp.ac.ynu.pl2017.groupj.gui.title

import javafx.animation.AnimationTimer
import javafx.scene.canvas.GraphicsContext
import jp.ac.ynu.pl2017.groupj.anim.Particle
import jp.ac.ynu.pl2017.groupj.anim.WindBell
import jp.ac.ynu.pl2017.groupj.util.Season
import jp.ac.ynu.pl2017.groupj.util.User
import jp.ac.ynu.pl2017.groupj.util.getSeason
import jp.ac.ynu.pl2017.groupj.util.pushPop
import java.util.*
import jp.ac.ynu.pl2017.groupj.gui.MainApp.Companion.HEIGHT as h
import jp.ac.ynu.pl2017.groupj.gui.MainApp.Companion.WIDTH as w

/**
 * タイトル画面のモデル。主に背景のアニメーションの描画の処理をする。
 */
class TitleModel {
    private lateinit var anim: AnimationTimer

    /**
     * canvasへの描画処理を開始。
     */
    fun startAnimation(g: GraphicsContext) {
        // 季節によって分岐する。夏だけパーティクルではない
        val season = if (User.season == Season.DEFAULT) Calendar.getInstance().getSeason() else User.season
        when (season) {
            Season.SPRING, Season.AUTUMN, Season.WINTER -> drawParticle(g)
            Season.SUMMER -> drawWindBell(g)
            else -> error("$season のアニメーションは存在しません")
        }
        anim.start()
    }

    /**
     * canvasへの描画処理を終了。
     */
    fun stopAnimation() {
        anim.stop()
    }

    // 風鈴の描画
    private fun 	drawWindBell(g: GraphicsContext) {
        val windBells = arrayOf(
                WindBell(0.01) { arg -> -8.0 * (Math.cos(arg) + Math.sin(arg * 2) / 2 + Math.cos(arg * 3) / 3) - 15.0},
                WindBell(0.015) { arg -> -8.0 * (Math.sin(arg) + Math.cos(arg * 2) / 2 + Math.sin(arg * 3) / 3) - 10.0},
                WindBell(0.01) { arg -> -6.0 * (Math.cos(arg) + Math.sin(arg * 2) / 3 + Math.cos(arg * 3) / 2) - 13.0},
                WindBell(0.008) { arg -> -7.0 * (Math.sin(arg) / 2 + Math.cos(arg * 2) / 3 + Math.sin(arg * 3)) - 14.0})
        anim = object : AnimationTimer() {
            override fun handle(now: Long) {
                windBells.forEach { it.nextFrame() }
                g.clearRect(0.0, 0.0, w, h)
                g.pushPop {
                    translate(220.0, 0.0)
                    scale(0.35, 0.35)
                    windBells[0].draw(this)
                }
                g.pushPop {
                    translate(170.0, 0.0)
                    scale(0.45, 0.45)
                    windBells[1].draw(this)
                }
                g.pushPop {
                    translate(100.0, 0.0)
                    scale(0.60, 0.60)
                    windBells[2].draw(this)
                }
                g.pushPop {
                    scale(0.8, 0.8)
                    windBells[3].draw(this)
                }
            }
        }
    }

    // パーティクルの描画
    private fun drawParticle(g: GraphicsContext) {
        val particleList = mutableListOf<Particle>()
        val random = Random()
        initParticle(particleList, random)
        anim = object : AnimationTimer() {
            override fun handle(now: Long) {
                var deleteI = -1
                if (random.nextDouble() < 0.05) { addParticle(particleList, random) }
                g.clearRect(0.0, 0.0, w, h)
                particleList.forEachIndexed { i, petal ->
                    petal.nextFrame()
                    petal.draw(g)
                    if (petal.canDelete(h)) deleteI = i
                }
                if (deleteI != -1) particleList.removeAt(deleteI)
            }
        }
    }

    private fun initParticle(particleList: MutableList<Particle>, random: Random) {
        repeat(20) { addParticle(particleList, random, random.nextDouble() * h)}
    }

    private fun addParticle(particleList: MutableList<Particle>, random: Random, height: Double = 0.0) {
        particleList.add(Particle(
                random.nextDouble() * w - 40 to height -40.0,
                random.nextDouble() * 0.8 - 0.4 to random.nextDouble() * 1.5 + 0.4,
                0.001 to (random.nextDouble() + 0.5) * 0.01,
                random.nextDouble() + 0.8,
                User.season))
    }
}
