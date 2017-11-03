package ru.icarumbas.bagel.screens.scenes


interface PlayerController {

    fun isUpPressed(): Boolean

    fun isDownPressed(): Boolean

    fun isLeftPressed(): Boolean

    fun isRightPressed(): Boolean
}