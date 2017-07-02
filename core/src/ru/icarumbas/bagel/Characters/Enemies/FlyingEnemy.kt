package ru.icarumbas.bagel.Characters.Enemies

import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import ru.icarumbas.PIX_PER_M
import ru.icarumbas.bagel.Characters.Player
import ru.icarumbas.bagel.Screens.GameScreen


open class FlyingEnemy : Enemy{

    override val strength = 15
    override val width = 92f.div(PIX_PER_M)
    override val height = 92f.div(PIX_PER_M)
    override val gravityScale = 0f
    override val speed = .25f

    var lastRight = true
    var lastUp = true

    constructor() : super()

    constructor(rectangle: Rectangle) : super(rectangle)

    override fun getState(player: Player): GameScreen.State {
        return GameScreen.State.Running
    }

    override fun move(player: Player, delta: Float) {
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
            if (lastRight) body!!.setLinearVelocity(-.3f, 0f)
            if (lastUp) body!!.setLinearVelocity(0f, -.3f)
            lastRight = false
            lastUp = false
        }
        else
            if (!isPlayerRight(player) && !isPlayerHigher(player)) {
                velocity = Vector2(-speed.div(k2), -speed.div(k))
                if (!lastRight) body!!.setLinearVelocity(.3f, 0f)
                if (!lastUp) body!!.setLinearVelocity(0f, .3f)
                lastRight = true
                lastUp = true
            }

            else
                if (isPlayerRight(player) && !isPlayerHigher(player)) {
                    velocity = Vector2(speed.div(k2), -speed.div(k))
                    if (lastRight) body!!.setLinearVelocity(-.3f, 0f)
                    if (!lastUp) body!!.setLinearVelocity(0f, .3f)
                    lastRight = false
                    lastUp = true
                }
                else
                    if (!isPlayerRight(player) && isPlayerHigher(player)) {
                        velocity = Vector2(-speed.div(k2), speed.div(k))
                        if (!lastRight) body!!.setLinearVelocity(.3f, 0f)
                        if (lastUp) body!!.setLinearVelocity(0f, -.3f)
                        lastRight = true
                        lastUp = false
                    }

        if (player.playerBody.linearVelocity.x < .01 && player.playerBody.linearVelocity.y < .01) {
            body!!.applyLinearImpulse(velocity, body!!.localPoint2, true)
        }
    }
}