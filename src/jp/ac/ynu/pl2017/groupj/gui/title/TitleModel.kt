package jp.ac.ynu.pl2017.groupj.gui.title

import javafx.scene.canvas.GraphicsContext
import javafx.scene.paint.Color
import jp.ac.ynu.pl2017.groupj.gui.MainApp.Companion.HEIGHT as h
import jp.ac.ynu.pl2017.groupj.gui.MainApp.Companion.WIDTH as w

/**
 * タイトル画面のモデル。
 */
class TitleModel(val graphics: GraphicsContext) {

    /**
     * canvasの描画処理を行う
     */
    fun draw() {
        graphics.run {
//            fill = Color.GREEN
//            fillRect(0.0, 0.0, w, h)
        }
    }
}
