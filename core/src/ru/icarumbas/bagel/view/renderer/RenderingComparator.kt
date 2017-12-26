package ru.icarumbas.bagel.view.renderer

import com.badlogic.ashley.core.Entity
import ru.icarumbas.bagel.utils.AI
import ru.icarumbas.bagel.utils.player

class RenderingComparator : Comparator<Entity> {

    override fun compare(e1: Entity, e2: Entity): Int {
        return when {
            player.has(e1) || (AI.has(e1) && !player.has(e2)) -> 1
            player.has(e2) || (AI.has(e2) && !player.has(e1)) -> -1
            else -> 0
        }
    }
}