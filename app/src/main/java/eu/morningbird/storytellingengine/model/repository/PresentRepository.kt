package eu.morningbird.storytellingengine.model.repository

import eu.morningbird.storytellingengine.MyApplication
import eu.morningbird.storytellingengine.model.Position
import eu.morningbird.storytellingengine.model.Present
import eu.morningbird.storytellingengine.model.Scene

object PresentRepository {
    suspend fun save(person: String, scene: Scene, position: Position) {
        scene.order?.let {
            MyApplication.database.presentDao()
                .insert(Present(scene = it, character = person, position = position))
        }
    }

    suspend fun get(scene: Int): List<Present>? {
        return MyApplication.database.presentDao().get(scene)
    }

}