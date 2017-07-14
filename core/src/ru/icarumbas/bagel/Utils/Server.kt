package ru.icarumbas.bagel.Utils

import com.esotericsoftware.kryonet.Connection
import com.esotericsoftware.kryonet.Listener
import com.esotericsoftware.kryonet.Server


class Server {

    val server = Server()

    init {
        server.start()
        server.bind(54555, 54777)

        server.addListener(object : Listener(){
            override fun received(connection: Connection, `object`: Any) {
                if (`object` is String) {
                    println(`object`)
                    connection.sendTCP("Fuck you")
                }

            }
        })
    }
}