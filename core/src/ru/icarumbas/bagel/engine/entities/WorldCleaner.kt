package ru.icarumbas.bagel.engine.entities


// Refactor. Delete.

/*
class WorldCleaner(private val entityDeleteList: ArrayList<Entity>,
                   private val engine: Engine,
                   private val world: World,
                   private val serializableMapObjects: ArrayList<SerializedMapObject>,
                   private val playerEntity: Entity,
                   private val game: Bagel){

    private val weapon = Mappers.weapon
    private val body = Mappers.body
    private val id = Mappers.roomId
    private val ai = Mappers.AI

    private fun deleteEntities(){
        entityDeleteList.forEach { deletingEntity ->
            world.destroyBody(body[deletingEntity].body)
            if (weapon.has(deletingEntity)){
                world.destroyBody(body[weapon[deletingEntity].entityLeft].body)
                world.destroyBody(body[weapon[deletingEntity].entityRight].body)
            }
            if (id.has(deletingEntity)) {
                serializableMapObjects.remove(id[deletingEntity].serialized)
            }
            engine.getEntitiesFor(Family.all(AIComponent::class.java).get()).forEach {
                if (ai[it].entityTarget == deletingEntity) {
                    ai[it].entityTarget = playerEntity
                }
            }

            if (deletingEntity === playerEntity){
                game.worldIO.prefs.putString("Continue", "No")
                game.worldIO.prefs.flush()
                game.screen = MainMenuScreen(game)
            }

            engine.removeEntity(deletingEntity)

        }

        entityDeleteList.clear()
    }

    fun update(){
        deleteEntities()
    }
}*/