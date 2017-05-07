package ru.icarumbas

import com.badlogic.gdx.Game

import ru.icarumbas.bagel.Screens.MainMenuScreen

class Bagel : Game() {

    val GROUND_BIT: Short = 2
    val PLATFORM_BIT: Short = 4
    val PLAYER_BIT: Short = 8

    val DEFAULT = 999
    val REG_ROOM_HEIGHT = 5.12f
    val REG_ROOM_WIDTH = 7.68f

    override fun create() {
        setScreen(MainMenuScreen(this))
    }
}
