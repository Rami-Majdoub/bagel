package ru.icarumbas.bagel.engine.controller

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.scenes.scene2d.InputListener


class WASDControl() : PlayerController, UIController, InputListener(){


    override fun isUpPressed(): Boolean {
        return Gdx.input.isKeyPressed(Input.Keys.W)
    }

    override fun isDownPressed(): Boolean {
        return Gdx.input.isKeyPressed(Input.Keys.S)
    }

    override fun isLeftPressed(): Boolean {
        return Gdx.input.isKeyPressed(Input.Keys.A)
    }

    override fun isRightPressed(): Boolean {
        return Gdx.input.isKeyPressed(Input.Keys.D)
    }

    override fun isAttackPressed(): Boolean {
        return Gdx.input.isKeyPressed(Input.Keys.SHIFT_RIGHT)
    }

    override fun isOpenPressed(): Boolean {
        return Gdx.input.isKeyPressed(Input.Keys.E)
    }

    override fun isMinimapPressed(): Boolean {
        return Gdx.input.isKeyPressed(Input.Keys.Q)
    }
}