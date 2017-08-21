package ru.icarumbas.bagel.Characters.mapObjects


/*
class Chest: BreakableMapObject{

    override val bit: Short = CHEST_BIT
    override lateinit var path: String
    var isOpened = false

    private constructor()

    constructor(rectangle: Rectangle): super(rectangle){

        when (MathUtils.random(2)) {
            0 -> path = "goldenChest"
            1 -> path = "silverChest"
            2 -> path = "bronzeChest"
        }
    }

    override fun onHit(gameScreen: GameScreen){
        if (isOpened && gameScreen.hud.openButtonPressed) {
            isOpened = false
            destroyed = true

            gameScreen.game.assetManager["Sounds/openchest.wav", Sound::class.java].play()

            coin = Coin(gameScreen.game.assetManager.get("Packs/RoomObjects.txt", TextureAtlas::class.java))
            coin!!.createCoins(body!!, gameScreen.world, coins, count = when (path) {
                "goldenChest" -> 30
                "silverChest" -> 20
                "bronzeChest" -> 10
                else -> throw Exception("Unknown path")
            })
            super.onHit(gameScreen)
        }
    }

}*/
