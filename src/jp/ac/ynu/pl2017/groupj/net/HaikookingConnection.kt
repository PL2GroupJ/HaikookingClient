package jp.ac.ynu.pl2017.groupj.net

import javafx.scene.image.Image
import jp.ac.ynu.pl2017.groupj.util.ConnectionCommand
import jp.ac.ynu.pl2017.groupj.util.toImage
import java.io.DataInputStream
import java.io.DataOutputStream
import java.net.Socket

class HaikookingConnection {
    private val ip = "localhost"
    private val port = 9999
    private val socket: Socket?
    private val input: DataInputStream?
    private val output: DataOutputStream?

    init {
        socket = Socket(ip, port)
        input = DataInputStream(socket.getInputStream())
        output = DataOutputStream(socket.getOutputStream())
    }

    fun writeHaiku(haiku: String) {
        sendCommand(ConnectionCommand.HAIKU)
        output!!.writeUTF(haiku)
    }

    fun readAdvice(): String {
        sendCommand(ConnectionCommand.ADVICE)
        return input!!.readUTF()
    }

    fun readNounList(): List<String> {
        sendCommand(ConnectionCommand.NOUN)
        return input!!.readUTF().split("/*/")
    }

    fun readImage(): Image {
        sendCommand(ConnectionCommand.IMAGE)
        val size = input!!.readInt()
        val bytes = ByteArray(size)
        socket!!.getInputStream().read(bytes)
        return bytes.toImage()
    }

    fun closeConnection() {
        sendCommand(ConnectionCommand.DISCONNECT)
        input?.close()
        output?.close()
        socket?.close()
    }

    private fun sendCommand(command: ConnectionCommand) = output!!.writeUTF(command.name)
}