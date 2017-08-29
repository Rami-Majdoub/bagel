package ru.icarumbas.bagel

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.utils.ImmutableArray
import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.Body
import com.badlogic.gdx.utils.Array
import ru.icarumbas.OTHER_ENTITY_BIT
import ru.icarumbas.PIX_PER_M
import ru.icarumbas.PLAYER_WEAPON_BIT
import ru.icarumbas.bagel.components.other.*
import ru.icarumbas.bagel.components.physics.BodyComponent
import ru.icarumbas.bagel.components.rendering.AnimationComponent
import ru.icarumbas.bagel.components.rendering.SizeComponent
import ru.icarumbas.bagel.components.velocity.JumpComponent
import ru.icarumbas.bagel.components.velocity.RunComponent
import ru.icarumbas.bagel.systems.other.StateSwapSystem
import ru.icarumbas.bagel.systems.physics.WeaponSystem
import ru.icarumbas.bagel.utils.createRevoluteJoint


class EntityCreator {

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
                        OTHER_ENTITY_BIT,
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
                .add(RunComponent())
                .add(ParametersComponent(
                        HP = 100,
                        acceleration = .02f,
                        maxSpeed = 6f,
                        strength = 5,
                        knockback = 0f,
                        nearAttackStrength = 0,
                        jumpVelocity = .16f,
                        maxJumps = 5,
                        attackSpeed = .00025f
                ))
                .add(SizeComponent(110 / PIX_PER_M, 145 / PIX_PER_M))
                .add(JumpComponent())
                .add(DamageComponent())
                .add(BodyComponent(playerBody))
                .add(EquipmentComponent())
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
                        StateSwapSystem.RUNNING to animCreator.create("Run", 10, .075f, Animation.PlayMode.LOOP, atlas),
                        StateSwapSystem.JUMPING to animCreator.create("Jump", 10, .15f, Animation.PlayMode.LOOP, atlas),
                        StateSwapSystem.STANDING to animCreator.create("Idle", 10, .1f, Animation.PlayMode.LOOP, atlas),
                        StateSwapSystem.DEAD to animCreator.create("Dead", 10, .1f, Animation.PlayMode.LOOP, atlas),
                        StateSwapSystem.ATTACKING to animCreator.create("Attack", 7, .075f, Animation.PlayMode.LOOP, atlas),
                        StateSwapSystem.WALKING to animCreator.create("Walk", 10, .075f, Animation.PlayMode.LOOP, atlas),
                        StateSwapSystem.JUMP_ATTACKING to animCreator.create("JumpAttack", 10, .075f, Animation.PlayMode.LOOP, atlas)
                )))

    }
}