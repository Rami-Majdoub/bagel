package ru.icarumbas.bagel.engine.controller

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input


class WASDPlayerController : PlayerMoveController{

    override fun isUpPressed(): Boolean {
        return Gdx.input.isKeyPressed(Input.Keys.SPACE)
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

}