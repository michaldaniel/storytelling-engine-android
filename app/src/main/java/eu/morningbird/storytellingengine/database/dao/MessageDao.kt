package eu.morningbird.storytellingengine.database.dao

import androidx.room.Dao
import androidx.room.Query
import eu.morningbird.storytellingengine.model.Message

@Dao
interface MessageDao : BaseDao<Message> {
    @Query("select * from message where scene == :scene")
    suspend fun get(scene: Int): List<Message>?

    @Query("select * from message where scene == :scene and `order` == :order")
    suspend fun get(scene: Int, order: Int): Message?

    @Query("delete from message")
    suspend fun drop()
}