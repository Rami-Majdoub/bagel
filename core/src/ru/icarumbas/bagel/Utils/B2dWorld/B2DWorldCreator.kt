package ru.icarumbas.bagel.Utils.B2dWorld

import com.badlogic.gdx.maps.MapLayer
import com.badlogic.gdx.maps.objects.PolylineMapObject
import com.badlogic.gdx.maps.objects.RectangleMapObject
import com.badlogic.gdx.physics.box2d.*
import ru.icarumbas.PIX_PER_M
import ru.icarumbas.bagel.Characters.mapObjects.*


class B2DWorldCreator {

    fun loadGround(objects: MapLayer, world: World, bodies: ArrayList<Body>, bit: Short) {
        val fixtureDef = FixtureDef()
        val def = BodyDef()

        for (o in objects.objects) {

            if (o is PolylineMapObject) {

                def.type = BodyDef.BodyType.StaticBody
                val vertices = o.polyline.transformedVertices

                for (x in vertices.indices) vertices[x] /= PIX_PER_M

                val shape2 = ChainShape()
                shape2.createChain(vertices)
                fixtureDef.shape = shape2
                fixtureDef.friction = 1f
                fixtureDef.filter.categoryBits = bit

                bodies.add(world.createBody(def))
                bodies[bodies.size - 1].createFixture(fixtureDef)
                bodies[bodies.size - 1].isActive = false

            }

            if (o is RectangleMapObject) {

                val shape = PolygonShape()
                val rect = o.rectangle

                def.position.x = (rect.x + rect.width / 2f) / PIX_PER_M
                def.position.y = (rect.y + rect.height / 2f) / PIX_PER_M
                def.type = BodyDef.BodyType.StaticBody

                shape.setAsBox(rect.width / 2f / PIX_PER_M, rect.height / 2f / PIX_PER_M)
                fixtureDef.shape = shape
                fixtureDef.friction = 1f
                fixtureDef.filter.categoryBits = bit

                bodies.add(world.createBody(def))
                bodies[bodies.size - 1].createFixture(fixtureDef)
                bodies[bodies.size - 1].isActive = false

            }

        }

    }


    fun loadBoxes(layer: MapLayer?, mapObjects: ArrayList<MapObject>) {
        if (layer != null)
        layer.objects
                .filterIsInstance<RectangleMapObject>()
                .forEach {
                    mapObjects.add(Box(it.rectangle))

                }
    }

    fun loadChandeliers(layer: MapLayer?, mapObjects: ArrayList<MapObject>) {
        if (layer != null)
        layer.objects
                .filterIsInstance<RectangleMapObject>()
                .forEach {
                    mapObjects.add(Chandelier(it.rectangle))

                }
    }

    fun loadChests(layer: MapLayer?, mapObjects: ArrayList<MapObject>) {
        if (layer != null)
        layer.objects
                .filterIsInstance<RectangleMapObject>()
                .forEach {
                    mapObjects.add(Chest(it.rectangle))

                }
    }

    fun loadStatues(layer: MapLayer?, mapObjects: ArrayList<MapObject>) {
        if (layer != null)
        layer.objects
                .filterIsInstance<RectangleMapObject>()
                .forEach {
                    mapObjects.add(Statue(it.rectangle))

                }
    }

    fun loadSpikes(layer: MapLayer?, mapObjects: ArrayList<MapObject>) {
        if (layer != null)
        layer.objects
                .filterIsInstance<RectangleMapObject>()
                .forEach {
                    mapObjects.add(Spikes(it.rectangle))

                }
    }


}


