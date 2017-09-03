package ru.icarumbas.bagel.screens

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.ScreenAdapter
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.Body
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer
import com.badlogic.gdx.physics.box2d.World
import com.badlogic.gdx.utils.viewport.FitViewport
import ru.icarumbas.Bagel
import ru.icarumbas.REG_ROOM_HEIGHT
import ru.icarumbas.REG_ROOM_WIDTH
import ru.icarumbas.bagel.DebugRenderer
import ru.icarumbas.bagel.MapRenderer
import ru.icarumbas.bagel.RoomManager
import ru.icarumbas.bagel.WorldCleaner
import ru.icarumbas.bagel.creators.AnimationCreator
import ru.icarumbas.bagel.creators.B2DWorldCreator
import ru.icarumbas.bagel.creators.EntityCreator
import ru.icarumbas.bagel.creators.WorldCreator
import ru.icarumbas.bagel.screens.scenes.Hud
import ru.icarumbas.bagel.systems.other.HealthSystem
import ru.icarumbas.bagel.systems.other.RoomChangingSystem
import ru.icarumbas.bagel.systems.other.StateSwapSystem
import ru.icarumbas.bagel.systems.physics.AwakeSystem
import ru.icarumbas.bagel.systems.physics.ContactSystem
import ru.icarumbas.bagel.systems.physics.WeaponSystem
import ru.icarumbas.bagel.systems.rendering.AnimationSystem
import ru.icarumbas.bagel.systems.rendering.RenderingSystem
import ru.icarumbas.bagel.systems.rendering.ViewportSystem
import ru.icarumbas.bagel.systems.velocity.JumpingSystem
import ru.icarumbas.bagel.systems.velocity.RunningSystem


class GameScreen(newWorld: Boolean, game: Bagel): ScreenAdapter() {

    private val world = World(Vector2(0f, -9.8f), true)
    private val hud = Hud()
    private val debugRenderer: DebugRenderer
    private val engine = Engine()
    private val mapRenderer: MapRenderer
    private val worldCleaner: WorldCleaner
    private val entityCreator: EntityCreator
    private val rm: RoomManager

    init {

        val b2DWorldCreator = B2DWorldCreator(world)
        val animationCreator = AnimationCreator(game.assetManager)
        val worldCreator = WorldCreator(game.assetManager)
        entityCreator = EntityCreator(b2DWorldCreator)
        rm = RoomManager(ArrayList(), game.assetManager, entityCreator, engine, animationCreator)

        if (newWorld) rm.createNewWorld(worldCreator, game.assetManager) else rm.continueWorld()
        rm.loadEntities()

        val viewport = FitViewport(REG_ROOM_WIDTH, REG_ROOM_HEIGHT, OrthographicCamera(REG_ROOM_WIDTH, REG_ROOM_HEIGHT))
        val orthoRenderer = OrthogonalTiledMapRenderer(game.assetManager.get(rm.path()), 0.01f)

        mapRenderer = MapRenderer(orthoRenderer, rm, game.assetManager, viewport)
        debugRenderer = DebugRenderer(Box2DDebugRenderer(), world, viewport)

        val playerBody = b2DWorldCreator.createPlayerBody()
        val coins = ArrayList<Body>()
        val entityDeleteList = ArrayList<Entity>()

        worldCleaner = WorldCleaner(entityDeleteList, engine, world)

        val weaponEntityLeft = entityCreator.createSwingWeaponEntity(
                path = "Sword2",
                atlas = game.assetManager["Packs/GuyKnight.pack", TextureAtlas::class.java],
                width = 30,
                height = 100,
                playerBody = playerBody,
                b2DWorldCreator = b2DWorldCreator,
                anchorA = Vector2(-.1f, -.3f),
                anchorB = Vector2(0f, -.5f))

        val weaponEntityRight = entityCreator.createSwingWeaponEntity(
                path = "Sword2",
                atlas = game.assetManager["Packs/GuyKnight.pack", TextureAtlas::class.java],
                width = 30,
                height = 100,
                playerBody = playerBody,
                b2DWorldCreator = b2DWorldCreator,
                anchorA = Vector2(.1f, -.3f),
                anchorB = Vector2(0f, -.5f))

        val contactSystem = ContactSystem(hud)
        world.setContactListener(contactSystem)

        with (engine) {

            // Other
            addSystem(RoomChangingSystem(rm))
            addSystem(StateSwapSystem(rm))
            addSystem(HealthSystem(rm, world, coins, entityDeleteList))

            // Velocity
            addSystem(RunningSystem(hud))
            addSystem(JumpingSystem(hud))

            // Physic
            addSystem(AwakeSystem(rm))
            addSystem(contactSystem)
            addSystem(WeaponSystem(hud))

            // Rendering
            addSystem(AnimationSystem(rm))
            addSystem(ViewportSystem(viewport, rm))
            addSystem(RenderingSystem(rm, orthoRenderer.batch))


            addEntity(weaponEntityRight)
            addEntity(weaponEntityLeft)
            addEntity(entityCreator.createPlayerEntity(
                    animationCreator,
                    game.assetManager["Packs/GuyKnight.pack", TextureAtlas::class.java],
                    playerBody,
                    weaponEntityLeft,
                    weaponEntityRight))
        }


        Gdx.input.inputProcessor = hud.stage

        println("Size of rooms: ${rm.size()}")
        println("World bodies count: ${world.bodyCount}")
    }

    override fun render(delta: Float) {
        world.step(1/45f, 6, 2)
        worldCleaner.update()
        mapRenderer.render()
        engine.update(delta)
        debugRenderer.render()
        hud.draw(rm)
    }

    override fun pause() {
        /*game.worldIO.writeRoomsToJson("roomsFile.Json", rooms, false)

        with(game.worldIO.preferences) {
            putFloat("PlayerPositionX", player.playerBody.position.x)
            putFloat("PlayerPositionY", player.playerBody.position.y)
            putInteger("Money", player.money)
            putInteger("CurrentMap", currentMap)
            putInteger("HP", player.HP)
            flush()
        }

    game.screen = MainMenuScreen(game)
*/
    }

    override fun resize(width: Int, height: Int) {
        hud.stage.viewport.update(width, height, true)
    }

    override fun dispose() {
        world.dispose()
        debugRenderer.dispose()
    }

}
