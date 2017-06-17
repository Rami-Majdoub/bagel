package ru.icarumbas.bagel.Characters

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.*
import ru.icarumbas.*
import ru.icarumbas.bagel.Screens.GameScreen
import ru.icarumbas.bagel.Screens.Scenes.Hud
import ru.icarumbas.bagel.Utils.WorldCreate.AnimationCreator
import kotlin.experimental.or

class Player(val gameScreen: GameScreen, animationCreator: AnimationCreator) : Sprite() {

    lateinit var playerBody: Body
//    lateinit var swordBody: Body

    private val stateAnimation: Animation<*>
    private val runAnimation: Animation<*>
    private val jumpAnimation: Animation<*>
    private val attackAnimation: Animation<*>
    private val deadAnimation: Animation<*>

    val atlas = TextureAtlas("Packs/GuyKnight.txt")
    var attacking = false
    var jumping = false
    var doubleJump = 0

    private var stateTimerDead = 0f

    private var runningRight = true
    var lastRight: Boolean = false
    private var stateTimer = 0f

    val defaultColor = color!!
    var hitTimer = 0f
    var isFirstHit = true

    var HP = 100
    var money = 0

    init {
        definePlayer()

        setSize(1.1f, 1.45f)
        setOrigin(width / 2f, height / 2f)

        // Animation
        stateAnimation = animationCreator.createSpriteAnimation("Idle", 10, .1f, Animation.PlayMode.LOOP, atlas)
        runAnimation = animationCreator.createSpriteAnimation("Run", 10, .075f, Animation.PlayMode.LOOP, atlas)
        jumpAnimation = animationCreator.createSpriteAnimation("Jump", 10, .125f, Animation.PlayMode.LOOP, atlas)
        attackAnimation = animationCreator.createSpriteAnimation("Attack", 10, .05f, Animation.PlayMode.LOOP, atlas)
        deadAnimation = animationCreator.createSpriteAnimation("Dead", 10, 1f, Animation.PlayMode.NORMAL, atlas)

    }

    fun definePlayer() {
        val bodyDef = BodyDef()
        bodyDef.position.set(6f, 5f)

        bodyDef.type = BodyDef.BodyType.DynamicBody
        playerBody = gameScreen.world.createBody(bodyDef)

        val shape = PolygonShape()
        shape.setAsBox(.3f, .52f)

        val fixtureDef = FixtureDef()
        fixtureDef.shape = shape
        fixtureDef.restitution = 0f
        fixtureDef.friction = .4f
        fixtureDef.density = .04f
        fixtureDef.filter.categoryBits = PLAYER_BIT
        fixtureDef.filter.maskBits = PLATFORM_BIT or GROUND_BIT or SPIKE_BIT or SPIKE_TRAP_BIT or CHEST_BIT or COIN_BIT or ENEMY_BIT

        playerBody.createFixture(fixtureDef)

        val circleShape = CircleShape()
        fixtureDef.shape = circleShape
        circleShape.radius = .29f
        circleShape.position = Vector2(0f, -.4f)
        fixtureDef.friction = 1.5f

        playerBody.createFixture(fixtureDef)

        playerBody.isFixedRotation = true

//        defineWeapon(bodyDef, playerBody)
    }

   /* fun defineWeapon(bodyDef: BodyDef, playerBody: Body) {
        bodyDef.position.set(6.05f, 5.5f)
        swordBody = gameScreen.world.createBody(bodyDef)

        val fixtureDef = FixtureDef()
        val shape = ChainShape()
        shape.createChain(arrayOf(Vector2(.5f, 0f), Vector2(2f, .75f)))
        fixtureDef.shape = shape
        fixtureDef.friction = 0f
        fixtureDef.density = 0f
        fixtureDef.filter.categoryBits = GROUND_BIT
        swordBody.createFixture(fixtureDef)

        val swordJoint = RevoluteJointDef()
        swordJoint.collideConnected = false
        swordJoint.bodyA = playerBody
        swordJoint.bodyB = swordBody
        swordJoint.localAnchorA.set(.3f, 0f)
        swordJoint.localAnchorB.set(.45f, 0f)

        *//*swordJoint.enableMotor = true
        swordJoint.motorSpeed = 0f
        swordJoint.maxMotorTorque = 10f
        swordJoint.enableLimit = true
        swordJoint.lowerAngle = 1.2f
        swordJoint.upperAngle = 5f*//*

        gameScreen.world.createJoint(swordJoint)
    }*/

    fun update(delta: Float, hud: Hud) {
        hitTimer += delta
        if (hitTimer > .1f) color = defaultColor
        if (hitTimer > 2) isFirstHit = true

        isDead()
        detectJumping(hud)
        setRegion(getFrame(delta, hud))
        setPosition(playerBody.position.x - width / 2, playerBody.position.y - height / 2)
        rotation = playerBody.angle * MathUtils.radiansToDegrees
//        swordBody.applyLinearImpulse(Vector2(.25f, 0f), playerBody.worldCenter, true)
        hud.hp.setText("HP: $HP")
        hud.money.setText("Money: $money")

    }

    fun isDead(){
        if (deadAnimation.isAnimationFinished(stateTimerDead)) Gdx.app.exit()
    }

