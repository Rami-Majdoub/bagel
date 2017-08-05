package ru.icarumbas.bagel.Utils

import java.io.DataInputStream
import java.io.DataOutputStream
import java.net.InetAddress
import java.net.Socket


class Client {

    val port = 6415
    val adress = "localhost"

    init {
        val inetAdress = InetAddress.getByName(adress)
        val socket = Socket(inetAdress, port)
        println("Connected")

        val input = DataInputStream(socket.getInputStream())
        val output = DataOutputStream(socket.getOutputStream())

        output.writeUTF(readLine())
        output.flush()

        println(input.readUTF())
    }
}