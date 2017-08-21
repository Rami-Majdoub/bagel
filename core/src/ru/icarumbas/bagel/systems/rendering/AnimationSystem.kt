package ru.icarumbas.bagel.systems.rendering

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.graphics.g2d.TextureRegion
import ru.icarumbas.bagel.components.other.PlayerComponent
import ru.icarumbas.bagel.components.other.RoomIdComponent
import ru.icarumbas.bagel.components.other.StateComponent
import ru.icarumbas.bagel.components.physics.StaticComponent
import ru.icarumbas.bagel.components.rendering.AnimationComponent
import ru.icarumbas.bagel.screens.GameScreen
import ru.icarumbas.bagel.systems.other.StateSwapSystem
import ru.icarumbas.bagel.utils.Mappers
import ru.icarumbas.bagel.utils.inView


class AnimationSystem : IteratingSystem {

    private val anim = Mappers.animation
    private val state = Mappers.state
    private val weapon = Mappers.weapon
    private val body = Mappers.body
    private val ai = Mappers.ai
    private val run = Mappers.run

    private val gs: GameScreen


    constructor(gs: GameScreen) : super(Family.all(
            AnimationComponent::class.java,
            StateComponent::class.java)
            .one(
            PlayerComponent::class.java,
            RoomIdComponent::class.java,
            StaticComponent::class.java).get()) {
        this.gs = gs
    }

    fun flip(e: Entity) {

        val textureReg = body[e].body.userData as TextureRegion

        if (ai.has(e)) {
            if (ai[e].isPlayerRight && textureReg.isFlipX) {
                textureReg.flip(true, false)
            } else
                if (!ai[e].isPlayerRight && textureReg.isFlipX) {
                    textureReg.flip(true, false)
                }
        }

        if (run.has(e)) {
            if (run[e].lastRight && textureReg.isFlipX) {
                textureReg.flip(true, false)
            } else
                if (!run[e].lastRight && !textureReg.isFlipX) {
                    textureReg.flip(true, false)
                }
        }
    }

    override fun processEntity(entity: Entity, deltaTime: Float) {

        state[entity].stateTime += deltaTime

        if (entity.inView(gs.currentMapId, gs.rooms)) {
            if (anim[entity].animations.containsKey(state[entity].currentState)) {
                body[entity].body.userData =
                                anim[entity].
                                animations[state[entity].
                                currentState]!!.
                                getKeyFrame(state[entity].stateTime, true)

            } else {
                if (state[entity].currentState == StateSwapSystem.ATTACKING) {
                    if (weapon.has(entity)) {
                        weapon[entity].weaponBody?.userData = weapon[entity].weaponAnimation?.getKeyFrame(weapon[entity].stateTimer)
                        weapon[entity].bulletBodies?.forEach {
                            it.userData = weapon[entity].bulletAnimation?.getKeyFrame(weapon[entity].stateTimer)
                        }
                    }
                }
            }
        }

        flip(entity)

    }
}