package ru.icarumbas.bagel

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.utils.ImmutableArray
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.maps.objects.PolylineMapObject
import com.badlogic.gdx.maps.objects.RectangleMapObject
import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.*
import com.badlogic.gdx.utils.Array
import ru.icarumbas.*
import ru.icarumbas.bagel.components.other.*
import ru.icarumbas.bagel.components.physics.BodyComponent
import ru.icarumbas.bagel.components.physics.StaticComponent
import ru.icarumbas.bagel.components.rendering.SizeComponent
import ru.icarumbas.bagel.systems.other.StateSwapSystem
import kotlin.experimental.or


class B2DWorldCreator(private val assets: AssetManager, private val world: World) {

    fun createPlayerBody(): Body {

        val bodyDef = BodyDef()
        bodyDef.position.set(4f, 5f)

        bodyDef.type = BodyDef.BodyType.DynamicBody
        val playerBody = world.createBody(bodyDef)

        val shape = PolygonShape()
        shape.setAsBox(.3f, .6f)

        val fixtureDef = FixtureDef()
        fixtureDef.shape = shape
        fixtureDef.restitution = .1f
        fixtureDef.friction = .4f
        fixtureDef.density = .04f
        fixtureDef.filter.categoryBits = PLAYER_BIT
        fixtureDef.filter.maskBits = GROUND_BIT or PLATFORM_BIT or OTHER_ENTITY_BIT or PLAYER_WEAPON_BIT

        playerBody.createFixture(fixtureDef)
        shape.dispose()

        val circleShape = CircleShape()
        fixtureDef.shape = circleShape
        circleShape.radius = .29f
        circleShape.position = Vector2(0f, -.4f)
        fixtureDef.friction = 1.5f

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
        shape.setAsBox(size.x / 2, size.y / 2, Vector2(size.x / 2, size.y / 2), 0f)
        fixtureDef.shape = shape
        fixtureDef.density = .001f
        weaponBody.createFixture(fixtureDef)
        weaponBody.isActive = false


        weaponBody.userData = texture

        shape.dispose()
        return weaponBody
    }

    fun loadGround(path: String, layer: String, bit: Short, engine: Engine) {

        if (assets.get(path, TiledMap::class.java).layers.get(layer) != null) {
            val objects = assets[path, TiledMap::class.java].layers[layer].objects

            val fixtureDef = FixtureDef()
            val def = BodyDef()
            val chainShape = ChainShape()
            val polygonShape = PolygonShape()


            objects.forEach {

                if (it is PolylineMapObject) {

                    val vertices = it.polyline.transformedVertices.clone()

                    for (x in vertices.indices) vertices[x] /= PIX_PER_M

                    chainShape.createChain(vertices)
                    fixtureDef.shape = chainShape
                    fixtureDef.friction = 1f
                    fixtureDef.filter.categoryBits = bit

                    val body = world.createBody(def)
                    body.createFixture(fixtureDef)
                    body.isActive = false

                    engine.addEntity(Entity()
                            .add(BodyComponent(body))
                            .add(StaticComponent(path)))

                }

                if (it is RectangleMapObject) {

                    val rect = it.rectangle

                    def.position.x = (rect.x + rect.width / 2f) / PIX_PER_M
                    def.position.y = (rect.y + rect.height / 2f) / PIX_PER_M
                    def.type = BodyDef.BodyType.StaticBody

                    polygonShape.setAsBox(rect.width / 2f / PIX_PER_M, rect.height / 2f / PIX_PER_M)
                    fixtureDef.shape = polygonShape
                    fixtureDef.friction = 1f
                    fixtureDef.filter.categoryBits = bit

                    val body = world.createBody(def)
                    body.createFixture(fixtureDef)
                    body.isActive = false

                    engine.addEntity(Entity()
                            .add(BodyComponent(body))
                            .add(StaticComponent(path)))

                }

            }
        }

    }

    fun loadMapObject(roomPath: String,
                      objectPath: String,
                      engine: Engine,
                      roomId: Int) {
        val layer = assets.get(roomPath, TiledMap::class.java).layers[objectPath]

        if (layer != null)
            layer.objects
                    .filterIsInstance<RectangleMapObject>()
                    .forEach {
                        engine.addEntity(when(objectPath){
                            "boxes" -> createBoxEntity(it.rectangle, roomId)
                            "chandeliers" -> createChandelierEntity(it.rectangle, roomId)
                            "chests" -> Entity()
                            "statues" -> Entity()
                            "spikeTraps" -> Entity()
                            "spikes" -> Entity()
                            "portalDoor" -> Entity()
                            "chairs" -> Entity()
                            "tables" -> Entity()
                            else -> throw Exception("NO SUCH CLASS")
                        })
                    }
    }

