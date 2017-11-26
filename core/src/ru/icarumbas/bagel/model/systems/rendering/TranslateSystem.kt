package ru.icarumbas.bagel.model.systems.rendering

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.math.MathUtils
import ru.icarumbas.bagel.RoomManager
import ru.icarumbas.bagel.model.components.physics.BodyComponent
import ru.icarumbas.bagel.model.components.rendering.TranslateComponent
import ru.icarumbas.bagel.utils.Mappers.Mappers.body
import ru.icarumbas.bagel.utils.Mappers.Mappers.size
import ru.icarumbas.bagel.utils.Mappers.Mappers.translate
import ru.icarumbas.bagel.utils.inView
import ru.icarumbas.bagel.utils.rotatedRight


class TranslateSystem : IteratingSystem{

    private val rm: RoomManager

    constructor(rm: RoomManager) : super(Family.all(BodyComponent::class.java, TranslateComponent::class.java).get()) {
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