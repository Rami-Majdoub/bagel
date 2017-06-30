package ru.icarumbas.bagel.Characters

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.audio.Sound
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.*
import ru.icarumbas.*
import ru.icarumbas.bagel.Screens.GameScreen
import ru.icarumbas.bagel.Screens.MainMenuScreen
import ru.icarumbas.bagel.Screens.Scenes.Hud
import ru.icarumbas.bagel.Utils.WorldCreate.AnimationCreator
import kotlin.experimental.or

class Player(val gameScreen: GameScreen, animationCreator: AnimationCreator) : Sprite() {

    lateinit var playerBody: Body

    val atlas = gameScreen.game.assetManager["Packs/GuyKnight.txt", TextureAtlas::class.java]!!

    private val stateAnimation: Animation<*>
    private val runAnimation: Animation<*>
    private val jumpAnimation: Animation<*>
    private val attackAnimation: Animation<*>
    private val deadAnimation: Animation<*>

    private val stepSound: Sound
    private var stepSoundPlaying = false

    var attacking = false
    var jumping = false
    var doubleJump = 0
    var lastRight = true
    var runningRight = true


    private var stateTimerDead = 0f
    private var stateTimer = 0f

    var hitTimer = 0f
    var isFirstHit = true

    var strength = 35
    var HP = 100
    var money = 0

    init {
        definePlayer()

        setSize(1.1f, 1.45f)
        setOrigin(width / 2f, height / 2f)

        stepSound = gameScreen.game.assetManager["Sounds/steps.wav", Sound::class.java]
        stepSound.loop()

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
        fixtureDef.filter.maskBits =
                PLATFORM_BIT or
                GROUND_BIT or
                SPIKE_BIT or
                CHEST_BIT or
                COIN_BIT or
                ENEMY_BIT or
                PORTAL_DOOR_BIT

        playerBody.createFixture(fixtureDef)

        val circleShape = CircleShape()
        fixtureDef.shape = circleShape
        circleShape.radius = .29f
        circleShape.position = Vector2(0f, -.4f)
        fixtureDef.friction = 1.5f

        playerBody.createFixture(fixtureDef)

        playerBody.isFixedRotation = true

        defineWeapon(playerBody, fixtureDef)
    }

    fun defineWeapon(playerBody: Body, fixtureDef: FixtureDef) {
        fixtureDef.friction = 0f
        fixtureDef.density = 0f
        fixtureDef.filter.categoryBits = SWORD_BIT
        fixtureDef.filter.maskBits = BREAKABLE_BIT or ENEMY_BIT

        val shape = EdgeShape()
        fixtureDef.shape = shape

        shape.set(.3f, 0f, .75f, 0f)
        playerBody.createFixture(fixtureDef)

        shape.set(.3f, 0f, .75f, -.5f)
        playerBody.createFixture(fixtureDef)

        shape.set(.3f, 0f, .75f, .5f)
        playerBody.createFixture(fixtureDef)

        fixtureDef.filter.categoryBits = SWORD_BIT_LEFT
        shape.set(-.3f, 0f, -.75f, 0f)
        playerBody.createFixture(fixtureDef)

        shape.set(-.3f, 0f, -.75f, -.5f)
        playerBody.createFixture(fixtureDef)

        shape.set(-.3f, 0f, -.75f, .5f)
        playerBody.createFixture(fixtureDef)
    }

    fun update(delta: Float, hud: Hud) {
        hitTimer += delta
        if (hitTimer > .1f) color = Color.WHITE
        if (hitTimer > 1) isFirstHit = true

        when (getState(hud)) {
            GameScreen.State.Dead -> {
                stepSoundPlaying = false
                stepSound.pause()

                stateTimerDead += .1f
                hud.stage.clear()
            }

            GameScreen.State.Running -> {
                if (!stepSoundPlaying) {
                    stepSound.stop()
                    stepSoundPlaying = false
                }
            }

            GameScreen.State.Standing -> {
                stepSoundPlaying = false
                stepSound.pause()
            }

            GameScreen.State.Jumping -> {
                stepSoundPlaying = false
                stepSound.pause()
            }

            GameScreen.State.Attacking -> {
                stepSoundPlaying = false
                stepSound.pause()
            }
        }

        isDead()
        detectJumping(hud)
        setRegion(getFrame(delta, hud))
        setPosition(playerBody.position.x - width / 2, playerBody.position.y - height / 2)
        hud.hp.setText("HP: $HP")
        hud.money.setText("Money: $money")
    }

    fun isDead(){
        if (deadAnimation.isAnimationFinished(stateTimerDead)) {
            gameScreen.game.worldIO.preferences.putBoolean("CanContinueWorld", false)
            gameScreen.game.worldIO.preferences.flush()
            gameScreen.game.screen = MainMenuScreen(gameScreen.game)
        }
    }

    private fun getFrame(dt: Float, hud: Hud): TextureRegion {

        val region = when (getState(hud)) {
            GameScreen.State.Jumping -> jumpAnimation.getKeyFrame(stateTimer) as TextureRegion
            GameScreen.State.Running -> runAnimation.getKeyFrame(stateTimer) as TextureRegion
            GameScreen.State.Standing -> stateAnimation.getKeyFrame(stateTimer) as TextureRegion
            GameScreen.State.Attacking -> attackAnimation.getKeyFrame(stateTimer) as TextureRegion
            GameScreen.State.Dead -> deadAnimation.getKeyFrame(stateTimer) as TextureRegion

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

    fun getState(hud: Hud): GameScreen.State {
        if (HP <= 0) return GameScreen.State.Dead
         else
        if (attacking) return GameScreen.State.Attacking else
        if (playerBody.linearVelocity.y > 1.5f) return GameScreen.State.Jumping else
        if ((
                hud.touchpad.knobX < hud.touchpad.width / 2 || hud.touchpad.knobX > hud.touchpad.width / 2)
            ) { return GameScreen.State.Running
        }
        else return GameScreen.State.Standing

    }

    fun detectJumping(hud: Hud){
        if (playerBody.linearVelocity.x < 4f && hud.touchpad.knobX > hud.touchpad.width / 2 + hud.touchpad.width/20) {
            playerBody.applyLinearImpulse(Vector2(.03f, 0f), playerBody.worldCenter, true)
            lastRight = true

            playerBody.fixtureList.forEach {
                if (it.filterData.categoryBits == SWORD_BIT_LEFT && !it.isSensor) it.isSensor = true
                if (it.filterData.categoryBits == SWORD_BIT && it.isSensor) it.isSensor = false
            }
        }

        if (playerBody.linearVelocity.x > -4f && hud.touchpad.knobX < hud.touchpad.width / 2 - hud.touchpad.width/20) {
            playerBody.applyLinearImpulse(Vector2(-.03f, 0f), playerBody.worldCenter, true)
            lastRight = false

            playerBody.fixtureList.forEach {
                if (it.filterData.categoryBits == SWORD_BIT_LEFT && it.isSensor) it.isSensor = false
                if (it.filterData.categoryBits == SWORD_BIT && !it.isSensor) it.isSensor = true
            }

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
        jumping = true
    }

    fun hit(damage: Int, velocity: Vector2){
        if (hitTimer > .5f && stateTimerDead == 0f) {
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




