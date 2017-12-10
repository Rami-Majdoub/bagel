package ru.icarumbas.bagel.engine.controller


interface PlayerController {

    fun isUpPressed(): Boolean

    fun isDownPressed(): Boolean

    fun isLeftPressed(): Boolean

    fun isRightPressed(): Boolean
}