package eu.morningbird.storytellingengine.database.dao

import androidx.room.Dao
import androidx.room.Query
import eu.morningbird.storytellingengine.model.Scene

@Dao
interface SceneDao : BaseDao<Scene> {
    @Query("select * from scene where `order` == :order")
    suspend fun get(order: Int): Scene?

    @Query("delete from scene")
    suspend fun drop()

    @Query("select * from scene where chapter == 1")
    suspend fun getChapters(): List<Scene>?
}