package ru.icarumbas.bagel.utils

import com.badlogic.ashley.core.Entity

class RenderingComparator : Comparator<Entity> {

    private val pl = Mappers.player

    override fun compare(e1: Entity, e2: Entity): Int {
        return when {
            pl.has(e1) -> 1
            pl.has(e2) -> -1
            else -> 0
        }
    }
}