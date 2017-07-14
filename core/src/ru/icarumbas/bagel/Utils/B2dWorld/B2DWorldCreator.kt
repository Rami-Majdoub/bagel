package ru.icarumbas.bagel.Utils.B2dWorld

import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.maps.MapLayer
import com.badlogic.gdx.maps.objects.PolylineMapObject
import com.badlogic.gdx.maps.objects.RectangleMapObject
import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.physics.box2d.*
import ru.icarumbas.PIX_PER_M
import ru.icarumbas.bagel.Characters.Enemies.RedBug
import ru.icarumbas.bagel.Characters.Enemies.Enemy
import ru.icarumbas.bagel.Characters.Enemies.Skeleton
import ru.icarumbas.bagel.Characters.mapObjects.*


class B2DWorldCreator {

    fun loadGround(objects: MapLayer, world: World, bodies: ArrayList<Body>, bit: Short) {
        val fixtureDef = FixtureDef()
        val def = BodyDef()

        val chainShape = ChainShape()
        val polygonShape = PolygonShape()


        objects.objects.forEach {

            if (it is PolylineMapObject) {

                val vertices = it.polyline.transformedVertices.clone()

                for (x in vertices.indices) vertices[x] /= PIX_PER_M

                chainShape.createChain(vertices)
                fixtureDef.shape = chainShape
                fixtureDef.friction = 1f
                fixtureDef.filter.categoryBits = bit

                bodies.add(world.createBody(def))
                bodies[bodies.size - 1].createFixture(fixtureDef)
                bodies[bodies.size - 1].isActive = false

            }

            if (it is RectangleMapObject) {

                val rect = it.rectangle

                def.position.x = (rect.x + rect.width / 2f) / PIX_PER_M
                def.position.y = (rect.y + rect.height / 2f) / PIX_PER_M
                def.type = BodyDef.BodyType.StaticBody

                polygonShape.setAsBox(rect.width / 2f / PIX_PER_M, rect.height / 2f / PIX_PER_M)
                fixtureDef.shape = polygonShape
                fixtureDef.friction = 1f
                fixtureDef.filter.categoryBits = bit

                bodies.add(world.createBody(def))
                bodies[bodies.size - 1].createFixture(fixtureDef)
                bodies[bodies.size - 1].isActive = false

            }

        }

    }

    fun loadMapObject(roomPath: String, objectPath: String, assetManager: AssetManager, mapObjects: ArrayList<MapObject>) {
        val layer = assetManager.get(roomPath, TiledMap::class.java).layers[objectPath]
        if (layer != null)
            layer.objects
                    .filterIsInstance<RectangleMapObject>()
                    .forEach {
                        mapObjects.add(when(objectPath){
                            "boxes" -> Box(it.rectangle)
                            "chandeliers" -> Chandelier(it.rectangle)
                            "chests" -> Chest(it.rectangle)
                            "statues" -> Statue(it.rectangle)
                            "spikeTraps" -> SpikeTrap(it.rectangle)
                            "spikes" -> Spike(it.rectangle)
                            "portalDoor" -> PortalDoor(it.rectangle)
                            else -> throw Exception("NO SUCH CLASS")
                        })
                    }
    }

    fun loadEnemies(roomPath: String, objectPath: String, assetManager: AssetManager, mapObjects: ArrayList<Enemy>) {
        val layer = assetManager.get(roomPath, TiledMap::class.java).layers[objectPath]
        if (layer != null)
            layer.objects
                    .filterIsInstance<RectangleMapObject>()
                    .forEach {
                        mapObjects.add(when(objectPath){
                            "flyingEnemies" -> RedBug(it.rectangle)
                            "groundEnemies" -> Skeleton(it.rectangle)
                            else -> throw Exception("NO SUCH CLASS")
                        })
                    }
    }

}


