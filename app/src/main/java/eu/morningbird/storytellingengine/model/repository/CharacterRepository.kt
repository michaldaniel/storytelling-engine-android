package eu.morningbird.storytellingengine.model.repository

import eu.morningbird.storytellingengine.MyApplication
import eu.morningbird.storytellingengine.model.Character


object CharacterRepository {
    suspend fun save(
        character: Character
    ) {
        MyApplication.database.characterDao().insert(character)
        character.sprites?.let { sprites ->
            for (sprite in sprites) {
                SpriteRepository.save(sprite, character)
            }
        }
    }

    suspend fun get(characterName: String): Character? {
        return MyApplication.database.characterDao().get(characterName)
    }
}