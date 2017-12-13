package ru.icarumbas.bagel.view.ui.actors

import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.scenes.scene2d.Actor
import ru.icarumbas.bagel.utils.damage

class HpBar(

        bar: Actor,
        barForeground: Actor,
        barBackGround: Actor,
        private val player: Entity

) : RegularBar(bar, barForeground, barBackGround){

    override fun act(dt: Float) {
        setValue(damage[player].HP / 100f)
    }
}