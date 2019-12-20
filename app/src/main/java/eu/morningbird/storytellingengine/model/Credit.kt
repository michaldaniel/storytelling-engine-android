package eu.morningbird.storytellingengine.model

import androidx.room.*
import com.fasterxml.jackson.annotation.JsonProperty

@Entity(tableName = "credit", indices = [Index(value = ["order"], unique = true)])
data class Credit(
    @JsonProperty("order")
    @ColumnInfo(name = "order")
    @PrimaryKey
    var order: Int,
    @JsonProperty("name")
    @ColumnInfo(name = "name")
    var name: String
) {
    @JsonProperty("member")
    @Ignore
    var members: List<String>? = null
}

