package ru.icarumbas.bagel.Characters

import com.badlogic.gdx.graphics.g2d.Sprite
import ru.icarumbas.bagel.AnimationCreator
import ru.icarumbas.bagel.screens.GameScreen

class Player(val gameScreen: GameScreen, animationCreator: AnimationCreator) : Sprite() {
/*

    lateinit var playerBody: Body

    val atlas = gameScreen.game.assetManager["Packs/GuyKnight.pack", TextureAtlas::class.java]!!

    private val stateAnimation: Animation<*>
    private val runAnimation: Animation<*>
    private val jumpAnimation: Animation<*>
    val attackAnimation: Animation<*>
    private val deadAnimation: Animation<*>

    private val stepSound: Sound

    var readyAttack = false
    var canDamage = false

    var collidingWithGround = false
    var doubleJump = 0
    var lastRight = true
    var runningRight = true
    var attackSoundPlayed = false

    var lastState = State.Running
    var currentState = State.Standing

    var stateTimer = 0f

    var hitTimer = 0f
    var isFirstHit = true

    var strength = 35
    var HP = 100
    var money = 0

    enum class State {
        Standing,
        Running,
        Jumping,
        Attacking,
        Dead
    }

    init {
        definePlayer()

        setSize(1.1f, 1.45f)
        setOrigin(width / 2f, height / 2f)

        stepSound = gameScreen.game.assetManager["Sounds/steps.wav", Sound::class.java]

        // Animation
        stateAnimation = animationCreator.create("Idle", 10, .1f, Animation.PlayMode.LOOP, atlas)
        runAnimation = animationCreator.create("Run", 10, .075f, Animation.PlayMode.LOOP, atlas)
        jumpAnimation = animationCreator.create("Jump", 10, .15f, Animation.PlayMode.LOOP, atlas)
        attackAnimation = animationCreator.create("Attack", 10, .05f, Animation.PlayMode.LOOP, atlas)
        deadAnimation = animationCreator.create("Dead", 10, .1f, Animation.PlayMode.LOOP, atlas)

    }

    fun definePlayer() {
        val bodyDef = BodyDef()
        bodyDef.position.set(6f, 5f)

        bodyDef.type = BodyDef.BodyType.DynamicBody
        playerBody = gameScreen.world.createBody(bodyDef)

        val shape = PolygonShape()
        shape.setAsBox(.3f, .6f)

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

        shape.set(.3f, 0f, 1f, 0f)
        playerBody.createFixture(fixtureDef)

        shape.set(.3f, 0f, 1f, -.5f)
        playerBody.createFixture(fixtureDef)

        shape.set(.3f, 0f, 1f, .5f)
        playerBody.createFixture(fixtureDef)

        fixtureDef.filter.categoryBits = SWORD_BIT_LEFT
        shape.set(-.3f, 0f, -1f, 0f)
        playerBody.createFixture(fixtureDef)

        shape.set(-.3f, 0f, -1f, -.5f)
        playerBody.createFixture(fixtureDef)

        shape.set(-.3f, 0f, -1f, .5f)
        playerBody.createFixture(fixtureDef)
    }

    fun update(delta: Float, hud: Hud) {
        hitTimer += delta
        if (hitTimer > .1f) color = Color.WHITE
        if (hitTimer > 1) isFirstHit = true

        currentState = getState(hud)

        if (lastState != currentState) {
            if (currentState == State.Attacking) {
                readyAttack = true
            }
            stateTimer = 0f
        }

        if (stateTimer > attackAnimation.animationDuration / 2 && !attackSoundPlayed && currentState == State.Attacking) {
            gameScreen.game.assetManager["Sounds/sword.wav", Sound::class.java].play()
            canDamage = true
            attackSoundPlayed = true
        }

        if (attackAnimation.animationDuration < stateTimer && currentState == State.Attacking) {
            readyAttack = false
            attackSoundPlayed = false
        }


        lastState = currentState

        // hack to collide with platform
        playerBody.applyLinearImpulse(Vector2(0f, -.00001f), playerBody.localPoint2, true)
        playerBody.applyLinearImpulse(Vector2(0f, .00001f), playerBody.localPoint2, true)

        isDead()
        detectJumping(hud)
        setRegion(getFrame(delta))
        setPosition(playerBody.position.x - width / 2, playerBody.position.y - height / 2)
        hud.hp.setText("HP: $HP")
        hud.money.setText("Money: $money")
    }

    fun isDead(){
        if (currentState == State.Dead && stateTimer > deadAnimation.animationDuration) {
            gameScreen.game.worldIO.preferences.putBoolean("CanContinueWorld", false)
            gameScreen.game.worldIO.preferences.flush()
            gameScreen.game.screen = MainMenuScreen(gameScreen.game)
            gameScreen.hud.stage.clear()

        }
    }

    private fun getFrame(dt: Float): TextureRegion {

        val region = when (currentState) {
            State.Jumping -> jumpAnimation.getKeyFrame(stateTimer) as TextureRegion
            State.Running -> runAnimation.getKeyFrame(stateTimer) as TextureRegion
            State.Standing -> stateAnimation.getKeyFrame(stateTimer) as TextureRegion
            State.Attacking -> attackAnimation.getKeyFrame(stateTimer) as TextureRegion
            State.Dead -> deadAnimation.getKeyFrame(stateTimer) as TextureRegion
            else -> throw Exception("Unknown State")
        }

        if ((playerBody.linearVelocity.x < 0 || !runningRight) && !region.isFlipX && !lastRight) {
            region.flip(true, false)
            runningRight = false
        } else
        if ((playerBody.linearVelocity.x > 0 || runningRight) && region.isFlipX && lastRight) {
            region.flip(true, false)
            runningRight = true
        }

        stateTimer += dt

        return region
    }

    fun setRoomPosition (side: String, plX: Int, plY: Int, previousMapLink: Int, currentMap: Int) {

        if (side == "Up" || side == "Down") {
            // Compare top-right parts of previous and current maps
            val X10 = gameScreen.rooms[currentMap].meshVertices[2]
            val prevX = gameScreen.rooms[previousMapLink].meshVertices[plX]

            if (side == "Up") {
                if (prevX == X10) {
                    playerBody.setTransform(gameScreen.rooms[currentMap].mapWidth - REG_ROOM_WIDTH / 2, 0f, 0f)
                }
                else playerBody.setTransform(REG_ROOM_WIDTH/2, 0f, 0f)
            }
            if (side == "Down") {
                if (prevX == X10) playerBody.setTransform(gameScreen.rooms[currentMap].mapWidth - REG_ROOM_WIDTH / 2,
                                                                 gameScreen.rooms[currentMap].mapHeight, 0f)
                else playerBody.setTransform(REG_ROOM_WIDTH/2, gameScreen.rooms[currentMap].mapHeight, 0f)
            }
        }

        if (side == "Left" || side == "Right") {
            // Compare top parts of previous and current maps
            val Y11 = gameScreen.rooms[currentMap].meshVertices[3]
            val prevY = gameScreen.rooms[previousMapLink].meshVertices[plY]

            if (side == "Left") {
                if (prevY == Y11) playerBody.setTransform(gameScreen.rooms[currentMap].mapWidth,
                                  gameScreen.rooms[currentMap].mapHeight - REG_ROOM_HEIGHT / 2 - height/2, 0f)
                else playerBody.setTransform(gameScreen.rooms[currentMap].mapWidth, REG_ROOM_HEIGHT/2 - height/2, 0f)
            }
            if (side == "Right") {
                if (prevY == Y11)
                    playerBody.setTransform(0f, gameScreen.rooms[currentMap].mapHeight - REG_ROOM_HEIGHT / 2 - height/2, 0f)
                else
                    playerBody.setTransform(0f, REG_ROOM_HEIGHT/2 - height/2, 0f)
            }
        }

    }

    fun getState(hud: Hud): State {
        if (HP <= 0) return State.Dead
         else
        if (readyAttack) return State.Attacking else
        if (playerBody.linearVelocity.y > 1.5f) return State.Jumping else
        if ((hud.touchpad.knobX < hud.touchpad.width / 2 || hud.touchpad.knobX > hud.touchpad.width / 2)) {
            return State.Running
        }
        else return State.Standing

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
        if (playerBody.linearVelocity.y == 0f && (hud.touchpad.knobY <= hud.touchpad.height - hud.touchpad.height/2 + 5 ||
                !collidingWithGround)) {
            doubleJump = 0
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
    }*/

}