    private fun getFrame(dt: Float, hud: Hud): TextureRegion {
        val currentState = getState(hud)

        var region = TextureRegion()

        when (currentState) {
            GameScreen.State.Jumping -> region = jumpAnimation.getKeyFrame(stateTimer) as TextureRegion
            GameScreen.State.Running -> region = runAnimation.getKeyFrame(stateTimer) as TextureRegion
            GameScreen.State.Standing -> region = stateAnimation.getKeyFrame(stateTimer) as TextureRegion
            GameScreen.State.Attacking -> region = attackAnimation.getKeyFrame(stateTimer) as TextureRegion
            GameScreen.State.Dead -> region = deadAnimation.getKeyFrame(stateTimer) as TextureRegion

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

    fun setRoomPosition (side: String, plX: Int, plY: Int, previousMapLink: Int) {

        if (side == "Up" || side == "Down") {
            // Compare top-right parts of previous and current maps
            val X10 = gameScreen.rooms[gameScreen.currentMap].meshVertices[2]
            val prevX = gameScreen.rooms[previousMapLink].meshVertices[plX]

            if (side == "Up") {
                if (prevX == X10) {
                    playerBody.setTransform(gameScreen.rooms[gameScreen.currentMap].mapWidth - REG_ROOM_WIDTH / 2, 0f, 0f)
                }
                else playerBody.setTransform(REG_ROOM_WIDTH/2, 0f, 0f)
            }
            if (side == "Down") {
                if (prevX == X10) playerBody.setTransform(gameScreen.rooms[gameScreen.currentMap].mapWidth - REG_ROOM_WIDTH / 2,
                                                                 gameScreen.rooms[gameScreen.currentMap].mapHeight, 0f)
                else playerBody.setTransform(REG_ROOM_WIDTH/2, gameScreen.rooms[gameScreen.currentMap].mapHeight, 0f)
            }
        }

        if (side == "Left" || side == "Right") {
            // Compare top parts of previous and current maps
            val Y11 = gameScreen.rooms[gameScreen.currentMap].meshVertices[3]
            val prevY = gameScreen.rooms[previousMapLink].meshVertices[plY]

            if (side == "Left") {
                if (prevY == Y11) playerBody.setTransform(gameScreen.rooms[gameScreen.currentMap].mapWidth,
                                  gameScreen.rooms[gameScreen.currentMap].mapHeight - REG_ROOM_HEIGHT / 2 - height/2, 0f)
                else playerBody.setTransform(gameScreen.rooms[gameScreen.currentMap].mapWidth, REG_ROOM_HEIGHT/2, 0f)
            }
            if (side == "Right") {
                if (prevY == Y11)
                playerBody.setTransform(0f, gameScreen.rooms[gameScreen.currentMap].mapHeight - REG_ROOM_HEIGHT / 2 - height/2, 0f)
                else playerBody.setTransform(0f, REG_ROOM_HEIGHT/2, 0f)
            }
        }

//        setSwordPosition()

    }

//    private fun setSwordPosition() = swordBody.setTransform(playerBody.position.x + .05f, playerBody.position.y+.5f, 0f)

    fun getState(hud: Hud): GameScreen.State {
        if (HP <= 0) {
            stateTimerDead += .1f
            hud.stage.clear()
            return GameScreen.State.Dead
        } else
        if (attacking) return GameScreen.State.Attacking else
        if (jumping && playerBody.linearVelocity.y != 0f) return GameScreen.State.Jumping else
        if ((hud.touchpad.knobX < hud.touchpad.width / 2 || hud.touchpad.knobX > hud.touchpad.width / 2) && !jumping && hud.touchedOnce)
            return GameScreen.State.Running
        else
            return GameScreen.State.Standing
    }

    fun detectJumping(hud: Hud){
        if (playerBody.linearVelocity.x < 4f && hud.touchpad.knobX > hud.touchpad.width / 2 + hud.touchpad.width/20) {
            playerBody.applyLinearImpulse(Vector2(.03f, 0f), playerBody.worldCenter, true)
            lastRight = true
        }

        if (playerBody.linearVelocity.x > -4f && hud.touchpad.knobX < hud.touchpad.width / 2 - hud.touchpad.width/20) {
            playerBody.applyLinearImpulse(Vector2(-.03f, 0f), playerBody.worldCenter, true)
            lastRight = false
        }

        if (hud.touchpad.knobY > hud.touchpad.height / 2 + hud.touchpad.width/10 && doubleJump < 5 && playerBody.linearVelocity.y < 3.5) {
            if (doubleJump == 0) {
                jump(.15f)
            }
            if (doubleJump == 2 || doubleJump == 3 || doubleJump == 4) {
                jump(.07f)
            } else {
                jump(.05f)
            }

        }
        if (playerBody.linearVelocity.y == 0f && hud.touchpad.knobY <= hud.touchpad.height - hud.touchpad.height/2 + 5) {
            doubleJump = 0
            jumping = false
        }
    }

    private fun jump(velocity: Float){
        playerBody.applyLinearImpulse(Vector2(0f, velocity), playerBody.worldCenter, true)
        doubleJump++
    }

    fun hit(damage: Int, velocity: Vector2){
        if (hitTimer > .5f) {
            if (isFirstHit) {
                playerBody.applyLinearImpulse(velocity, playerBody.localPoint2, true)
                isFirstHit = false
            }

            color = Color.RED
            HP -= damage
            hitTimer = 0f
        }
    }
}




