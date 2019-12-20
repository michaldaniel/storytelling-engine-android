package eu.morningbird.storytellingengine.model.repository

import eu.morningbird.storytellingengine.MyApplication
import eu.morningbird.storytellingengine.model.Position
import eu.morningbird.storytellingengine.model.Scene

object SceneRepository {
    suspend fun get(order: Int): Scene? {
        return MyApplication.database.sceneDao().get(order)
    }

    suspend fun save(scene: Scene, order: Int) {
        scene.order = order
        MyApplication.database.sceneDao().insert(scene)
        scene.messages?.let { messages ->
            for ((index, message) in messages.withIndex()) {
                MessageRepository.save(message, index + 1, scene)
            }
        }
        scene.present?.let { present ->
            for (person in present.left) {
                PresentRepository.save(person, scene, Position.LEFT)
            }
            for (person in present.right) {
                PresentRepository.save(person, scene, Position.RIGHT)
            }
        }


    }

    suspend fun getChapters(): List<Scene>? {
        return MyApplication.database.sceneDao().getChapters()
    }
}