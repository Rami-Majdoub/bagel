package ru.icarumbas.bagel.Characters.Enemies


class Skeleton {/*


    override val strength = 20
    override val maskbit = GROUND_BIT or PLATFORM_BIT or super.maskbit
    override val width = 145f.div(PIX_PER_M)
    override val height = 220f.div(PIX_PER_M)
    val speed = 3.5f


    var appeared = false
    var attacking = false
    var attackTimer = 0f


    constructor() : super()

    constructor(rectangle: Rectangle) : super(rectangle)

    override fun draw(batch: Batch, delta: Float, gameScreen: GameScreen) {
        if (appearAnimation != null && appearAnimation!!.isAnimationFinished(stateTimer)) {
            appeared = true
        }

        super.draw(batch, delta, gameScreen)
    }

    override fun isDead(gameScreen: GameScreen) {
        if (dieAnimation!!.isAnimationFinished(stateTimer)) super.isDead(gameScreen)
    }

    override fun getState(player: Player): GameScreen.State {
        if (!appeared) {
            if ((player.playerBody.position.x > body!!.position.x - 2 &&
                 player.playerBody.position.x < body!!.position.x + 2 &&
                    player.playerBody.position.y < body!!.position.y + sprite!!.width.div(2) + 1 &&
                    player.playerBody.position.y > body!!.position.y - sprite!!.width.div(2) - 1) || currentState == GameScreen.State.Appearing)
                return GameScreen.State.Appearing
            else
                return GameScreen.State.NULL
        } else {
            if (HP <= 0 || currentState == GameScreen.State.Dead) {
                return GameScreen.State.Dead
            } else
                if (attacking) return GameScreen.State.Attacking
                else
                    if (body!!.linearVelocity.x != 0f) return GameScreen.State.Running
                    else
                        return GameScreen.State.Standing
        }
    }

    fun disappear(player: Player){
        */
/*if (
            player.playerBody.position.x > body!!.position.x + 3f &&
            player.playerBody.position.x < body!!.position.x - 3f) {
            appeared = false
            currentState = GameScreen.State.Appearing
        }*//*

    }

    override fun move(player: Player, delta: Float) {
        if (isPlayerRight(player) && body!!.linearVelocity.x < 1.5f && player.playerBody.position.x > body!!.position.x + 1.5f) {
            body!!.applyLinearImpulse(Vector2(speed, 0f), body!!.localPoint2, true)
        } else
            if (!isPlayerRight(player) && body!!.linearVelocity.x > -1.5f && player.playerBody.position.x < body!!.position.x - 1.5f) {
                body!!.applyLinearImpulse(Vector2(-speed, 0f), body!!.localPoint2, true)
            }

        if (
            player.playerBody.position.x < body!!.position.x + 1.51f &&
            player.playerBody.position.x > body!!.position.x - 1.51f &&
            player.playerBody.position.y < body!!.position.y + sprite!!.width.div(2) &&
            player.playerBody.position.y > body!!.position.y - sprite!!.width.div(2)
            )
        {
            attackTimer += delta
            if (attackTimer > 1.5f) {
                attacking = true
                attackTimer = 0f
            } else {
                if (attackAnimation!!.isAnimationFinished(attackTimer) && attacking) {
                    attacking = false
                    attack(player)
                }
            }
        }
        else {
            if (!attackAnimation!!.isAnimationFinished(attackTimer))
                attackTimer+=delta
            else
                attacking = false
        }

        if (lastState != currentState) {
            stateTimer = 0f
        }

        lastState = currentState

    }

    override fun loadAnimation(textureAtlas: TextureAtlas, animationCreator: AnimationCreator) {
        stateAnimation = Animation(.1f, textureAtlas.findRegions("idle"), Animation.PlayMode.LOOP)
        attackAnimation = Animation(.1f, textureAtlas.findRegions("hit"), Animation.PlayMode.LOOP)
        runAnimation = Animation(.08f, textureAtlas.findRegions("go"), Animation.PlayMode.LOOP)
        dieAnimation = Animation(.25f, textureAtlas.findRegions("die"), Animation.PlayMode.LOOP)
        appearAnimation = Animation(.1f, textureAtlas.findRegions("appear"), Animation.PlayMode.LOOP)

        sprite = Sprite()
    }*/
}
