package ru.icarumbas.bagel.Tools.B2dWorldCreator

import com.badlogic.gdx.maps.MapLayer
import com.badlogic.gdx.maps.MapObject
import com.badlogic.gdx.maps.objects.PolylineMapObject
import com.badlogic.gdx.maps.objects.RectangleMapObject
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.physics.box2d.Body
import com.badlogic.gdx.physics.box2d.BodyDef
import com.badlogic.gdx.physics.box2d.ChainShape
import com.badlogic.gdx.physics.box2d.FixtureDef
import com.badlogic.gdx.physics.box2d.PolygonShape
import com.badlogic.gdx.physics.box2d.World
import com.badlogic.gdx.utils.Array

class B2DWorldCreator {

    fun loadBodies(objects: MapLayer, world: World, bodies: ArrayList<Body>, bit: Short) {
        val fixtureDef = FixtureDef()
        val def = BodyDef()

        for (o in objects.objects) {

            if (o is PolylineMapObject) {

                def.type = BodyDef.BodyType.StaticBody
                val vertices = o.polyline.transformedVertices

                for (x in vertices.indices) vertices[x] /= 100f

                val shape2 = ChainShape()
                shape2.createChain(vertices)
                def.position.set(0f, 0f)
                fixtureDef.shape = shape2
                fixtureDef.friction = 1f
                fixtureDef.filter.categoryBits = bit

                bodies.add(world.createBody(def))
                bodies.get(bodies.size - 1).createFixture(fixtureDef)
                bodies.get(bodies.size - 1).isActive = false


            }

            if (o is RectangleMapObject) {

                val shape = PolygonShape()
                val rect = o.rectangle

                def.position.x = (rect.x + rect.width / 2) / 100
                def.position.y = (rect.y + rect.height / 2) / 100
                def.type = BodyDef.BodyType.StaticBody

                shape.setAsBox(rect.width / 2f / 100f, rect.height / 2f / 100f)
                fixtureDef.shape = shape
                fixtureDef.friction = 1f
                fixtureDef.filter.categoryBits = bit

                bodies.add(world.createBody(def))
                bodies.get(bodies.size - 1).createFixture(fixtureDef)

            }

        }

    }
}


