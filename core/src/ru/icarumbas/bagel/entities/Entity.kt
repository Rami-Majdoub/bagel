package ru.icarumbas.bagel.entities

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.physics.box2d.Body
import ru.icarumbas.bagel.Screens.GameScreen


abstract class Entity {

    lateinit var sprite: Sprite
    lateinit var body: Body
    lateinit var rect : Rectangle

    constructor(){
        throw Exception("Empty constructor only for deserialization")
    }

    constructor(rectangle: Rectangle){
        rect = rectangle
    }

    fun update(gameScreen: GameScreen){
        sprite.setPosition(body.position.x - rect.width/2, body.position.y - rect.height/2)
    }

    fun draw(batch: Batch){
        sprite.draw(batch)
    }


}