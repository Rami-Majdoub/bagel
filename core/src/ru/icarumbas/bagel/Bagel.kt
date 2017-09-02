package ru.icarumbas

import com.badlogic.gdx.Game
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.assets.loaders.SoundLoader
import com.badlogic.gdx.assets.loaders.TextureAtlasLoader
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver
import com.badlogic.gdx.audio.Sound
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.maps.tiled.TmxMapLoader
import ru.icarumbas.bagel.WorldIO
import ru.icarumbas.bagel.screens.MainMenuScreen

const val GROUND_BIT: Short = 2
const val PLATFORM_BIT: Short = 4
const val PLAYER_BIT: Short = 8
const val PLAYER_WEAPON_BIT: Short = 16
const val KEY_OPEN_BIT: Short = 32
const val BREAKABLE_BIT: Short = 64
const val AI_WEAPON_BI: Short = 128
const val AI_BIT: Short = 256
const val TAKE_BIT: Short = 512
const val STATIC_BIT: Short = 1024


const val PIX_PER_M = 100f
const val REG_ROOM_HEIGHT = 768f.div(PIX_PER_M)
const val REG_ROOM_WIDTH = 1152f.div(PIX_PER_M)
const val TILED_MAPS_TOTAL = 2


class Bagel: Game() {


    lateinit var worldIO : WorldIO
    lateinit var assetManager : AssetManager

    override fun create() {
        // Asset manager loading
        assetManager = AssetManager()

        // Rooms
        assetManager.setLoader(TiledMap::class.java, TmxMapLoader(InternalFileHandleResolver()))
        (0 until TILED_MAPS_TOTAL).forEach {
            assetManager.load("Maps/New/map$it.tmx", TiledMap::class.java)
        }

        // Texture Atlases
        assetManager.setLoader(TextureAtlas::class.java, TextureAtlasLoader(InternalFileHandleResolver()))
        assetManager.load("Packs/RoomObjects.txt", TextureAtlas::class.java)
        assetManager.load("Packs/GuyKnight.pack", TextureAtlas::class.java)
        assetManager.load("Packs/Main_Menu.txt", TextureAtlas::class.java)
        assetManager.load("Packs/Enemies.txt", TextureAtlas::class.java)
        assetManager.load("Packs/items.pack", TextureAtlas::class.java)




        // Sounds
        assetManager.setLoader(Sound::class.java, SoundLoader(InternalFileHandleResolver()))
        assetManager.load("Sounds/openchest.wav", Sound::class.java)
        assetManager.load("Sounds/coinpickup.wav", Sound::class.java)
        assetManager.load("Sounds/spikes.wav", Sound::class.java)
        assetManager.load("Sounds/shatterMetal.wav", Sound::class.java)
        assetManager.load("Sounds/crateBreak0.wav", Sound::class.java)
        assetManager.load("Sounds/crateBreak1.wav", Sound::class.java)
        assetManager.load("Sounds/steps.wav", Sound::class.java)
        assetManager.load("Sounds/sword.wav", Sound::class.java)


        assetManager.finishLoading()

        worldIO = WorldIO()
        setScreen(MainMenuScreen(this))
    }

    override fun dispose() {
        assetManager.dispose()
    }
}
