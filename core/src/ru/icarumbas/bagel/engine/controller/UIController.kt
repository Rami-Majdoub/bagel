package ru.icarumbas.bagel.engine.controller


interface UIController {

    fun isAttackPressed(): Boolean

    fun isOpenPressed(): Boolean

    fun isMinimapPressed(): Boolean
}