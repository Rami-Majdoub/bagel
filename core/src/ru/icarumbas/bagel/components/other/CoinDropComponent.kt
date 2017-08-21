package ru.icarumbas.bagel.components.other

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.physics.box2d.Body


data class CoinDropComponent(val coinsCountMax: Int, val coins: ArrayList<Body> = ArrayList()) : Component