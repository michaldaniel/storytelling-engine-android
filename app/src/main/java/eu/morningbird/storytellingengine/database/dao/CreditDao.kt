package eu.morningbird.storytellingengine.database.dao

import androidx.room.Dao
import androidx.room.Query
import eu.morningbird.storytellingengine.model.Credit

@Dao
interface CreditDao : BaseDao<Credit> {
    @Query("delete from credit")
    suspend fun drop()

    @Query("select * from credit")
    suspend fun get(): List<Credit>?
}