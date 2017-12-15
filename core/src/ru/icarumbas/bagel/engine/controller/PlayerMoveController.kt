package ru.icarumbas.bagel.engine.controller


interface PlayerMoveController {

    fun isUpPressed(): Boolean

    fun isDownPressed(): Boolean

    fun isLeftPressed(): Boolean

    fun isRightPressed(): Boolean

}