    private fun createBoxEntity(rectangle: Rectangle, roomId: Int): Entity {
        return Entity()
                .add(BodyComponent(defineBody(
                        world,
                        rectangle,
                        BodyDef.BodyType.DynamicBody,
                        OTHER_ENTITY_BIT,
                        when (MathUtils.random(1)){
                            0 -> assets["Packs/RoomObjects.txt", TextureAtlas::class.java].createSprite("box")
                            else -> assets["Packs/RoomObjects.txt", TextureAtlas::class.java].createSprite("barrel")
                        }
                        )))
                .add(SizeComponent(
                        64 / PIX_PER_M,
                        64 / PIX_PER_M))
                .add(DamageComponent())
                .add(ParametersComponent(5))
                .add(CoinDropComponent(2))
                .add(StateComponent(ImmutableArray<String>(Array.with(
                        StateSwapSystem.AllStates.STANDING,
                        StateSwapSystem.AllStates.DEAD))))
                .add(RoomIdComponent(roomId))


    }

    private fun createChandelierEntity(rectangle: Rectangle, roomId: Int): Entity {

        val random = MathUtils.random(1)

        return Entity()
                .add(BodyComponent(defineBody(
                        world,
                        rectangle,
                        BodyDef.BodyType.StaticBody,
                        OTHER_ENTITY_BIT,
                        when (random){
                            0 -> assets["Packs/RoomObjects.txt", TextureAtlas::class.java].createSprite("goldenChandelier")
                            else -> assets["Packs/RoomObjects.txt", TextureAtlas::class.java].createSprite("silverChandelier")
                        })))
                .add(SizeComponent(
                        rectangle.width / PIX_PER_M,
                        rectangle.height / PIX_PER_M))
                .add(DamageComponent())
                .add(ParametersComponent(5))
                .add(CoinDropComponent(when (random){
                    0 -> MathUtils.random(3)
                    else -> MathUtils.random(1)
                }))
                .add(StateComponent(ImmutableArray<String>(Array.with(
                        StateSwapSystem.AllStates.STANDING,
                        StateSwapSystem.AllStates.DEAD))))
                .add(RoomIdComponent(roomId))

    }

    private fun createChestEntity(world: World, rectangle: Rectangle, roomId: Int, assetManager: AssetManager): Entity {

        val random = MathUtils.random(2)

        return Entity()
                /*.add(BodyComponent(defineBody(
                        world,
                        rectangle,
                        BodyDef.BodyType.DynamicBody,
                        OTHER_ENTITY_BIT)))
                .add(SizeComponent(
                        rectangle.width / PIX_PER_M,
                        rectangle.height / PIX_PER_M))
                .add(CoinDropComponent(when (random){
                    0 -> MathUtils.random(10)
                    1-> MathUtils.random(20)
                    else -> MathUtils.random(30)
                }))
                .add(StateComponent(ImmutableArray<String>(Array.with(
                        StateSwapSystem.AllStates.STANDING,
                        StateSwapSystem.AllStates.DEAD))))
                .add(RoomIdComponent(roomId))*/

    }

    private fun defineBody(world: World,
                           rectangle: Rectangle,
                           bodyType: BodyDef.BodyType,
                           categoryBit: Short,
                           maskBit: Short,
                           gravityScale: Float,
                           texture: TextureRegion) : Body {

        val fixtureDef = FixtureDef()
        val def = BodyDef()
        val shape = PolygonShape()

        def.type = bodyType
        def.position.x = rectangle.x / PIX_PER_M + rectangle.width / PIX_PER_M / 2f
        def.position.y = rectangle.y / PIX_PER_M + rectangle.height / PIX_PER_M / 2f

        shape.setAsBox(rectangle.width / PIX_PER_M / 2f, rectangle.height / PIX_PER_M / 2f)
        fixtureDef.shape = shape
        fixtureDef.friction = .4f
        fixtureDef.restitution = .1f
        fixtureDef.density = 10f
        fixtureDef.filter.categoryBits = categoryBit
        fixtureDef.filter.maskBits = maskBit

        val body = world.createBody(def)
        body.createFixture(fixtureDef)
        body.isActive = false
        body.isFixedRotation = true
        body.gravityScale = gravityScale
        body.userData = texture

        shape.dispose()
        return body
    }

    private fun defineBody(world: World,
                           rectangle: Rectangle,
                           bodyType: BodyDef.BodyType,
                           categoryBit: Short,
                           texture: TextureRegion)
            = defineBody(world, rectangle, bodyType, categoryBit, -1, 1f, texture)

}


