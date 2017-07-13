package jp.ac.ynu.pl2017.groupj.net

import javafx.scene.image.Image
import jp.ac.ynu.pl2017.groupj.util.ConnectionCommand
import jp.ac.ynu.pl2017.groupj.util.Season
import jp.ac.ynu.pl2017.groupj.util.toImage
import java.io.DataInputStream
import java.io.DataOutputStream
import java.net.Socket

/**
 * 自サーバとのやり取りをするクラス。
 */
class HaikookingConnection {
    private val ip = "localhost"
    private val port = 9999
    private val socket: Socket?
    private val input: DataInputStream?
    private val output: DataOutputStream?
    private val separator = ":*:"

    init {
        socket = Socket(ip, port)
        input = DataInputStream(socket.getInputStream())
        output = DataOutputStream(socket.getOutputStream())
        // TODO: 送信失敗時などの処理を追加する
    }

    /**
     * 俳句を送信する。
     * @param haiku 送信する俳句の文字列
     */
    fun writeHaiku(haiku: String) {
        sendCommand(ConnectionCommand.HAIKU)
        output!!.writeUTF(haiku)
    }

    /**
     * 季語を受信する
     * @return [Season]
     */
    fun readSeason(): Season {
        sendCommand(ConnectionCommand.SEASON)
        return Season.valueOf(input!!.readUTF())
    }

    /**
     * アドバイスを受信する。
     * @return アドバイスの文字列
     */
    fun readAdvice(): String {
        sendCommand(ConnectionCommand.ADVICE)
        return input!!.readUTF()
    }

    /**
     * 名詞のリストを受信する。
     * @return 名詞の文字列のリスト
     */
    fun readNounList(): List<String> {
        sendCommand(ConnectionCommand.NOUN)
        return input!!.readUTF().split(separator)
    }

    /**
     * 画像を受信する。WordCloudの画像受信に利用。
     * @return 画像
     */
    fun readImage(): Image {
        sendCommand(ConnectionCommand.IMAGE)
        val size = input!!.readInt()
        val bytes = ByteArray(size)
        socket!!.getInputStream().read(bytes)
        return bytes.toImage()
    }

    /**
     * 接続を切断する。通信終了時に必ず呼ばなければならない。
     */
    fun closeConnection() {
        sendCommand(ConnectionCommand.DISCONNECT)
        input?.close()
        output?.close()
        socket?.close()
    }

    // 処理分岐のコマンド送信
    private fun sendCommand(command: ConnectionCommand) = output!!.writeUTF(command.name)
}