package ru.icarumbas.bagel.engine.systems.velocity

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.math.Vector2
import ru.icarumbas.bagel.engine.components.velocity.FlyComponent
import ru.icarumbas.bagel.engine.world.RoomWorld
import ru.icarumbas.bagel.utils.inView

class FlyingSystem : IteratingSystem {

    private val playerEntity: Entity
    private val rm: RoomWorld
    private val velocity = Vector2()

    constructor(playerEntity: Entity, rm: RoomWorld) : super(Family.all(FlyComponent::class.java).get()) {
        this.playerEntity = playerEntity
        this.rm = rm
    }

    override fun processEntity(entity: Entity, deltaTime: Float) {
        if (entity.inView(rm)) {
            val distanceHor = Math.abs(body[playerEntity].body.position.x - body[entity].body.position.x)
            val distanceVert = Math.abs(body[playerEntity].body.position.y - body[entity].body.position.y)

            var k = distanceHor.div(distanceVert)
            var k2 = 1f

            if (distanceVert > distanceHor) {
                k2 = distanceVert.div(distanceHor)
                k = 1f
            }

            when {
                AI[entity].isTargetRight && AI[entity].isTargetHigher -> {
                    velocity.set(fly[entity].speed.div(k2), fly[entity].speed.div(k))
                    if (fly[entity].lastRight) body[entity].body.setLinearVelocity(-.3f, 0f)
                    if (fly[entity].lastUp) body[entity].body.setLinearVelocity(0f, -.3f)
                    fly[entity].lastRight = false
                    fly[entity].lastUp = false
                }
                !AI[entity].isTargetRight && !AI[entity].isTargetHigher -> {
                    velocity.set(-fly[entity].speed.div(k2), -fly[entity].speed.div(k))
                    if (!fly[entity].lastRight) body[entity].body.setLinearVelocity(.3f, 0f)
                    if (!fly[entity].lastUp) body[entity].body.setLinearVelocity(0f, .3f)
                    fly[entity].lastRight = true
                    fly[entity].lastUp = true
                }
                AI[entity].isTargetRight && !AI[entity].isTargetHigher -> {
                    velocity.set(fly[entity].speed.div(k2), -fly[entity].speed.div(k))
                    if (fly[entity].lastRight) body[entity].body.setLinearVelocity(-.3f, 0f)
                    if (!fly[entity].lastUp) body[entity].body.setLinearVelocity(0f, .3f)
                    fly[entity].lastRight = false
                    fly[entity].lastUp = true
                }
                !AI[entity].isTargetRight && AI[entity].isTargetHigher -> {
                    velocity.set(-fly[entity].speed.div(k2), fly[entity].speed.div(k))
                    if (!fly[entity].lastRight) body[entity].body.setLinearVelocity(.3f, 0f)
                    if (fly[entity].lastUp) body[entity].body.setLinearVelocity(0f, -.3f)
                    fly[entity].lastRight = true
                    fly[entity].lastUp = false
                }
            }

            body[entity].body.applyLinearImpulse(velocity, body[entity].body.localPoint2, true)
        }
    }
}