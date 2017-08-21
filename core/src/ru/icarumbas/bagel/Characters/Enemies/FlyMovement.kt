package ru.icarumbas.bagel.Characters.Enemies


interface FlyMovement {

   /* val velocity: Vector2
    val speed: Float
    var lastRight: Boolean
    var lastUp: Boolean

    fun fly(player: Player, body: Body, playerRight: Boolean, playerHigher: Boolean) {
        val distanceHor = Math.abs(player.playerBody.position.x - body.position.x)
        val distanceVert = Math.abs(player.playerBody.position.y - body.position.y)

        var k = distanceHor.div(distanceVert)
        var k2 = 1f

        if (distanceVert > distanceHor) {
            k2 = distanceVert.div(distanceHor)
            k = 1f
        }

        if (playerRight && playerHigher) {
            velocity.set(speed.div(k2), speed.div(k))
            if (lastRight) body.setLinearVelocity(-.3f, 0f)
            if (lastUp) body.setLinearVelocity(0f, -.3f)
            lastRight = false
            lastUp = false
        }
        else
            if (!playerRight && !playerHigher) {
                velocity.set(-speed.div(k2), -speed.div(k))
                if (!lastRight) body.setLinearVelocity(.3f, 0f)
                if (!lastUp) body.setLinearVelocity(0f, .3f)
                lastRight = true
                lastUp = true
            }

            else
                if (playerRight && !playerHigher) {
                    velocity.set(speed.div(k2), -speed.div(k))
                    if (lastRight) body.setLinearVelocity(-.3f, 0f)
                    if (!lastUp) body.setLinearVelocity(0f, .3f)
                    lastRight = false
                    lastUp = true
                }
                else
                    if (!playerRight && playerHigher) {
                        velocity.set(-speed.div(k2), speed.div(k))
                        if (!lastRight) body.setLinearVelocity(.3f, 0f)
                        if (lastUp) body.setLinearVelocity(0f, -.3f)
                        lastRight = true
                        lastUp = false
                    }

        body.applyLinearImpulse(velocity, body.localPoint2, true)

    }*/

}