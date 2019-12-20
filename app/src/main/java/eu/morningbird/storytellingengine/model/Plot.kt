package eu.morningbird.storytellingengine.model

import androidx.room.*
import com.fasterxml.jackson.annotation.JsonProperty

@Entity(tableName = "plot", indices = [Index(value = ["version"], unique = true)])
data class Plot(
    @JsonProperty("version")
    @ColumnInfo(name = "version")
    @PrimaryKey
    var version: Int
) {
    @JsonProperty("character")
    @Ignore
    var characters: List<Character>? = null

    @JsonProperty("scene")
    @Ignore
    var scenes: List<Scene>? = null

    @JsonProperty("credit")
    @Ignore
    var credits: List<Credit>? = null
}

