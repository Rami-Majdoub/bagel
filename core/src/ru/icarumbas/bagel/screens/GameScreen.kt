package ru.icarumbas.bagel.screens

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.utils.ImmutableArray
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.ScreenAdapter
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.Body
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer
import com.badlogic.gdx.physics.box2d.World
import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.utils.viewport.FitViewport
import ru.icarumbas.*
import ru.icarumbas.bagel.*
import ru.icarumbas.bagel.components.other.*
import ru.icarumbas.bagel.components.physics.BodyComponent
import ru.icarumbas.bagel.components.rendering.AnimationComponent
import ru.icarumbas.bagel.components.rendering.SizeComponent
import ru.icarumbas.bagel.components.velocity.JumpComponent
import ru.icarumbas.bagel.components.velocity.RunComponent
import ru.icarumbas.bagel.screens.scenes.Hud
import ru.icarumbas.bagel.systems.other.RoomChangingSystem
import ru.icarumbas.bagel.systems.other.StateSwapSystem
import ru.icarumbas.bagel.systems.other.WeaponSystem
import ru.icarumbas.bagel.systems.physics.AwakeSystem
import ru.icarumbas.bagel.systems.physics.ContactSystem
import ru.icarumbas.bagel.systems.rendering.*
import ru.icarumbas.bagel.systems.velocity.JumpingSystem
import ru.icarumbas.bagel.systems.velocity.RunningSystem


class GameScreen(newWorld: Boolean, game: Bagel): ScreenAdapter() {

    var currentMapId = 0


    var rooms = ArrayList<Room>()
    private val world = World(Vector2(0f, -9.8f), true)
    private val hud = Hud()
    private val debugRenderer : DebugRenderer
    private val engine = Engine()
    val playerBody: Body
    private val mapRenderer: MapRenderer

    init {

        val b2DWorldCreator = B2DWorldCreator(game.assetManager, world)
        val animationCreator = AnimationCreator(game.assetManager)
        val worldCreator: WorldCreator = WorldCreator(game.assetManager)

        if (newWorld) createNewWorld(worldCreator, b2DWorldCreator, game.assetManager) else continueWorld()

        val viewport = FitViewport(REG_ROOM_WIDTH, REG_ROOM_HEIGHT, OrthographicCamera(REG_ROOM_WIDTH, REG_ROOM_HEIGHT))
        val orthoRenderer = OrthogonalTiledMapRenderer(game.assetManager.get(rooms[currentMapId].path), 0.01f)

        mapRenderer = MapRenderer(orthoRenderer, this, game.assetManager, viewport)
        debugRenderer = DebugRenderer(Box2DDebugRenderer(), world, viewport)
        playerBody = b2DWorldCreator.createPlayerBody()

        val contactSystem = ContactSystem(hud)
        world.setContactListener(contactSystem)

        // Other
        engine.addSystem(RoomChangingSystem(this))
        engine.addSystem(StateSwapSystem(this))

        // Velocity
        engine.addSystem(RunningSystem(hud))
        engine.addSystem(JumpingSystem(hud))

        // Physic
        engine.addSystem(AwakeSystem(this))
        engine.addSystem(contactSystem)
        engine.addSystem(WeaponSystem(hud))

        // Rendering
        engine.addSystem(AnimationSystem(this))
        engine.addSystem(ViewportSystem(viewport, this))
        engine.addSystem(RenderingSystem(this, orthoRenderer.batch))
        engine.addSystem(WeaponRenderingSystem(orthoRenderer.batch))


        engine.addEntity(createPlayerEntity(
                animationCreator,
                game.assetManager["Packs/GuyKnight.pack", TextureAtlas::class.java],
                b2DWorldCreator))

        (0..TILED_MAPS_TOTAL).forEach {
            b2DWorldCreator.loadGround("Maps/Map$it.tmx", "ground", GROUND_BIT, engine)
            b2DWorldCreator.loadGround("Maps/Map$it.tmx", "platform", PLATFORM_BIT, engine)
        }

        Gdx.input.inputProcessor = hud.stage

        println("Size of rooms: ${rooms.size}")
        println("World bodies count: ${world.bodyCount}")
    }

    override fun render(delta: Float) {
        world.step(1/45f, 6, 2)
        mapRenderer.render()
        engine.update(delta)
        debugRenderer.render()
        hud.draw(this)
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

    private fun createPlayerEntity(animCreator: AnimationCreator,
                                   atlas: TextureAtlas,
                                   b2DWorldCreator: B2DWorldCreator): Entity {


        val entity = Entity()
                .add(PlayerComponent(0))
                .add(RunComponent())
                .add(ParametersComponent(
                        HP = 100,
                        acceleration = .04f,
                        maxSpeed = 4f,
                        strength = 5,
                        attackSpeed = .5f,
                        knockback = 0f,
                        nearAttackStrength = 0,
                        jumpVelocity = .12f,
                        maxJumps = 5
                        ))
                .add(SizeComponent(110 / PIX_PER_M, 145 / PIX_PER_M))
                .add(JumpComponent())
                .add(DamageComponent())
                .add(BodyComponent(playerBody))
                .add(EquipmentComponent())
                .add(WeaponComponent(
                        WeaponSystem.SWING,
                        b2DWorldCreator.createSwordWeapon(playerBody),
                        animCreator.create("Attack", 10, .05f, Animation.PlayMode.LOOP, atlas)))
                .add(StateComponent(ImmutableArray<String>(Array.with(
                        StateSwapSystem.AllStates.RUNNING,
                        StateSwapSystem.AllStates.JUMPING,
                        StateSwapSystem.AllStates.STANDING,
                        StateSwapSystem.AllStates.ATTACKING,
                        StateSwapSystem.AllStates.DEAD))))
                .add(AnimationComponent(hashMapOf(
                        StateSwapSystem.AllStates.RUNNING to animCreator.create("Run", 10, .075f, Animation.PlayMode.LOOP, atlas),
                        StateSwapSystem.AllStates.JUMPING to animCreator.create("Jump", 10, .15f, Animation.PlayMode.LOOP, atlas),
                        StateSwapSystem.AllStates.STANDING to animCreator.create("Idle", 10, .1f, Animation.PlayMode.LOOP, atlas),
                        StateSwapSystem.AllStates.DEAD to animCreator.create("Dead", 10, .1f, Animation.PlayMode.LOOP, atlas)
                )))

        return entity
    }

    private fun createNewWorld(worldCreator: WorldCreator, b2DWorldCreator: B2DWorldCreator, assetManager: AssetManager) {
        rooms.add(Room())
        rooms[currentMapId].meshVertices = intArrayOf(25, 25, 25, 25)
        rooms[currentMapId].loadMap("Maps/Map4.tmx", assetManager, 0)

//        worldCreator.createWorld(100, rooms)

        currentMapId = 0

        rooms.forEach { it.loadEntities(b2DWorldCreator, engine) }
    }

    private fun continueWorld() {
        /*rooms = game.worldIO.loadRoomsFromJson("roomsFile.Json")
        currentMap = game.worldIO.preferences.getInteger("CurrentMap")
        player.money = game.worldIO.preferences.getInteger("Money")
        game.worldIO.loadLastPlayerState(player)
        player.HP = game.worldIO.preferences.getInteger("HP")*/

    }

}
