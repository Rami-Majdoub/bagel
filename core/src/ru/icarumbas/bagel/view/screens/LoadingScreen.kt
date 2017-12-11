package ru.icarumbas.bagel.view.screens

import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.utils.viewport.ExtendViewport
import ktx.app.KtxScreen
import ru.icarumbas.Bagel
import ru.icarumbas.bagel.engine.resources.ResourceManager
import ru.icarumbas.bagel.view.ui.actors.LoadingBar


class LoadingScreen(private val game: Bagel) : KtxScreen {

    private val loadingPack = TextureAtlas("Packs/LoadingScreen.pack")

    private var stage = Stage(ExtendViewport(800f, 480f))

    private val screenBg = Image(loadingPack.findRegion("screen-bg")).apply {
        setSize(stage.width, stage.height)
    }

    private val logo = Image(loadingPack.findRegion("IcaIcon")).apply {
        x = (stage.width - width) / 2
        y = (stage.height - height) / 2 + 100
    }

    private val loadingFrame = Image(loadingPack.findRegion("loading-frame")).apply {
        x = (stage.width - width) / 2
        y = (stage.height - height) / 2
    }

    private val loadingBar = LoadingBar(
            Animation(0.05f, loadingPack.findRegions("loading-bar-anim"), PlayMode.LOOP_REVERSED)).apply {
        x = loadingFrame.x + 15
        y = loadingFrame.y + 5
    }

    private val loadingBarHidden = Image(
            loadingPack.findRegion("loading-bar-hidden")).apply {
        x = loadingBar.x + 35
        y = loadingBar.y - 3
    }

    /* The rest of the hidden bar */
    private val loadingBg = Image(loadingPack.findRegion("loading-frame-bg")).apply {
        setSize(450f, 50f)
        x = loadingBarHidden.x + 30
        y = loadingBarHidden.y + 3
        invalidate()
    }

    /* The start position and how far to move the hidden loading bar */
    private var startX = loadingBarHidden.x
    private var endX = 440f
    private var percent = 0f

    init {
        ResourceManager.loadAssets()

        with (stage) {
            addActor(screenBg)
            addActor(loadingBar)
            addActor(loadingBg)
            addActor(loadingBarHidden)
            addActor(loadingFrame)
            addActor(logo)
        }
    }

    override fun render(delta: Float) {

        if (ResourceManager.assets.update()) {
            game.setScreen<MainMenuScreen>()
        }

        // Interpolate the percentage to make it more smooth
        percent = Interpolation.linear.apply(percent, ResourceManager.assets.progress, 0.1f)

        loadingBarHidden.x = startX + endX * percent
        loadingBg.x = loadingBarHidden.x + 30
        loadingBg.width = 450 - 450 * percent

        stage.draw()
        stage.act()
    }

    override fun resize(width: Int, height: Int) {
        stage.viewport.update(width , height, false)
    }

    override fun dispose() {
        stage.dispose()
        loadingPack.dispose()
    }
}