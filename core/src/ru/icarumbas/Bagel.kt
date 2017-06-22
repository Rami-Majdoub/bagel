package ru.icarumbas

import com.badlogic.gdx.Game
import ru.icarumbas.bagel.Screens.MainMenuScreen

const val GROUND_BIT: Short = 2
const val PLATFORM_BIT: Short = 4
const val PLAYER_BIT: Short = 8
const val CHEST_BIT: Short = 16
const val BREAKABLE_BIT: Short = 32
const val SPIKE_BIT: Short = 64
const val ENEMY_BIT: Short = 128
const val COIN_BIT: Short = 256
const val PORTAL_DOOR_BIT: Short = 512


const val REG_ROOM_HEIGHT = 10.24f
const val REG_ROOM_WIDTH = 15.36f
const val PIX_PER_M = 100f
const val TILED_MAPS_TOTAL = 4


class Bagel : Game() {

    override fun create() {
        setScreen(MainMenuScreen(this))
    }
}
