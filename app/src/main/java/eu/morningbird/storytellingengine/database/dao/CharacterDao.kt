package eu.morningbird.storytellingengine.database.dao

import androidx.room.Dao
import androidx.room.Query
import eu.morningbird.storytellingengine.model.Character

@Dao
interface CharacterDao : BaseDao<Character> {
    @Query("select * from character")
    suspend fun get(): List<Character>

    @Query("select * from character where :name == name")
    suspend fun get(name: String): Character?

    @Query("delete from character")
    suspend fun drop()
}