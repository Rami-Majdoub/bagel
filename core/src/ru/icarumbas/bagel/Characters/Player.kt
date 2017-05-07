package ru.icarumbas.bagel.Characters

import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.*
import com.badlogic.gdx.utils.Array
import ru.icarumbas.Bagel
import ru.icarumbas.bagel.Screens.GameScreen

class Player(val gameScreen: GameScreen, val game: Bagel) : Sprite() {
    lateinit var playerBody: Body
    private val stateAnimation: Animation<*>
    private val runAnimation: Animation<*>
    private val jumpAnimation: Animation<*>
    val playerBodyHeight = 1.1f
    val playerBodyWidth = .71f


    private enum class State {
        Standing, Running, Jumping
    }

    private var runningRight = true
    var lastRight: Boolean = false
    private var stateTimer = 0f
    var bodyDef: BodyDef

    init {

        val atlas = TextureAtlas("Packs/Knight.txt")

        bodyDef = BodyDef()
        bodyDef.position.set(3f, 2.5f)

        definePlayer()

        val frames = Array<Sprite>()

        (1..10).forEach { frames.add(Sprite(atlas.findRegion("Idle ($it)"))) }

        stateAnimation = Animation(.1f, frames)
        stateAnimation.playMode = Animation.PlayMode.LOOP
        frames.clear()

        (1..10).forEach { frames.add(Sprite(atlas.findRegion("Run ($it)"))) }

        runAnimation = Animation(.1f, frames)
        runAnimation.playMode = Animation.PlayMode.LOOP
        frames.clear()


        (1..10).forEach { frames.add(Sprite(atlas.findRegion("Jump ($it)"))) }
        jumpAnimation = Animation(.1f, frames)
        jumpAnimation.playMode = Animation.PlayMode.LOOP
        frames.clear()
    }

    fun definePlayer() {
        bodyDef.type = BodyDef.BodyType.DynamicBody
        playerBody = gameScreen.world.createBody(bodyDef)


        val shape = PolygonShape()
        shape.setAsBox(.23f, .49f)

        val fixtureDef = FixtureDef()
        fixtureDef.shape = shape
        fixtureDef.restitution = 0f
        fixtureDef.friction = .2f
        fixtureDef.density = .2f

        playerBody.createFixture(fixtureDef)

        val circleShape = CircleShape()
        circleShape.radius = .3f
        circleShape.position = Vector2(0f, .2f)

        fixtureDef.shape = circleShape
        fixtureDef.friction = 0f

        playerBody.createFixture(fixtureDef)


        circleShape.radius = .25f
        circleShape.position = Vector2(0f, -.28f)
        fixtureDef.friction = 1f
        fixtureDef.filter.categoryBits = game.PLAYER_BIT
        playerBody.createFixture(fixtureDef)

        setSize(playerBodyWidth, playerBodyHeight)
        setOrigin(width / 2f, height / 2f)

        playerBody.isFixedRotation = true
        playerBody.userData = this
    }

    fun update(delta: Float) {
        setRegion(getFrame(delta))
        setPosition(playerBody.position.x - width / 2, playerBody.position.y - height / 2)
        rotation = playerBody.angle * MathUtils.radiansToDegrees

        if (playerBody.linearVelocity.y == 0f && gameScreen.hud.touchpad.knobY < gameScreen.hud.touchpad.height - 45) {
            gameScreen.hud.doubleJump = 0
            gameScreen.hud.jumping = false
        }
    }

    private fun getFrame(dt: Float): TextureRegion {
        val currentState = state

        var region = TextureRegion()

        when (currentState) {
            Player.State.Jumping -> region = jumpAnimation.getKeyFrame(stateTimer) as TextureRegion
            Player.State.Running -> region = runAnimation.getKeyFrame(stateTimer) as TextureRegion
            Player.State.Standing -> region = stateAnimation.getKeyFrame(stateTimer) as TextureRegion
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

    fun setPlayerPosition(game: Bagel, side: String, player: Player, plX: Int, plY: Int, previousMapLink: Int) {

        if (side == "Up" || side == "Down") {
            // Compare top-right parts of previous and current maps
            val X10 = gameScreen.rooms[gameScreen.currentMap].meshVertices[2]
            val prevX = gameScreen.rooms[previousMapLink].meshVertices[plX]

            if (side == "Up") {
                if (prevX == X10) player.playerBody.setTransform(gameScreen.rooms[gameScreen.currentMap].mapWidth - game.REG_ROOM_WIDTH/2, 0f, 0f)
                else player.playerBody.setTransform(game.REG_ROOM_WIDTH/2, 0f, 0f)
            }
            if (side == "Down") {
                if (prevX == X10) player.playerBody.setTransform(gameScreen.rooms[gameScreen.currentMap].mapWidth - game.REG_ROOM_WIDTH/2,
                                                                 gameScreen.rooms[gameScreen.currentMap].mapHeight, 0f)
                else player.playerBody.setTransform(game.REG_ROOM_WIDTH/2, gameScreen.rooms[gameScreen.currentMap].mapHeight, 0f)
            }
        }

        if (side == "Left" || side == "Right") {
            // Compare top parts of previous and current maps
            val Y11 = gameScreen.rooms[gameScreen.currentMap].meshVertices[3]
            val prevY = gameScreen.rooms[previousMapLink].meshVertices[plY]

            if (side == "Left") {
                if (prevY == Y11) player.playerBody.setTransform(gameScreen.rooms[gameScreen.currentMap].mapWidth,
                                                                 gameScreen.rooms[gameScreen.currentMap].mapHeight - game.REG_ROOM_HEIGHT/2, 0f)
                else player.playerBody.setTransform(gameScreen.rooms[gameScreen.currentMap].mapWidth, game.REG_ROOM_HEIGHT/2, 0f)
            }
            if (side == "Right") {
                if (prevY == Y11) player.playerBody.setTransform(0f, gameScreen.rooms[gameScreen.currentMap].mapHeight - game.REG_ROOM_HEIGHT/2, 0f)
                else player.playerBody.setTransform(0f, game.REG_ROOM_HEIGHT/2, 0f)
            }
        }


    }

    private val state: State
        get() {
            if (gameScreen.hud.jumping && playerBody.linearVelocity.y != 0f)
                return State.Jumping
            if ((gameScreen.hud.touchpad.knobX < gameScreen.hud.touchpad.width / 2 ||
                    gameScreen.hud.touchpad.knobX > gameScreen.hud.touchpad.width / 2) &&
                    !gameScreen.hud.jumping && gameScreen.hud.touchedFirst)

                return State.Running
            else
                return State.Standing
        }
}




