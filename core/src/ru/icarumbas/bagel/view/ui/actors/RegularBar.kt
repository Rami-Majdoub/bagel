package ru.icarumbas.bagel.view.ui.actors

import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Stage


class RegularBar {

    private val bar: Actor
    private val barForeground: Actor
    private val barBackGround: Actor

    var height = 0f
    var width = 0f

    var posX = 0f
    var posY = 0f


    constructor(bar: Actor, barForeground: Actor, barBackGround: Actor, stage: Stage) {
        this.bar = bar
        this.barForeground = barForeground
        this.barBackGround = barBackGround

        stage.addActor(barBackGround)
        stage.addActor(bar)
        stage.addActor(barForeground)
    }

    fun setPosition(x: Float, y: Float){
        bar.setPosition(x + (width - bar.width) / 2, y)
        barBackGround.setPosition(x, y)
        barForeground.setPosition(x, y)

        posX = x
        posY = y
    }

    fun setSize(width: Float, height: Float){
        bar.setSize(width - (barForeground.width - bar.width) / 2, height)
        barBackGround.setSize(width, height)
        barForeground.setSize(width, height)

        this.width = barForeground.width
        this.height = barForeground.height
    }

    fun setValue(percent: Float){
        bar.width = (width - 30) * percent
    }
}