package ru.icarumbas.bagel.view.renderer

import com.badlogic.ashley.core.Entity
import ru.icarumbas.bagel.utils.player

class RenderingComparator : Comparator<Entity> {


    override fun compare(e1: Entity, e2: Entity): Int {
        return when {
            player.has(e1) -> 1
            player.has(e2) -> -1
            else -> 0
        }
    }
}