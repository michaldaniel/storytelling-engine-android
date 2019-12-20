package eu.morningbird.storytellingengine.database.dao

import androidx.room.Dao
import androidx.room.Query
import eu.morningbird.storytellingengine.model.Sprite

@Dao
interface SpriteDao : BaseDao<Sprite> {
    @Query("select * from sprite where :character == character and :name == name")
    suspend fun get(character: String, name: String): Sprite?

    @Query("delete from sprite")
    suspend fun drop()

}