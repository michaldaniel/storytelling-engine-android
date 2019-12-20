package eu.morningbird.storytellingengine.model

import androidx.annotation.Nullable
import androidx.room.*
import com.fasterxml.jackson.annotation.JsonProperty

@Entity(tableName = "character", indices = [Index(value = ["name"], unique = true)])
data class Character(
    @JsonProperty("name")
    @ColumnInfo(name = "name")
    @PrimaryKey
    var name: String,
    @JsonProperty("tag")
    @ColumnInfo(name = "tag")
    var tag: String,
    @JsonProperty("color")
    @ColumnInfo(name = "color")
    @Nullable
    var color: String? = null
) {
    @JsonProperty("sprite")
    @Ignore
    var sprites: List<Sprite>? = null
}

