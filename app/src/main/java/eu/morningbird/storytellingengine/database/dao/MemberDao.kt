package eu.morningbird.storytellingengine.database.dao

import androidx.room.Dao
import androidx.room.Query
import eu.morningbird.storytellingengine.model.Member

@Dao
interface MemberDao : BaseDao<Member> {
    @Query("delete from member")
    suspend fun drop()

    @Query("select * from member where credit == :credit")
    suspend fun get(credit: Int): List<Member>?
}