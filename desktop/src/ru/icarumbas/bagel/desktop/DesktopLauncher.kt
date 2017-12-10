package ru.icarumbas.bagel.desktop

import com.badlogic.gdx.backends.lwjgl.LwjglApplication
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration
import org.jetbrains.annotations.Nullable
import ru.icarumbas.Bagel


object DesktopLauncher {
    @Nullable @JvmStatic @Override
    fun main(arg: Array<String>) {
        val config = LwjglApplicationConfiguration()
        LwjglApplication(Bagel(), config)
        config.width = 800
        config.height = 480
    }
}

