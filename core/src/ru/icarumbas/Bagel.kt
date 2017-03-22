package ru.icarumbas

import com.badlogic.gdx.Game

import ru.icarumbas.bagel.Screens.MainMenuScreen

class Bagel : Game() {
    override fun create() {
        setScreen(MainMenuScreen(this))
    }
}
