package ru.icarumbas.bagel.Characters.Enemies

import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.physics.box2d.*
import ru.icarumbas.bagel.Characters.Player
import ru.icarumbas.bagel.Screens.GameScreen
import ru.icarumbas.bagel.Utils.WorldCreate.AnimationCreator


abstract class Enemy {

    abstract var destroyed: Boolean
    abstract var body: Body
    abstract var sprite: Sprite?
    abstract val bit: Short
    abstract var stateTimer: Float
    abstract var stateAnimation: Animation<*>
    abstract var runAnimation: Animation<*>
    abstract var posX : Float
    abstract var posY: Float


    fun defineBody(world: World){
        val fixtureDef = FixtureDef()
        val def = BodyDef()
        val shape = PolygonShape()

        def.position.x = posX.plus(sprite!!.width.div(2))
        def.position.y = posY.plus(sprite!!.width.div(2))

        shape.setAsBox(sprite!!.width.div(2), sprite!!.height.div(2))
        fixtureDef.shape = shape
        fixtureDef.friction = 1f
        fixtureDef.filter.categoryBits = bit

        body = world.createBody(def)
        body.createFixture(fixtureDef)
        body.isActive = false
        body.isFixedRotation = true
    }

    private fun getState(): GameScreen.State {
        return GameScreen.State.Standing
    }

    fun flipOnPlayer(player: Player){

        if (player.x > sprite!!.x && !sprite!!.isFlipX){
            sprite!!.flip(true, false)
        } else
        if (player.x < sprite!!.x && sprite!!.isFlipX){
            sprite!!.flip(true, false)
        }
    }

    fun getFrame(delta: Float): Sprite{
        val currentState = getState()
        stateTimer += delta

        when (currentState) {
            GameScreen.State.Standing -> return stateAnimation.getKeyFrame(stateTimer) as Sprite
            GameScreen.State.Running -> TODO()
            GameScreen.State.Jumping -> TODO()
            GameScreen.State.Attacking -> TODO()
            GameScreen.State.Dead -> TODO()
        }

    }

    abstract fun loadSprite(textureAtlas: TextureAtlas, animationCreator: AnimationCreator)

    fun draw(batch: Batch, delta: Float, gameScreen: GameScreen) {
        if (!destroyed) {
            sprite!!.set(getFrame(delta))
            sprite!!.setPosition(body.position.x, body.position.y)
            flipOnPlayer(gameScreen.player)
            sprite!!.draw(batch)

        }
    }
}