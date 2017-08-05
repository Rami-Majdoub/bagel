package ru.icarumbas.bagel.Utils

import java.io.DataInputStream
import java.io.DataOutputStream
import java.net.ServerSocket


class Server {

    val port = 6415

    init {
            val serverSocket = ServerSocket(port)
            println("Waiting for a client...")

            val socket = serverSocket.accept()
            println("Got it!")

            val input = DataInputStream(socket.getInputStream())
            val output = DataOutputStream(socket.getOutputStream())

            var line = ""
            while (true) {
                line = input.readUTF()
                println("It sent me this shit: $line")

                output.writeUTF("Take this back $line")
                output.flush()

            }

    }
}