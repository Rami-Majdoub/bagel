package ru.icarumbas.bagel.desktop

import com.badlogic.gdx.backends.lwjgl.LwjglApplication
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration
import ru.icarumbas.Bagel


object DesktopLauncher{
    @JvmStatic fun main(Args: Array<String>){
        val config = LwjglApplicationConfiguration()
        config.width = 800
        config.height = 480

        LwjglApplication(Bagel(), config)
    }
}

