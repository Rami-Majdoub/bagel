package ru.icarumbas.bagel.Characters

import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.*
import com.badlogic.gdx.utils.Array
import ru.icarumbas.GROUND_BIT
import ru.icarumbas.PLAYER_BIT
import ru.icarumbas.REG_ROOM_HEIGHT
import ru.icarumbas.REG_ROOM_WIDTH
import ru.icarumbas.bagel.Screens.GameScreen

class Player(val gameScreen: GameScreen) : Sprite() {
    lateinit var playerBody: Body
    private val stateAnimation: Animation<*>
    private val runAnimation: Animation<*>
    private val jumpAnimation: Animation<*>
    private val attackAnimation: Animation<*>
    val atlas = TextureAtlas("Packs/GuyKnight.txt")
    var attacking = false


    private enum class State {
        Standing, Running, Jumping, Attacking
    }

    private var runningRight = true
    var lastRight: Boolean = false
    private var stateTimer = 0f
    var bodyDef = BodyDef()

    init {
        bodyDef.position.set(6f, 5f)
        definePlayer()

        // Animation
        stateAnimation = createAnimation("Idle", 10, .1f, Animation.PlayMode.LOOP)
        runAnimation = createAnimation("Run", 10, .1f, Animation.PlayMode.LOOP)
        jumpAnimation = createAnimation("Jump", 10, .1f, Animation.PlayMode.LOOP)
        attackAnimation = createAnimation("Attack", 10, .05f, Animation.PlayMode.LOOP)

    }

    private fun createAnimation(path: String, count: Int, animSpeed: Float, animPlaymode: Animation.PlayMode): Animation<*> {
        val frames = Array<Sprite>(count)
        (1..count).forEach { frames.add(Sprite(atlas.findRegion("$path ($it)"))) }
        val animation = Animation(animSpeed, frames)
        animation.playMode = animPlaymode
        frames.clear()
        return animation
    }

    fun definePlayer() {
        bodyDef.type = BodyDef.BodyType.DynamicBody
        playerBody = gameScreen.world.createBody(bodyDef)


        val shape = PolygonShape()
        shape.setAsBox(.3f, .6f)

        val fixtureDef = FixtureDef()
        fixtureDef.filter.categoryBits = PLAYER_BIT
        fixtureDef.shape = shape
        fixtureDef.restitution = 0f
        fixtureDef.friction = .4f
        fixtureDef.density = .04f

        playerBody.createFixture(fixtureDef)

        val circleShape = CircleShape()

        fixtureDef.shape = circleShape

        circleShape.radius = .3f
        circleShape.position = Vector2(0f, -.45f)
        fixtureDef.friction = 1.5f
        playerBody.createFixture(fixtureDef)


        setSize(1.1f, 1.45f)
        setOrigin(width / 2f, height / 2f)

        playerBody.isFixedRotation = true
    }

    fun update(delta: Float) {
        setRegion(getFrame(delta))
        setPosition(playerBody.position.x - width / 2, playerBody.position.y - height / 2)
        rotation = playerBody.angle * MathUtils.radiansToDegrees
    }

    private fun getFrame(dt: Float): TextureRegion {
        val currentState = state

        var region = TextureRegion()

        when (currentState) {
            State.Jumping -> region = jumpAnimation.getKeyFrame(stateTimer) as TextureRegion
            State.Running -> region = runAnimation.getKeyFrame(stateTimer) as TextureRegion
            State.Standing -> region = stateAnimation.getKeyFrame(stateTimer) as TextureRegion
            State.Attacking -> region = attackAnimation.getKeyFrame(stateTimer) as TextureRegion

        }

        if ((playerBody.linearVelocity.x < 0 || !runningRight) && !region.isFlipX && !lastRight) {
            region.flip(true, false)
            runningRight = false
        }
        if ((playerBody.linearVelocity.x > 0 || runningRight) && region.isFlipX && lastRight) {
            region.flip(true, false)
            runningRight = true
        }

        stateTimer += dt

        return region
    }

    fun setPlayerPosition(side: String, player: Player, plX: Int, plY: Int, previousMapLink: Int) {

        if (side == "Up" || side == "Down") {
            // Compare top-right parts of previous and current maps
            val X10 = gameScreen.rooms[gameScreen.currentMap].meshVertices[2]
            val prevX = gameScreen.rooms[previousMapLink].meshVertices[plX]

            if (side == "Up") {
                if (prevX == X10) player.playerBody.setTransform(gameScreen.rooms[gameScreen.currentMap].mapWidth - REG_ROOM_WIDTH / 2, 0f, 0f)
                else player.playerBody.setTransform(REG_ROOM_WIDTH/2, 0f, 0f)
            }
            if (side == "Down") {
                if (prevX == X10) player.playerBody.setTransform(gameScreen.rooms[gameScreen.currentMap].mapWidth - REG_ROOM_WIDTH / 2,
                                                                 gameScreen.rooms[gameScreen.currentMap].mapHeight, 0f)
                else player.playerBody.setTransform(REG_ROOM_WIDTH/2, gameScreen.rooms[gameScreen.currentMap].mapHeight, 0f)
            }
        }

        if (side == "Left" || side == "Right") {
            // Compare top parts of previous and current maps
            val Y11 = gameScreen.rooms[gameScreen.currentMap].meshVertices[3]
            val prevY = gameScreen.rooms[previousMapLink].meshVertices[plY]

            if (side == "Left") {
                if (prevY == Y11) player.playerBody.setTransform(gameScreen.rooms[gameScreen.currentMap].mapWidth,
                                  gameScreen.rooms[gameScreen.currentMap].mapHeight - REG_ROOM_HEIGHT / 2 - height/2, 0f)
                else player.playerBody.setTransform(gameScreen.rooms[gameScreen.currentMap].mapWidth, REG_ROOM_HEIGHT/2, 0f)
            }
            if (side == "Right") {
                if (prevY == Y11) player.playerBody.setTransform(0f, gameScreen.rooms[gameScreen.currentMap].mapHeight - REG_ROOM_HEIGHT / 2 - height/2, 0f)
                else player.playerBody.setTransform(0f, REG_ROOM_HEIGHT/2, 0f)
            }
        }


    }

    private var state = State.Standing
        get() {
            if (attacking) return State.Attacking
            if (gameScreen.hud.jumping && playerBody.linearVelocity.y != 0f)
                return State.Jumping
            if ((gameScreen.hud.touchpad.knobX < gameScreen.hud.touchpad.width / 2 ||
                    gameScreen.hud.touchpad.knobX > gameScreen.hud.touchpad.width / 2) &&
                    !gameScreen.hud.jumping && gameScreen.hud.touchedOnce)

                return State.Running
            else
                return State.Standing
        }
}




