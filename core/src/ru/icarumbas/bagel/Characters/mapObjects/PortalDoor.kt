package ru.icarumbas.bagel.Characters.mapObjects


/*
class PortalDoor : MapObject {

    override val bit = PORTAL_DOOR_BIT
    override var path = "doorClosed"
    override val width = 128f.div(PIX_PER_M)
    override val height = 192f.div(PIX_PER_M)
    val roomsWithPortalDoor = ArrayList<Room>()

    var timer = 0f
    var isOpened = false

    @Suppress("Used for JSON Serialization")
    private constructor()

    constructor(rectangle: Rectangle) : super(rectangle)

    private fun changeRoom(gameScreen: GameScreen){
        gameScreen.rooms.forEach {
            room -> room.mapObjects.forEach {
                if (it is PortalDoor && it != this) roomsWithPortalDoor.add(room)
            }
        }

        if (roomsWithPortalDoor.isNotEmpty()) {
            gameScreen.updateRoomObjects(
                    gameScreen.currentMap,
                    gameScreen.rooms.indexOf(roomsWithPortalDoor[MathUtils.random(0, roomsWithPortalDoor.size - 1)]))
        }

        roomsWithPortalDoor.clear()
    }

    private fun open(gameScreen: GameScreen, delta: Float) {
        if ((isOpened && gameScreen.hud.openButtonPressed) || path == "doorOpened") {

            if (path == "doorClosed") {
                path = "doorOpened"
                loadSprite(gameScreen.game.assetManager.get("Packs/RoomObjects.txt", TextureAtlas::class.java))
            }

            timer += delta

            if (timer > .1) {
                isOpened = false
                changeRoom(gameScreen)
                gameScreen.hud.openButtonPressed = false
                path = "doorClosed"
                loadSprite(gameScreen.game.assetManager.get("Packs/RoomObjects.txt", TextureAtlas::class.java))

                gameScreen.rooms[gameScreen.currentMap].mapObjects.forEach {
                    if (it is PortalDoor) {
                        gameScreen.player.playerBody.setTransform(it.body!!.position.x, it.body!!.position.y - .26f, 0f)
                    }
                }

                timer = 0f
            }
        }
    }

    override fun draw(batch: Batch, delta: Float, gameScreen: GameScreen) {
        super.draw(batch, delta, gameScreen)
        open(gameScreen, delta)
    }
}*/
