package ru.icarumbas.bagel.utils

import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.Body
import com.badlogic.gdx.physics.box2d.joints.DistanceJointDef
import com.badlogic.gdx.physics.box2d.joints.RevoluteJointDef


fun Body.createRevoluteJoint(bodyA: Body, anchorA: Vector2, anchorB: Vector2, maxSpeed: Float, speed: Float): Body {
    val joint = RevoluteJointDef()
    joint.bodyA = bodyA
    joint.bodyB = this
    joint.localAnchorA.set(anchorA.x, anchorA.y)
    joint.localAnchorB.set(anchorB.x, anchorB.y)
    joint.enableMotor = true
    joint.motorSpeed = speed
    joint.maxMotorTorque = maxSpeed
    world.createJoint(joint)
    return this
}

fun Body.createDistanceJoint(bodyA: Body): Body {
    val joint = DistanceJointDef()
    joint.bodyA = bodyA
    joint.bodyB = this
    world.createJoint(joint)
    return this
}

/**
 * Normalizes an angle to a relative angle.
 * The normalized angle will be in the range from -PI to PI, where PI
 * itself is not included.
 *
 * @param angle the angle to normalize
 * @return the normalized angle that will be in the range of [-PI,PI]
 */
fun Body.angleInDegrees(): Float{
    val ang = angle % MathUtils.PI2
    return if ((ang) >= 0) if (ang < MathUtils.PI) ang else ang - MathUtils.PI2 else if (ang >= -MathUtils.PI) ang else ang + MathUtils.PI2
}

