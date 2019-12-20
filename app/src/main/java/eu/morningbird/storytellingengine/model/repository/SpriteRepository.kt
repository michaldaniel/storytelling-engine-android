package eu.morningbird.storytellingengine.model.repository

import eu.morningbird.storytellingengine.MyApplication
import eu.morningbird.storytellingengine.model.Character
import eu.morningbird.storytellingengine.model.Sprite

object SpriteRepository {
    suspend fun save(sprite: Sprite, character: Character) {
        sprite.character = character.name
        MyApplication.database.spriteDao().insert(sprite)
    }

    suspend fun get(character: String, name: String): Sprite? {
        return MyApplication.database.spriteDao().get(character, name)
    }

}