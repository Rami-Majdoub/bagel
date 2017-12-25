package ru.icarumbas.bagel.view.ui.actors

import com.badlogic.gdx.scenes.scene2d.Actor


open class RegularBar(

        private val barBackGround: Actor,
        private val bar: Actor,
        private val barForeground: Actor

) : Actor(){

    override fun setPosition(x: Float, y: Float){
        bar.setPosition(x + (width - bar.width) / 2, y)
        barBackGround.setPosition(x, y)
        barForeground.setPosition(x, y)

        this.x = x
        this.y = y
    }

    override fun setSize(width: Float, height: Float){
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