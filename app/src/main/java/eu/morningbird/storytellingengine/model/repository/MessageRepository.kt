package eu.morningbird.storytellingengine.model.repository

import eu.morningbird.storytellingengine.MyApplication
import eu.morningbird.storytellingengine.model.Message
import eu.morningbird.storytellingengine.model.Scene


object MessageRepository {
    suspend fun get(scene: Int, message: Int): Message? {
        return MyApplication.database.messageDao().get(scene, message)
    }

    suspend fun get(scene: Int): List<Message>? {
        return MyApplication.database.messageDao().get(scene)?.sortedBy { it.order }
    }

    suspend fun save(message: Message, order: Int, scene: Scene) {
        message.order = order
        message.scene = scene.order
        message.characterName = message.characterSpriteRelation?.name
        message.spriteName = message.characterSpriteRelation?.sprite
        MyApplication.database.messageDao().insert(message)

    }
}