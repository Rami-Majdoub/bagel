package ru.icarumbas.bagel.screens.scenes


interface UIController {

    fun attackPressed(): Boolean

    fun openPressed(): Boolean

    fun minimapPressed(): Boolean
}