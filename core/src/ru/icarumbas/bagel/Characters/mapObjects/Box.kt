package ru.icarumbas.bagel.Characters.mapObjects


/*
open class Box : BreakableMapObject {

    override val bodyType = BodyDef.BodyType.DynamicBody
    override lateinit var path: String
    override val bit = BREAKABLE_BIT

    constructor()

    constructor(rectangle: Rectangle) : super(rectangle){
        when (MathUtils.random(1)) {
            0 -> path = "box"
            1 -> path = "barrel"
        }
    }

    override fun onHit(gameScreen: GameScreen) {
        if (canBeBroken && gameScreen.player.attacking && !destroyed) {

            when (MathUtils.random(1)) {
                0 -> gameScreen.game.assetManager["Sounds/crateBreak0.wav", Sound::class.java].play()
                1 -> gameScreen.game.assetManager["Sounds/crateBreak1.wav", Sound::class.java].play()
            }

            coin = Coin(gameScreen.game.assetManager.get("Packs/RoomObjects.txt", TextureAtlas::class.java))
            coin!!.createCoins(body!!, gameScreen.world, coins, when (MathUtils.random(4)) {
                0 -> 1 // 1 coin
                else -> 0
            })

            super.onHit(gameScreen)

        }
    }


}*/
