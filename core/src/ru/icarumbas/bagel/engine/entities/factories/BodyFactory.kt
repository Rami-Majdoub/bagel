package ru.icarumbas.bagel.engine.entities.factories

import com.badlogic.gdx.maps.objects.PolylineMapObject
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.Body
import com.badlogic.gdx.physics.box2d.BodyDef
import com.badlogic.gdx.physics.box2d.World
import ktx.box2d.body
import ru.icarumbas.bagel.engine.world.PIX_PER_M
import ru.icarumbas.bagel.utils.*
import kotlin.experimental.or


class BodyFactory(
        private val world: World
) {

    fun playerBody(): Body {

        return world.body {
            position.set(4f, 5f)
            type = BodyDef.BodyType.DynamicBody

            box(width = 25 / PIX_PER_M, height = 40 / PIX_PER_M) {
                restitution = .1f
                friction = .4f
                density = 1.5f
                filter.categoryBits = PLAYER_BIT
                filter.maskBits = GROUND_BIT or PLATFORM_BIT or WEAPON_BIT or SHARP_BIT or KEY_OPEN_BIT or AI_BIT
            }

            circle(radius = .25f, position = Vector2(0f, -.25f)) {
                friction = 1.5f
                filter.categoryBits = PLAYER_FEET_BIT
            }

            fixedRotation = true
        }
    }

    fun swordWeapon(categoryBit: Short, maskBit: Short, size: Vector2): Body {

        return world.body {
            type = BodyDef.BodyType.DynamicBody
            position.set(4f, 5f)

            box(width = size.x / 2, height = size.y / 2) {
                density = .001f
                filter.categoryBits = categoryBit
                filter.maskBits = maskBit
            }

            active = false
        }
    }

    fun polylineMapObjectBody(obj: PolylineMapObject, bType: BodyDef.BodyType, cBit: Short, mBit: Short = -1): Body {

        return world.body {
            type = bType

            chain(vertices = obj.polyline.transformedVertices.clone().map { it.div(PIX_PER_M) }.toFloatArray()) {
                restitution = .1f
                friction = 1f
                filter.categoryBits = cBit
                filter.maskBits = mBit
            }

            active = false
        }
    }

    fun rectangleMapObjectBody(rect: Rectangle,
                               bType: BodyDef.BodyType,
                               width: Float = 0f,
                               height: Float = 0f,
                               cBit: Short,
                               mBit: Short = -1,
                               fixed: Boolean = true,
                               gravity: Float = 1f): Body {

        return world.body {
            type = bType
            position.x = rect.x / PIX_PER_M + width / 2
            position.y = rect.y / PIX_PER_M + height / 2

            box(width = width / 2f, height = height / 2f) {
                restitution = .1f
                friction = 1f
                density = .3f
                filter.categoryBits = cBit
                filter.maskBits = mBit
            }

            gravityScale = gravity
            active = false
            fixedRotation = fixed
        }
    }
}
