package ru.icarumbas.bagel.view.screens

import com.badlogic.gdx.ScreenAdapter
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver
import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.maps.tiled.TmxMapLoader
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.utils.viewport.ExtendViewport
import ru.icarumbas.Bagel
import ru.icarumbas.TILED_MAPS_TOTAL
import ru.icarumbas.bagel.view.actors.LoadingBar


class LoadingScreen(private val game: Bagel) : ScreenAdapter() {

    private var stage = Stage(ExtendViewport(800f, 480f))

    private val loadingBar: LoadingBar

    private val loadingPack = TextureAtlas("Packs/LoadingScreen.pack")

    private val loadingBarHidden: Image
    private val loadingBg: Image
    private val loadingFrame: Image
    private val logo: Image
    private val screenBg: Image

    private var startX = 0f
    private var endX = 0f
    private var percent = 0f

    init {

        // Grab the regions from the atlas and create some images
        logo = Image(loadingPack.findRegion("IcaIcon"))
        loadingFrame = Image(loadingPack.findRegion("loading-frame"))
        loadingBarHidden = Image(loadingPack.findRegion("loading-bar-hidden"))
        screenBg = Image(loadingPack.findRegion("screen-bg"))
        loadingBg = Image(loadingPack.findRegion("loading-frame-bg"))

        // Add the loading bar animation
        val anim = Animation(0.05f, loadingPack.findRegions("loading-bar-anim"), PlayMode.LOOP_REVERSED)
        loadingBar = LoadingBar(anim)

        // Add all the actors to the stage
        stage.addActor(screenBg)
        stage.addActor(loadingBar)
        stage.addActor(loadingBg)
        stage.addActor(loadingBarHidden)
        stage.addActor(loadingFrame)
        stage.addActor(logo)

        // Rooms
        game.assetManager.setLoader(TiledMap::class.java, TmxMapLoader(InternalFileHandleResolver()))
        (0 until TILED_MAPS_TOTAL).forEach {
            game.assetManager.load("Maps/Map$it.tmx", TiledMap::class.java, TmxMapLoader.Parameters().apply {
                generateMipMaps = true
            })
        }

        // Texture Atlases
        game.assetManager.load("Packs/GuyKnight.pack", TextureAtlas::class.java)
        game.assetManager.load("Packs/Main_Menu.txt", TextureAtlas::class.java)
        game.assetManager.load("Packs/items.pack", TextureAtlas::class.java)
        game.assetManager.load("Packs/Enemies/Skeleton.pack", TextureAtlas::class.java)
        game.assetManager.load("Packs/Enemies/Golem.pack", TextureAtlas::class.java)
        game.assetManager.load("Packs/Enemies/Vamp.pack", TextureAtlas::class.java)
        game.assetManager.load("Packs/Enemies/Zombie.pack", TextureAtlas::class.java)
        game.assetManager.load("Packs/weapons.pack", TextureAtlas::class.java)
        game.assetManager.load("Packs/Enemies/MiniDragon.pack", TextureAtlas::class.java)
        game.assetManager.load("Packs/minimap.pack", TextureAtlas::class.java)
        game.assetManager.load("Packs/UI.pack", TextureAtlas::class.java)

        // Make the background fill the screen
        screenBg.setSize(stage.width, stage.height)

        // Place the logo in the middle of the screen and 100 px up
        logo.x = (stage.width - logo.width) / 2
        logo.y = (stage.height - logo.height) / 2 + 100

        // Place the loading frame in the middle of the screen
        loadingFrame.x = (stage.width - loadingFrame.width) / 2
        loadingFrame.y = (stage.height - loadingFrame.height) / 2

        // Place the loading bar at the same spot as the frame, adjusted a few px
        loadingBar.x = loadingFrame.x + 15
        loadingBar.y = loadingFrame.y + 5

        // Place the image that will hide the bar on top of the bar, adjusted a few px
        loadingBarHidden.x = loadingBar.x + 35
        loadingBarHidden.y = loadingBar.y - 3

        // The start position and how far to move the hidden loading bar
        startX = loadingBarHidden.x
        endX = 440f

        // The rest of the hidden bar
        loadingBg.setSize(450f, 50f)
        loadingBg.x = loadingBarHidden.x + 30
        loadingBg.y = loadingBarHidden.y + 3
        loadingBg.invalidate()
    }

    override fun render(delta: Float) {

        if (game.assetManager.update()) {
            game.screen = MainMenuScreen(game)
        }

        // Interpolate the percentage to make it more smooth
        percent = Interpolation.linear.apply(percent, game.assetManager.progress, 0.1f)

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