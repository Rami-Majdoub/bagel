package ru.icarumbas.bagel.creators

import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.maps.objects.PolylineMapObject
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.*
import ru.icarumbas.*
import kotlin.experimental.or


class B2DWorldCreator(private val world: World) {

    fun createPlayerBody(): Body {

        val bodyDef = BodyDef()
        bodyDef.position.set(4f, 5f)

        bodyDef.type = BodyDef.BodyType.DynamicBody
        val playerBody = world.createBody(bodyDef)

        val shape = PolygonShape()
        shape.setAsBox(.25f, .4f)

        val fixtureDef = FixtureDef()
        fixtureDef.shape = shape
        fixtureDef.restitution = .1f
        fixtureDef.friction = .4f
        fixtureDef.density = .04f
        fixtureDef.filter.categoryBits = PLAYER_BIT
        fixtureDef.filter.maskBits = GROUND_BIT or PLATFORM_BIT or PLAYER_WEAPON_BIT

        playerBody.createFixture(fixtureDef)
        shape.dispose()

        val circleShape = CircleShape()
        circleShape.radius = .25f
        circleShape.position = Vector2(0f, -.25f)
        fixtureDef.friction = 1.5f
        fixtureDef.shape = circleShape

        playerBody.createFixture(fixtureDef)
        circleShape.dispose()

        playerBody.isFixedRotation = true
        return playerBody

    }

    fun createSwordWeapon(categoryBit: Short, maskBit: Short, texture: TextureRegion, size: Vector2): Body{

        val bodyDef = BodyDef()
        bodyDef.type = BodyDef.BodyType.DynamicBody
        bodyDef.position.set(4f, 5f)
        val weaponBody = world.createBody(bodyDef)

        val fixtureDef = FixtureDef()
        fixtureDef.filter.categoryBits = categoryBit
        fixtureDef.filter.maskBits = maskBit

        val shape = PolygonShape()
        shape.setAsBox(size.x / 2, size.y / 2)
        fixtureDef.shape = shape
        fixtureDef.density = .001f
        weaponBody.createFixture(fixtureDef)
        weaponBody.isActive = false


        weaponBody.userData = texture

        shape.dispose()
        return weaponBody
    }

    fun definePolylineMapObjectBody(obj: PolylineMapObject,
                                type: BodyDef.BodyType,
                                cBit: Short,
                                mBit: Short = -1): Body{
        val def = BodyDef()
        def.type = type

        val fixtureDef = FixtureDef()
        fixtureDef.restitution = .1f
        fixtureDef.friction = 1f
        fixtureDef.filter.categoryBits = cBit
        fixtureDef.filter.maskBits = mBit

        val vertices = obj.polyline.transformedVertices.clone()

        for (x in vertices.indices) vertices[x] /= PIX_PER_M

        val chainShape = ChainShape()
        chainShape.createChain(vertices)
        fixtureDef.shape = chainShape

        val body = world.createBody(def)
        body.createFixture(fixtureDef)
        body.isActive = false

        chainShape.dispose()
        return body
    }

    fun defineRectangleMapObjectBody(rect: Rectangle,
                            type: BodyDef.BodyType,
                            cBit: Short,
                            width: Float = 0f,
                            height: Float = 0f,
                            mBit: Short = -1,
                            tex: TextureRegion? = null,
                            gravity: Float = 0f): Body{


        val def = BodyDef()
        def.type = type

        val fixtureDef = FixtureDef()
        fixtureDef.restitution = .1f
        fixtureDef.friction = 1f
        fixtureDef.filter.categoryBits = cBit
        fixtureDef.filter.maskBits = mBit

        def.position.x = rect.x / PIX_PER_M + width / 2
        def.position.y = rect.y / PIX_PER_M + height / 2
        def.type = BodyDef.BodyType.StaticBody

        val polygonShape = PolygonShape()
        polygonShape.setAsBox(width / 2f, height / 2f)
        fixtureDef.shape = polygonShape

        val body = world.createBody(def)
        body.createFixture(fixtureDef)
        body.gravityScale = gravity
        body.userData = tex
        body.isActive = false

        polygonShape.dispose()
        return body
    }

}


