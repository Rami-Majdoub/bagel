package ru.icarumbas

import com.badlogic.gdx.Game
import ru.icarumbas.bagel.engine.resources.ResourceManager
import ru.icarumbas.bagel.view.screens.LoadingScreen


class Bagel : Game() {

    lateinit var assets: ResourceManager

    override fun create(){
        assets = ResourceManager()

        setScreen(LoadingScreen(assets, this))
    }

    override fun dispose() {
        this.dispose()
        assets.assetManager.dispose()
    }
}
