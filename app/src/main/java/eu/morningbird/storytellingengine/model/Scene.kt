package eu.morningbird.storytellingengine.model

import androidx.room.*
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import eu.morningbird.storytellingengine.model.json.Present

@Entity(tableName = "scene", indices = [Index(value = ["order"], unique = true)])
data class Scene(
    @JsonIgnore
    @ColumnInfo(name = "order")
    @PrimaryKey
    var order: Int? = null,
    @JsonProperty("background")
    @ColumnInfo(name = "background")
    var background: String,
    @JsonProperty("intro")
    @ColumnInfo(name = "intro")
    var intro: String? = null,
    @JsonProperty("outro")
    @ColumnInfo(name = "outro")
    var outro: String? = null,
    @JsonProperty("chapter")
    @ColumnInfo(name = "chapter")
    var chapter: Boolean = false,
    @JsonProperty("name")
    @ColumnInfo(name = "name")
    var name: String? = null,
    @JsonProperty("music")
    @ColumnInfo(name = "music")
    var music: String? = null
) {
    @JsonProperty("present")
    @Ignore
    var present: Present? = null

    @JsonProperty("message")
    @Ignore
    var messages: List<Message>? = null

    val unlocked: Boolean
        get() {
            order?.let {
                if (it <= QuickSave.unlocked) return true
            }
            return false
        }
}