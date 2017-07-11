package ru.icarumbas.bagel.Utils

import com.esotericsoftware.kryonet.Client
import com.esotericsoftware.kryonet.Connection
import com.esotericsoftware.kryonet.Listener


class Client {

    val client = Client()

    init {
        client.start()
        client.connect(5000, "localhost", 54555, 54777)
        client.sendTCP("Im client. You suck")

        client.addListener(object : Listener(){
            override fun received(connection: Connection?, `object`: Any?) {
                if (`object` is String) println(`object`)
            }
        })
    }
}