package ru.icarumbas

import com.badlogic.gdx.Game

import ru.icarumbas.bagel.Screens.MainMenuScreen

const val GROUND_BIT: Short = 2
const val PLATFORM_BIT: Short = 4
const val PLAYER_BIT: Short = 8
const val BOX_BIT: Short = 16

const val DEFAULT = 999
const val REG_ROOM_HEIGHT = 10.24f
const val REG_ROOM_WIDTH = 15.36f
const val PIX_PER_M = 100f

class Bagel : Game() {

    override fun create() {
        setScreen(MainMenuScreen(this))
    }
}
