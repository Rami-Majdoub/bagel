package ru.icarumbas.bagel.Characters.Enemies

import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import ru.icarumbas.PIX_PER_M
import ru.icarumbas.bagel.Characters.Player


open class FlyingEnemy : Enemy{

    override val strength = 15
    override val width = 92f.div(PIX_PER_M)
    override val height = 92f.div(PIX_PER_M)
    override val gravityScale = 0f
    override val speed = .25f

    var lastRight = true

    constructor() : super()

    constructor(rectangle: Rectangle) : super(rectangle)

    override fun move(player: Player) {
        val distanceHor = Math.abs(player.playerBody.position.x - body!!.position.x)
        val distanceVert = Math.abs(player.playerBody.position.y - body!!.position.y)

        var k = distanceHor.div(distanceVert)
        var k2 = 1f

        if (distanceVert > distanceHor) {
            k2 = distanceVert.div(distanceHor)
            k = 1f
        }

        if (isPlayerRight(player) && isPlayerHigher(player)) {
            velocity = Vector2(speed.div(k2), speed.div(k))
            if (lastRight) body!!.setLinearVelocity(-.25f, 0f)
            lastRight = false
        }
        else
            if (!isPlayerRight(player) && !isPlayerHigher(player)) {
                velocity = Vector2(-speed.div(k2), -speed.div(k))
                if (!lastRight) body!!.setLinearVelocity(.25f, 0f)
                lastRight = true
            }

            else
                if (isPlayerRight(player) && !isPlayerHigher(player)) {
                    velocity = Vector2(speed.div(k2), -speed.div(k))
                    if (lastRight) body!!.setLinearVelocity(-.25f, 0f)
                    lastRight = false
                }
                else
                    if (!isPlayerRight(player) && isPlayerHigher(player)) {
                        if (!lastRight) body!!.setLinearVelocity(.25f, 0f)
                        velocity = Vector2(-speed.div(k2), speed.div(k))
                        lastRight = true
                    }

        if (player.playerBody.linearVelocity.x < .01 && player.playerBody.linearVelocity.y < .01) {
            body!!.applyLinearImpulse(velocity, body!!.localPoint2, true)
        }
    }
}