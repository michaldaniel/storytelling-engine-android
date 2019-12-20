package eu.morningbird.storytellingengine.database.dao

import androidx.room.Dao
import androidx.room.Query
import eu.morningbird.storytellingengine.model.Present

@Dao
interface PresentDao : BaseDao<Present> {
    @Query("select * from present where :scene == scene")
    suspend fun get(scene: Int): List<Present>

    @Query("delete from present")
    suspend fun drop()
}