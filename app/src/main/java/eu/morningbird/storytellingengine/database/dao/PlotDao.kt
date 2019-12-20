package eu.morningbird.storytellingengine.database.dao

import androidx.room.Dao
import androidx.room.Query
import eu.morningbird.storytellingengine.model.Plot

@Dao
interface PlotDao : BaseDao<Plot> {
    @Query("select * from plot where version == :version")
    suspend fun get(version: Int): Plot?

    @Query("delete from plot")
    suspend fun drop()
}