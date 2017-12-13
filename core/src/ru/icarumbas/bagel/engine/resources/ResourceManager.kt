package ru.icarumbas.bagel.engine.resources

import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.maps.tiled.TmxMapLoader
import ru.icarumbas.bagel.engine.world.MAPS_TOTAL


class ResourceManager {

    val assetManager: AssetManager = AssetManager()

    fun getTextureAtlas(path: String) = assetManager.get<TextureAtlas>(path)!!

    fun getTiledMap(path: String) = assetManager.get<TiledMap>(path)

    fun loadTextureAtlas(path: String) = assetManager.load(path, TextureAtlas::class.java)

    fun loadAssets(){

        // Texture Atlases
        loadTextureAtlas("Packs/GuyKnight.pack")
        loadTextureAtlas("Packs/Main_Menu.txt")
        loadTextureAtlas("Packs/items.pack")
        loadTextureAtlas("Packs/Enemies/Skeleton.pack")
        loadTextureAtlas("Packs/Enemies/Golem.pack")
        loadTextureAtlas("Packs/Enemies/Vamp.pack")
        loadTextureAtlas("Packs/Enemies/Zombie.pack")
        loadTextureAtlas("Packs/weapons.pack")
        loadTextureAtlas("Packs/Enemies/MiniDragon.pack")
        loadTextureAtlas("Packs/minimap.pack")
        loadTextureAtlas("Packs/UI.pack")

        with (assetManager) {

            // Rooms
            setLoader(TiledMap::class.java, TmxMapLoader(InternalFileHandleResolver()))
            (0 until MAPS_TOTAL).forEach {
                load("Maps/Map$it.tmx", TiledMap::class.java, TmxMapLoader.Parameters().apply {
                    generateMipMaps = true
                })
            }
        }
    }
}