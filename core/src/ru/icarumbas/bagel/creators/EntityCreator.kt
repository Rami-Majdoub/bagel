package ru.icarumbas.bagel.creators

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.utils.ImmutableArray
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.maps.MapObject
import com.badlogic.gdx.maps.objects.PolylineMapObject
import com.badlogic.gdx.maps.objects.RectangleMapObject
import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.Body
import com.badlogic.gdx.physics.box2d.BodyDef
import com.badlogic.gdx.utils.Array
import ru.icarumbas.*
import ru.icarumbas.bagel.components.other.*
import ru.icarumbas.bagel.components.physics.BodyComponent
import ru.icarumbas.bagel.components.physics.StaticComponent
import ru.icarumbas.bagel.components.rendering.AnimationComponent
import ru.icarumbas.bagel.components.rendering.SizeComponent
import ru.icarumbas.bagel.components.velocity.JumpComponent
import ru.icarumbas.bagel.components.velocity.RunComponent
import ru.icarumbas.bagel.systems.other.StateSystem
import ru.icarumbas.bagel.systems.physics.WeaponSystem
import ru.icarumbas.bagel.utils.createRevoluteJoint
import kotlin.experimental.or


class EntityCreator(private val b2DWorldCreator: B2DWorldCreator,
                    private val assets: AssetManager,
                    private val engine: Engine,
                    private val animCreator: AnimationCreator){

    private fun createSwingWeaponEntity(width: Int,
                                height: Int,
                                mainBody: Body,
                                b2DWorldCreator: B2DWorldCreator,
                                anchorA: Vector2,
                                anchorB: Vector2,
                                path: String? = null,
                                atlas: TextureAtlas? = null): Entity {
        val weaponEntity = Entity()
                .add(BodyComponent(b2DWorldCreator.createSwordWeapon(
                        WEAPON_BIT,
                        AI_BIT or BREAKABLE_BIT or PLAYER_BIT,
                        atlas?.findRegion(path),
                        Vector2(width / PIX_PER_M, height / PIX_PER_M)).createRevoluteJoint(mainBody, anchorA, anchorB)))

        engine.addEntity(weaponEntity)
        return weaponEntity
    }

    fun createPlayerEntity(animCreator: AnimationCreator,
                           atlas: TextureAtlas,
                           playerBody: Body): Entity {

        return Entity()
                .add(PlayerComponent(0))
                .add(RunComponent(.01f, 6f))
                .add(SizeComponent(.425f))
                .add(JumpComponent(.07f, 5))
                .add(DamageComponent(100))
                .add(BodyComponent(playerBody))
                .add(EquipmentComponent())
                .add(WeaponComponent(
                        type = WeaponSystem.SWING,
                        strength = 5,
                        attackSpeed = .00025f,
                        nearAttackStrength = 0,
                        knockback = Vector2(1.5f, 1f),
                        entityLeft = createSwingWeaponEntity(
                                width = 30,
                                height = 100,
                                mainBody = playerBody,
                                b2DWorldCreator = b2DWorldCreator,
                                anchorA = Vector2(-.1f, -.3f),
                                anchorB = Vector2(0f, -.5f),
                                path = "Sword2",
                                atlas = assets["Packs/GuyKnight.pack", TextureAtlas::class.java])
                                .add(AlwaysRenderingMarkerComponent())
                                .add(SizeComponent(.5f)),
                        entityRight = createSwingWeaponEntity(
                                width = 30,
                                height = 100,
                                mainBody = playerBody,
                                b2DWorldCreator = b2DWorldCreator,
                                anchorA = Vector2(.1f, -.3f),
                                anchorB = Vector2(0f, -.5f),
                                path = "Sword2",
                                atlas = assets["Packs/GuyKnight.pack", TextureAtlas::class.java])
                                .add(AlwaysRenderingMarkerComponent())
                                .add(SizeComponent(.5f))))
                .add(StateComponent(ImmutableArray<String>(Array.with(
                        StateSystem.RUNNING,
                        StateSystem.JUMPING,
                        StateSystem.STANDING,
                        StateSystem.ATTACKING,
                        StateSystem.DEAD,
                        StateSystem.WALKING,
                        StateSystem.JUMP_ATTACKING
                ))))
                .add(AnimationComponent(hashMapOf(
                        StateSystem.RUNNING to animCreator.create("Run", 10, .075f, atlas),
                        StateSystem.JUMPING to animCreator.create("Jump", 10, .15f, atlas),
                        StateSystem.STANDING to animCreator.create("Idle", 10, .1f, atlas),
                        StateSystem.DEAD to animCreator.create("Dead", 10, .1f, atlas, Animation.PlayMode.NORMAL),
                        StateSystem.ATTACKING to animCreator.create("Attack", 7, .075f, atlas),
                        StateSystem.WALKING to animCreator.create("Walk", 10, .075f, atlas),
                        StateSystem.JUMP_ATTACKING to animCreator.create("JumpAttack", 10, .075f, atlas)
                )))
                .add(AlwaysRenderingMarkerComponent())

    }


    private fun createGroundEntity(obj: MapObject, path: String, bit: Short): Entity{
        return Entity()
                .add(StaticComponent(path))
                .add(when (obj) {
                    is RectangleMapObject -> {
                        BodyComponent(b2DWorldCreator.defineRectangleMapObjectBody(
                                obj.rectangle,
                                BodyDef.BodyType.StaticBody,
                                obj.rectangle.width / PIX_PER_M,
                                obj.rectangle.height / PIX_PER_M,
                                bit))
                    }
                    is PolylineMapObject -> {
                        BodyComponent(b2DWorldCreator.definePolylineMapObjectBody(
                                obj,
                                BodyDef.BodyType.StaticBody,
                                bit))
                    }
                    else -> throw Exception()
                })

    }

    fun loadIdEntity(roomId: Int,
                             rect: Rectangle,
                             objectPath: String,
                             atlas: TextureAtlas,
                             r: Int): Boolean {

        engine.addEntity(when(objectPath){
            "vase" -> {
                val size = when (r) {
                    1 -> Pair(132, 171)
                    2 -> Pair(65, 106)
                    3 -> Pair(120, 162)
                    else -> Pair(98, 72)
                }

                Entity()
                        .add(BodyComponent(b2DWorldCreator.defineRectangleMapObjectBody(
                                rect,
                                BodyDef.BodyType.StaticBody,
                                size.first / PIX_PER_M,
                                size.second / PIX_PER_M,
                                BREAKABLE_BIT,
                                WEAPON_BIT,
                                atlas.findRegion("Vase ($r)"))))
                        .add(DamageComponent(5))
                        .add(StateComponent(ImmutableArray(Array.with(StateSystem.DEAD))))
                        .add(RoomIdComponent(roomId))
                        .add(SizeComponent())
            }

            "window" -> {
                Entity()
                        .add(BodyComponent(b2DWorldCreator.defineRectangleMapObjectBody(
                        rect,
                        BodyDef.BodyType.StaticBody,
                        86 / PIX_PER_M,
                        169 / PIX_PER_M,
                        BREAKABLE_BIT,
                        WEAPON_BIT,
                        atlas.findRegion("Window Small ($r)"))))
                        .add(RoomIdComponent(roomId))
                        .add(SizeComponent())


            }

            "chair1" -> {
                if (r == 2) return false
                Entity()
                        .add(BodyComponent(b2DWorldCreator.defineRectangleMapObjectBody(
                        rect,
                        BodyDef.BodyType.StaticBody,
                        70 / PIX_PER_M,
                        128 / PIX_PER_M,
                        BREAKABLE_BIT,
                        WEAPON_BIT,
                        atlas.findRegion("Chair (1)"))))
                        .add(DamageComponent(5))
                        .add(StateComponent(ImmutableArray(Array.with(StateSystem.DEAD))))
                        .add(RoomIdComponent(roomId))
                        .add(SizeComponent())


            }

            "chair2" -> {
                if (r == 2) return false
                Entity()
                        .add(BodyComponent(b2DWorldCreator.defineRectangleMapObjectBody(
                        rect,
                        BodyDef.BodyType.StaticBody,
                        70 / PIX_PER_M,
                        128 / PIX_PER_M,
                        BREAKABLE_BIT,
                        WEAPON_BIT,
                        atlas.findRegion("Chair (2)"))))
                        .add(DamageComponent(5))
                        .add(StateComponent(ImmutableArray(Array.with(StateSystem.DEAD))))
                        .add(RoomIdComponent(roomId))
                        .add(SizeComponent())


            }

            "table" -> {
                if (r == 2) return false
                Entity()
                        .add(BodyComponent(b2DWorldCreator.defineRectangleMapObjectBody(
                        rect,
                        BodyDef.BodyType.StaticBody,
                        137 / PIX_PER_M,
                        69 / PIX_PER_M,
                        BREAKABLE_BIT,
                        WEAPON_BIT,
                        atlas.findRegion("Table"))))
                        .add(DamageComponent(5))
                        .add(StateComponent(ImmutableArray(Array.with(StateSystem.DEAD))))
                        .add(RoomIdComponent(roomId))
                        .add(SizeComponent())


            }

            "chandelier" -> {
                Entity()
                        .add(BodyComponent(b2DWorldCreator.defineRectangleMapObjectBody(
                        rect,
                        BodyDef.BodyType.StaticBody,
                        243 / PIX_PER_M,
                        120 / PIX_PER_M,
                        BREAKABLE_BIT,
                        WEAPON_BIT)))
                        .add(DamageComponent(5))
                        .add(AnimationComponent(hashMapOf(StateSystem.STANDING to
                                animCreator.create("Chandelier", 4, .125f, atlas))))
                        .add(StateComponent(
                                ImmutableArray(Array.with(StateSystem.STANDING, StateSystem.DEAD)),
                                MathUtils.random()
                        ))
                        .add(RoomIdComponent(roomId))
                        .add(SizeComponent())


            }

            "groundEnemy" -> {
                val skeletonAtlas = assets["Packs/Enemies/Skeleton.pack", TextureAtlas::class.java]
                when (r) {
                    1, 2, 3, 4, 5 -> {

                        val body = b2DWorldCreator.defineRectangleMapObjectBody(
                                rect,
                                BodyDef.BodyType.DynamicBody,
                                85 / PIX_PER_M,
                                228 / PIX_PER_M,
                                AI_BIT,
                                WEAPON_BIT or GROUND_BIT or PLATFORM_BIT)

                        Entity()
                                .add(BodyComponent(body))
                                .add(DamageComponent(2 * roomId + 1))
                                .add(AnimationComponent(hashMapOf(
                                        StateSystem.STANDING to Animation(
                                                .125f,
                                                skeletonAtlas.findRegions("idle"),
                                                Animation.PlayMode.LOOP),
                                        StateSystem.ATTACKING to Animation(
                                                .125f,
                                                skeletonAtlas.findRegions("hit"),
                                                Animation.PlayMode.LOOP),
                                        StateSystem.DEAD to Animation(
                                                .125f,
                                                skeletonAtlas.findRegions("die"),
                                                Animation.PlayMode.NORMAL),
                                        StateSystem.APPEARING to Animation(
                                                .1f,
                                                skeletonAtlas.findRegions("appear"),
                                                Animation.PlayMode.LOOP),
                                        StateSystem.RUNNING to Animation(
                                                .125f,
                                                skeletonAtlas.findRegions("go"),
                                                Animation.PlayMode.LOOP))
                                ))
                                .add(StateComponent(
                                        ImmutableArray(Array.with(
                                                StateSystem.STANDING,
                                                StateSystem.ATTACKING,
                                                StateSystem.DEAD,
                                                StateSystem.APPEARING,
                                                StateSystem.RUNNING))
                                ))
                                .add(RoomIdComponent(roomId))
                                .add(RunComponent(.4f, 4f))
                                .add(AIComponent())
                                .add(SizeComponent(.65f))
                                .add(WeaponComponent(
                                        type = WeaponSystem.SWING,
                                        strength = 2 * roomId + 1,
                                        attackSpeed = .00001f,
                                        nearAttackStrength = 2 * roomId + 1,
                                        knockback = Vector2(.1f, .1f),
                                        entityLeft = createSwingWeaponEntity(
                                                35,
                                                125,
                                                body,
                                                b2DWorldCreator,
                                                Vector2(-.1f, -.3f),
                                                Vector2(0f, -.5f))
                                                .add(RoomIdComponent(roomId)),
                                        entityRight = createSwingWeaponEntity(
                                                35,
                                                125,
                                                body,
                                                b2DWorldCreator,
                                                Vector2(.1f, -.3f),
                                                Vector2(0f, -.5f))
                                                .add(RoomIdComponent(roomId))))

                    }
                    else -> return false
                }
            }

            else -> throw Exception("NO SUCH CLASS: $objectPath")
        })
        return true
    }

    fun loadStaticMapObject(roomPath: String,
                                    objectPath: String,
                                    atlas: TextureAtlas = assets["Packs/items.pack", TextureAtlas::class.java]){

        val layer = assets.get(roomPath, TiledMap::class.java).layers[objectPath]

        layer?.objects?.forEach {
            engine.addEntity(when(objectPath){
                "lighting" -> {
                    Entity()
                            .add(BodyComponent(b2DWorldCreator.defineRectangleMapObjectBody(
                            (it as RectangleMapObject).rectangle,
                            BodyDef.BodyType.StaticBody,
                            98 / PIX_PER_M,
                            154 / PIX_PER_M,
                            STATIC_BIT,
                            -1,
                            atlas.findRegion("Lighting"))))
                            .add(AnimationComponent(hashMapOf(StateSystem.STANDING to
                                    animCreator.create("Lighting", 4, .125f, atlas))))
                            .add(StateComponent(ImmutableArray(Array.with(StateSystem.STANDING)),
                                    MathUtils.random()))
                            .add(StaticComponent(roomPath))
                            .add(SizeComponent())
                }
                "torch" -> {
                    Entity()
                            .add(BodyComponent(b2DWorldCreator.defineRectangleMapObjectBody(
                            (it as RectangleMapObject).rectangle,
                            BodyDef.BodyType.StaticBody,
                            178 / PIX_PER_M,
                            116 / PIX_PER_M,
                            STATIC_BIT,
                            -1,
                            atlas.findRegion("Torch"))))
                            .add(AnimationComponent(hashMapOf(StateSystem.STANDING to
                                    animCreator.create("Torch", 4, .125f, atlas))))
                            .add(StateComponent(ImmutableArray(Array.with(StateSystem.STANDING)),
                                    MathUtils.random()))
                            .add(StaticComponent(roomPath))
                            .add(SizeComponent())


                }
                "ground" -> createGroundEntity(it, roomPath, GROUND_BIT)
                "platform" -> createGroundEntity(it, roomPath, PLATFORM_BIT)
                else -> throw Exception("NO SUCH CLASS $objectPath")
            })
        }
    }
}