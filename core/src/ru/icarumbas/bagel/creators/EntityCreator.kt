package ru.icarumbas.bagel.creators

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.utils.ImmutableArray
import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.maps.MapObject
import com.badlogic.gdx.maps.objects.PolylineMapObject
import com.badlogic.gdx.maps.objects.RectangleMapObject
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
import ru.icarumbas.bagel.systems.other.StateSwapSystem
import ru.icarumbas.bagel.systems.physics.WeaponSystem
import ru.icarumbas.bagel.utils.createRevoluteJoint
import kotlin.experimental.or


class EntityCreator(private val b2DWorldCreator: B2DWorldCreator){

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
                        WEAPON_BIT,
                        AI_BIT or BREAKABLE_BIT or PLAYER_BIT,
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
                .add(SizeComponent(110 / PIX_PER_M * .75f, 155 / PIX_PER_M * .75f))
                .add(JumpComponent(.07f, 5))
                .add(DamageComponent(100))
                .add(BodyComponent(playerBody))
                .add(EquipmentComponent())
                .add(AttackComponent(
                        strength = 5,
                        attackSpeed = .00025f,
                        nearAttackStrength = 0,
                        knockback = Vector2(1.5f, 1f)))
                .add(WeaponComponent(
                        type = WeaponSystem.SWING,
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
                        StateSwapSystem.RUNNING to animCreator.create("Run", 10, .075f, atlas),
                        StateSwapSystem.JUMPING to animCreator.create("Jump", 10, .15f, atlas),
                        StateSwapSystem.STANDING to animCreator.create("Idle", 10, .1f, atlas),
                        StateSwapSystem.DEAD to animCreator.create("Dead", 10, .1f, atlas, Animation.PlayMode.NORMAL),
                        StateSwapSystem.ATTACKING to animCreator.create("Attack", 7, .075f, atlas),
                        StateSwapSystem.WALKING to animCreator.create("Walk", 10, .075f, atlas),
                        StateSwapSystem.JUMP_ATTACKING to animCreator.create("JumpAttack", 10, .075f, atlas)
                )))
                .add(AlwaysRenderingMarkerComponent())

    }


    fun createGroundEntity(obj: MapObject, path: String, bit: Short): Entity{
        return Entity()
                .add(StaticComponent(path))
                .add(when (obj) {
                    is RectangleMapObject -> {
                        BodyComponent(b2DWorldCreator.defineRectangleMapObjectBody(
                                obj.rectangle,
                                BodyDef.BodyType.StaticBody,
                                bit,
                                obj.rectangle.width / PIX_PER_M,
                                obj.rectangle.height / PIX_PER_M))
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

    fun createMapObjectEntity(rect: Rectangle,
                                    width: Int,
                                    height: Int,
                                    bType: BodyDef.BodyType,
                                    cBit: Short,
                                    mBit: Short,
                                    tex: TextureRegion? = null): Entity{
        return Entity()
                .add(BodyComponent(b2DWorldCreator.defineRectangleMapObjectBody(
                        rect,
                        bType,
                        cBit,
                        width / PIX_PER_M,
                        height / PIX_PER_M,
                        mBit,
                        tex

                )))
                .add(SizeComponent(
                        width / PIX_PER_M,
                        height / PIX_PER_M))

    }
}