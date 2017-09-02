package ru.icarumbas.bagel.creators

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.utils.ImmutableArray
import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.maps.MapObject
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.Body
import com.badlogic.gdx.physics.box2d.BodyDef
import com.badlogic.gdx.utils.Array
import ru.icarumbas.AI_BIT
import ru.icarumbas.BREAKABLE_BIT
import ru.icarumbas.PIX_PER_M
import ru.icarumbas.PLAYER_WEAPON_BIT
import ru.icarumbas.bagel.components.other.*
import ru.icarumbas.bagel.components.physics.BodyComponent
import ru.icarumbas.bagel.components.physics.StaticComponent
import ru.icarumbas.bagel.components.rendering.AnimationComponent
import ru.icarumbas.bagel.components.rendering.SizeComponent
import ru.icarumbas.bagel.components.velocity.JumpComponent
import ru.icarumbas.bagel.components.velocity.RunComponent
import ru.icarumbas.bagel.systems.other.StateSwapSystem
import ru.icarumbas.bagel.systems.physics.WeaponSystem
import ru.icarumbas.bagel.utils.createRevoluteJoint
import kotlin.experimental.or


class EntityCreator(private val b2DWorldCreator: B2DWorldCreator,
                    private val animCreator: AnimationCreator){

    fun createSwingWeaponEntity(path: String,
                                atlas: TextureAtlas,
                                width: Int,
                                height: Int,
                                playerBody: Body,
                                b2DWorldCreator: B2DWorldCreator,
                                anchorA: Vector2,
                                anchorB: Vector2): Entity{
        return Entity()
                .add(BodyComponent(b2DWorldCreator.createSwordWeapon(
                        PLAYER_WEAPON_BIT,
                        AI_BIT or BREAKABLE_BIT,
                        atlas.findRegion(path),
                        Vector2(width / PIX_PER_M, height / PIX_PER_M)).createRevoluteJoint(playerBody, anchorA, anchorB)))
                .add(SizeComponent(width / PIX_PER_M, height / PIX_PER_M))
                .add(AlwaysRenderingMarkerComponent())
    }

    fun createPlayerEntity(animCreator: AnimationCreator,
                           atlas: TextureAtlas,
                           playerBody: Body,
                           weaponEntityLeft: Entity,
                           weaponEntityRight: Entity): Entity {

        return Entity()
                .add(PlayerComponent(0))
                .add(RunComponent(.01f, 6f))
                .add(SizeComponent(110 / PIX_PER_M * .75f, 145 / PIX_PER_M * .75f))
                .add(JumpComponent(.07f, 5))
                .add(DamageComponent(100))
                .add(BodyComponent(playerBody))
                .add(EquipmentComponent())
                .add(WeaponComponent(
                        type = WeaponSystem.SWING,
                        strength = 5,
                        attackSpeed = .00025f,
                        nearAttackStrength = 0,
                        knockback = 0f,
                        entityLeft = weaponEntityLeft,
                        entityRight = weaponEntityRight))
                .add(StateComponent(ImmutableArray<String>(Array.with(
                        StateSwapSystem.RUNNING,
                        StateSwapSystem.JUMPING,
                        StateSwapSystem.STANDING,
                        StateSwapSystem.ATTACKING,
                        StateSwapSystem.DEAD,
                        StateSwapSystem.WALKING,
                        StateSwapSystem.JUMP_ATTACKING
                ))))
                .add(AnimationComponent(hashMapOf(
                        StateSwapSystem.RUNNING to animCreator.create("Run", 10, .075f, Animation.PlayMode.LOOP, atlas),
                        StateSwapSystem.JUMPING to animCreator.create("Jump", 10, .15f, Animation.PlayMode.LOOP, atlas),
                        StateSwapSystem.STANDING to animCreator.create("Idle", 10, .1f, Animation.PlayMode.LOOP, atlas),
                        StateSwapSystem.DEAD to animCreator.create("Dead", 10, .1f, Animation.PlayMode.LOOP, atlas),
                        StateSwapSystem.ATTACKING to animCreator.create("Attack", 7, .075f, Animation.PlayMode.LOOP, atlas),
                        StateSwapSystem.WALKING to animCreator.create("Walk", 10, .075f, Animation.PlayMode.LOOP, atlas),
                        StateSwapSystem.JUMP_ATTACKING to animCreator.create("JumpAttack", 10, .075f, Animation.PlayMode.LOOP, atlas)
                )))
                .add(AlwaysRenderingMarkerComponent())

    }

    fun createGroundEntity(obj: MapObject, path: String, bit: Short): Entity{
        return Entity()
                .add(StaticComponent(path))
                .add(BodyComponent(b2DWorldCreator.defineMapObjectBody(obj, BodyDef.BodyType.StaticBody, bit)))

    }

    fun createMapObjectEntity(obj: MapObject,
                                    atlas: TextureAtlas,
                                    path: String,
                                    width: Int,
                                    height: Int,
                                    bType: BodyDef.BodyType,
                                    cBit: Short,
                                    mBit: Short): Entity{
        return Entity()
                .add(BodyComponent(b2DWorldCreator.defineMapObjectBody(
                        obj,
                        bType,
                        cBit,
                        width / PIX_PER_M,
                        height / PIX_PER_M,
                        mBit,
                        atlas.findRegion(path)

                )))
                .add(SizeComponent(
                        width / PIX_PER_M,
                        height / PIX_PER_M))

    }

    fun createMapObjectStaticAnimationEntity(obj: MapObject,
                                    roomPath: String,
                                    atlas: TextureAtlas,
                                    path: String,
                                    width: Int,
                                    height: Int,
                                    animSpeed: Float,
                                    animCount: Int,
                                    bType: BodyDef.BodyType,
                                    cBit: Short,
                                    mBit: Short): Entity{
        return createMapObjectEntity(obj, atlas, path, width, height, bType, cBit, mBit)
                .add(AnimationComponent(hashMapOf(
                        StateSwapSystem.STANDING to animCreator.create(path, animCount, animSpeed, Animation.PlayMode.LOOP, atlas))
                ))
                .add(StateComponent(ImmutableArray<String>(Array.with(
                        StateSwapSystem.AllStates.STANDING))))
                .add(StaticComponent(roomPath))


    }

    fun createMapObjectIdEntity(obj: MapObject,
                                atlas: TextureAtlas,
                                path: String,
                                width: Int,
                                height: Int,
                                id: Int,
                                bType: BodyDef.BodyType
                                ): Entity{
        return createMapObjectEntity(obj, atlas, path, width, height, bType, BREAKABLE_BIT, PLAYER_WEAPON_BIT)
                .add(RoomIdComponent(id))
                .add(StateComponent(ImmutableArray<String>(Array.with(
                        StateSwapSystem.AllStates.DEAD))))
                .add(DamageComponent(5))


    }
}