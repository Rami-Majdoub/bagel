package ru.icarumbas

import com.badlogic.gdx.Game

import ru.icarumbas.bagel.Screens.MainMenuScreen

const val GROUND_BIT: Short = 2
const val PLATFORM_BIT: Short = 4
const val PLAYER_BIT: Short = 8

const val DEFAULT = 999
const val REG_ROOM_HEIGHT = 5.12f
const val REG_ROOM_WIDTH = 7.68f

class Bagel : Game() {

    override fun create() {
        setScreen(MainMenuScreen(this))
    }
}
