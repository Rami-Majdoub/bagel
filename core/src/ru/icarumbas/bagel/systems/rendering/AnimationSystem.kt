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
import ru.icarumbas.bagel.utils.*


class AnimationSystem : IteratingSystem {

    private val anim = Mappers.animation
    private val state = Mappers.state
    private val weapon = Mappers.weapon
    private val body = Mappers.body

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

            if (e.rotatedRight() && textureReg.isFlipX) {
                textureReg.flip(true, false)
            } else
                if (!e.rotatedRight() && !textureReg.isFlipX) {
                    textureReg.flip(true, false)
                }
    }

    override fun processEntity(e: Entity, deltaTime: Float) {

        state[e].stateTime += deltaTime

        if (e.inView(gs.currentMapId, gs.rooms)) {
            if (state[e].currentState == StateSwapSystem.ATTACKING) {

                val frame = if (e.rotatedRight()){
                    body[weapon[e].entityRight].body.angleInDegrees().div(PI_DIV_7).toInt() * -1
                } else {
                    body[weapon[e].entityLeft].body.angleInDegrees().div(PI_DIV_7).toInt()
                }
                body[e].body.userData = anim[e].animations[state[e].currentState]!!.keyFrames.get(frame)

            } else
                if (anim[e].animations.containsKey(state[e].currentState)) {
                    body[e].body.userData =
                                    anim[e].
                                    animations[state[e].
                                    currentState]!!.
                                    getKeyFrame(state[e].stateTime, true)

                }

            if (weapon.has(e))
            weapon[e].bulletBodies?.forEach {
                it.userData = weapon[e].bulletAnimation?.getKeyFrame(weapon[e].stateTimer)
            }

            flip(e)

        }
    }
}