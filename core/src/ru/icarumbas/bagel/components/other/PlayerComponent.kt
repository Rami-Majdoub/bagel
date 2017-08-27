package ru.icarumbas.bagel.components.other

import com.badlogic.ashley.core.Component
import ru.icarumbas.bagel.Equip


data class PlayerComponent(var money: Int,
                           var collidingWithGround: Boolean = false,
                           var inventory: ArrayList<Equip> = ArrayList()) : Component