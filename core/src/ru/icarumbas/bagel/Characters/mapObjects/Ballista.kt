package ru.icarumbas.bagel.Characters.mapObjects

import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.physics.box2d.Body
import ru.icarumbas.PIX_PER_M
import ru.icarumbas.bagel.Characters.Enemies.Enemy
import ru.icarumbas.bagel.Characters.Enemies.ShootAbility


class Ballista : MapObject, ShootAbility {

    override val width = 128f.div(PIX_PER_M)
    override val height = 192f.div(PIX_PER_M)

    constructor() : super()

    constructor(rectangle: Rectangle) : super(rectangle)

    override fun shoot(body: Body) {
        super.shoot(body)
    }


}