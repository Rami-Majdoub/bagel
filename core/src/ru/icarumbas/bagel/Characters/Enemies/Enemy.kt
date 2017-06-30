package ru.icarumbas.bagel.Characters.Enemies

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.*
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.*
import ru.icarumbas.*
import ru.icarumbas.bagel.Characters.Coin
import ru.icarumbas.bagel.Characters.Player
import ru.icarumbas.bagel.Screens.GameScreen
import ru.icarumbas.bagel.Utils.WorldCreate.AnimationCreator
import kotlin.experimental.or


abstract class Enemy {

    var killed = false
    var body: Body? = null
    var sprite: Sprite? = null
    val categorybit: Short = ENEMY_BIT
    open val maskbit: Short = PLAYER_BIT or SWORD_BIT or SWORD_BIT_LEFT
    open val gravityScale = 1f

    lateinit var coin: Coin
    val coins = ArrayList<Body>()

    var canHit = false
    var HP = 100
    abstract val strength: Int

    var stateTimer = 0f
    var hitTimer = 0f
    var biteTimer = 0f

    var stateAnimation: Animation<*>? = null
    var runAnimation: Animation<*>? = null

    abstract val speed: Float
    lateinit var velocity: Vector2
    var posX = 0f
    var posY = 0f
    abstract val width: Float
    abstract val height: Float

    @Suppress("Used for JSON Serialization")
    constructor()

    constructor(rectangle: Rectangle) {
        posX = rectangle.x.div(PIX_PER_M)
        posY = rectangle.y.div(PIX_PER_M)
    }

    open fun draw(batch: Batch, delta: Float, gameScreen: GameScreen) {
        if (!killed) {
            hitTimer += delta
            biteTimer += delta
            if (hitTimer > .1f) sprite!!.color = Color.WHITE


            sprite!!.setRegion(getFrame(delta, gameScreen.player))
            sprite!!.setPosition(body!!.position.x.minus(width.div(2)), body!!.position.y.minus(height.div(2)))
            sprite!!.draw(batch)

            if (canHit && gameScreen.player.attacking) hit(gameScreen.player)
            move(gameScreen.player)
            isDead(gameScreen)
        }
        if (coins.isNotEmpty()) coin.updateCoins(coins, batch)

    }

    abstract fun move(player: Player)

    fun isPlayerRight(player: Player) = player.playerBody.position.x > body!!.position.x

    fun isPlayerHigher(player: Player) = player.playerBody.position.y > body!!.position.y

    fun hit(player: Player){
        if (isPlayerRight(player)) {
            body!!.setLinearVelocity(0f, 0f)
            body!!.applyLinearImpulse(Vector2(-20f, 0f), body!!.localPoint2, true)
        }
        else {
            body!!.setLinearVelocity(0f, 0f)
            body!!.applyLinearImpulse(Vector2(20f, 0f), body!!.localPoint2, true)
        }

        HP -= player.strength
        sprite!!.color = Color.RED
        canHit = false
        player.attacking = false
        hitTimer = 0f
    }

    fun bite(player: Player){
        if (biteTimer > .25f) {
            if (isPlayerRight(player)) {
                player.hit(strength, Vector2(.1f, .05f))
                body!!.setLinearVelocity(0f, 0f)
                body!!.applyLinearImpulse(Vector2(15f, 0f), body!!.localPoint2, true)
            } else {
                player.hit(strength, Vector2(-.1f, .05f))
                body!!.setLinearVelocity(0f, 0f)
                body!!.applyLinearImpulse(Vector2(15f, 0f), body!!.localPoint2, true)
            }
            biteTimer = 0f
        }
    }

    fun isDead(gameScreen: GameScreen) {
        if (HP <= 0) {
            coin = Coin(gameScreen.game.assetManager.get("Packs/RoomObjects.txt", TextureAtlas::class.java))
            coin.createCoins(body!!, gameScreen.world, coins, MathUtils.random(3))

            gameScreen.worldContactListener.deleteList.add(body!!)
            sprite = null
            body = null
            killed = true
        }
    }

    fun defineBody(world: World){

        val fixtureDef = FixtureDef()
        val def = BodyDef()
        val shape = PolygonShape()

        def.type = BodyDef.BodyType.DynamicBody
        def.position.x = posX.plus(width.div(2))
        def.position.y = posY.plus(height.div(2))

        shape.setAsBox(width.div(2), height.div(2))
        fixtureDef.shape = shape
        fixtureDef.friction = 0f
        fixtureDef.restitution = .1f
        fixtureDef.density = 10f
        fixtureDef.filter.categoryBits = categorybit
        fixtureDef.filter.maskBits = maskbit

        body = world.createBody(def)
        body!!.createFixture(fixtureDef)
        body!!.isActive = false
        body!!.isFixedRotation = true
        body!!.gravityScale = gravityScale
    }

    private fun getState(): GameScreen.State {
        return GameScreen.State.Standing
    }

    private fun flipOnPlayer(player: Player, region: TextureRegion){

        if (player.x > sprite!!.x && !region.isFlipX){
            region.flip(true, false)
        } else
            if (player.x < sprite!!.x && region.isFlipX){
                region.flip(true, false)
            }
    }

    fun getFrame(delta: Float, player: Player): TextureRegion{
        stateTimer += delta
        val region = stateAnimation!!.getKeyFrame(stateTimer) as TextureRegion
        flipOnPlayer(player, region)


        when (getState()) {
            GameScreen.State.Standing -> return region
            GameScreen.State.Running -> TODO()
            GameScreen.State.Jumping -> TODO()
            GameScreen.State.Attacking -> TODO()
            GameScreen.State.Dead -> TODO()
        }

    }

    open fun loadSprite(textureAtlas: TextureAtlas, animationCreator: AnimationCreator){}

}