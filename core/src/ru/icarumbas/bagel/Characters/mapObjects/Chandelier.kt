package ru.icarumbas.bagel.Characters.mapObjects


/*
class Chandelier: BreakableMapObject {

    override lateinit var path: String
    override val bit = BREAKABLE_BIT
    override val width = 127f.div(PIX_PER_M)
    override val height = 192f.div(PIX_PER_M)

    private constructor()

    constructor(rectangle: Rectangle) : super(rectangle){

        when (MathUtils.random(1)) {
            0 -> path = "goldenChandelier"
            1 -> path = "silverChandelier"
        }
    }

    override fun onHit(gameScreen: GameScreen) {
        if (!destroyed) {
            if (canBeBroken && body!!.type != BodyDef.BodyType.DynamicBody && gameScreen.player.attacking) {

                body!!.type = BodyDef.BodyType.DynamicBody
                sprite!!.setAlpha(.25f)
                gameScreen.game.assetManager["Sounds/shatterMetal.wav", Sound::class.java].play()

                coin = Coin(gameScreen.game.assetManager.get("Packs/RoomObjects.txt", TextureAtlas::class.java))
                coin!!.createCoins(body!!, gameScreen.world, coins, when (path) {
                    "goldenChandelier" -> MathUtils.random(0, 4)
                    "silverChandelier" -> MathUtils.random(0, 2)
                    else -> throw Exception("Unknown path")
                })

            }
            if (body!!.linearVelocity.y < -3f) {
                super.onHit(gameScreen)
            }
        }
    }
}*/
