package ru.icarumbas.bagel.Characters.mapObjects


/*
class Statue: BreakableMapObject {

    override lateinit var path: String
    override val bit = BREAKABLE_BIT
    override val height = 128f.div(PIX_PER_M)

    private constructor()

    constructor(rectangle: Rectangle) : super(rectangle){
        when (MathUtils.random(1)) {
            0 -> path = "goldenStatue"
            1 -> path = "silverStatue"
        }
    }

    override fun onHit(gameScreen: GameScreen) {
        if (canBeBroken && gameScreen.player.attacking && !destroyed) {

            gameScreen.game.assetManager["Sounds/shatterMetal.wav", Sound::class.java].play()

            coin = Coin(gameScreen.game.assetManager.get("Packs/RoomObjects.txt", TextureAtlas::class.java))
            coin!!.createCoins(body!!, gameScreen.world, coins, when (path) {
                "goldenStatue" -> MathUtils.random(0, 5)
                "silverStatue" -> MathUtils.random(0, 2)
                else -> throw Exception("Unknown path")
            })
            super.onHit(gameScreen)

        }
    }
}*/
