package ru.icarumbas

import ktx.app.KtxGame
import ktx.app.KtxScreen
import ru.icarumbas.bagel.view.screens.LoadingScreen


class Bagel : KtxGame<KtxScreen>() {

    override fun create() {
        addScreen(LoadingScreen(this))
    }

    override fun dispose() {
        this.dispose()
    }
}
