package ru.icarumbas

import com.badlogic.gdx.Game
import ru.icarumbas.bagel.engine.io.WorldIO
import ru.icarumbas.bagel.engine.resources.ResourceManager
import ru.icarumbas.bagel.view.screens.LoadingScreen


class Bagel : Game() {

    lateinit var assets: ResourceManager
    lateinit var worldIO: WorldIO


    override fun create(){
        assets = ResourceManager()
        worldIO = WorldIO()

        setScreen(LoadingScreen(assets, this))
    }

    override fun dispose() {
        assets.assetManager.dispose()
    }
}
