package ru.icarumbas

import com.badlogic.gdx.Game
import com.badlogic.gdx.assets.AssetManager
import ru.icarumbas.bagel.WorldIO
import ru.icarumbas.bagel.screens.LoadingScreen

const val GROUND_BIT: Short = 2
const val PLATFORM_BIT: Short = 4
const val PLAYER_BIT: Short = 8
const val PLAYER_FEET_BIT: Short = 16
const val KEY_OPEN_BIT: Short = 32
const val BREAKABLE_BIT: Short = 64
const val AI_BIT: Short = 256
const val WEAPON_BIT: Short = 512
const val STATIC_BIT: Short = 1024
const val SHARP_BIT: Short = 2048


const val PIX_PER_M = 100f
const val REG_ROOM_HEIGHT = 768f.div(PIX_PER_M)
const val REG_ROOM_WIDTH = 1152f.div(PIX_PER_M)
const val TILED_MAPS_TOTAL = 20


//TODO("Blood, stones, effects")
//TODO("Golems, skeletons not affected by spikes. Golem destroys them')


class Bagel : Game() {


    lateinit var worldIO : WorldIO
    lateinit var assetManager : AssetManager

    override fun create() {
        // Asset manager loading
        assetManager = AssetManager()

        worldIO = WorldIO()
        setScreen(LoadingScreen(this))
    }

    override fun dispose() {
        assetManager.dispose()
    }
}
