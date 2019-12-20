package eu.morningbird.storytellingengine.model

import androidx.room.*
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty

@Entity(tableName = "sprite", indices = [Index(value = ["character", "name"], unique = true)])
data class Sprite(
    @JsonIgnore
    @ColumnInfo(name = "id")
    @PrimaryKey(autoGenerate = true)
    var id: Int? = null,
    @JsonProperty("name")
    @ColumnInfo(name = "name")
    val name: String,
    @JsonIgnore
    @ColumnInfo(name = "character")
    @ForeignKey(
        entity = Character::class,
        parentColumns = ["name"],
        childColumns = ["character"],
        onUpdate = ForeignKey.CASCADE,
        onDelete = ForeignKey.CASCADE
    )
    var character: String? = null,
    @JsonProperty("graphic")
    @ColumnInfo(name = "graphic")
    val graphic: String
)