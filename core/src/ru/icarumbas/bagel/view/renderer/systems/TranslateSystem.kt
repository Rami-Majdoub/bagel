package ru.icarumbas.bagel.view.renderer.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.math.MathUtils
import ru.icarumbas.bagel.engine.components.physics.BodyComponent
import ru.icarumbas.bagel.engine.world.RoomWorld
import ru.icarumbas.bagel.utils.inView
import ru.icarumbas.bagel.utils.rotatedRight
import ru.icarumbas.bagel.view.renderer.components.TranslateComponent


class TranslateSystem : IteratingSystem{

    private val rm: RoomWorld

    constructor(rm: RoomWorld) : super(Family.all(BodyComponent::class.java, TranslateComponent::class.java).get()) {
        this.rm = rm
    }

    override fun processEntity(e: Entity, deltaTime: Float) {
        if (e.inView(rm)){
            translate[e].x = body[e].body.position.x - if (e.rotatedRight()) size[e].rectSize.x / 2 else size[e].spriteSize.x - size[e].rectSize.x / 2
            translate[e].y = body[e].body.position.y - size[e].rectSize.y / 2
            translate[e].angle = body[e].body.angle * MathUtils.radiansToDegrees
        }
    }
}