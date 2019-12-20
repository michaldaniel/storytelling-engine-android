package eu.morningbird.storytellingengine.model

import androidx.room.*
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty

@Entity(tableName = "member", indices = [Index(value = ["order", "credit"], unique = true)])
data class Member(
    @JsonIgnore
    @ColumnInfo(name = "id")
    @PrimaryKey(autoGenerate = true)
    var id: Int? = null,
    @JsonIgnore
    @ColumnInfo(name = "order")
    var order: Int,
    @JsonProperty("name")
    @ColumnInfo(name = "name")
    val name: String,
    @JsonIgnore
    @ColumnInfo(name = "credit")
    @ForeignKey(
        entity = Credit::class,
        parentColumns = ["order"],
        childColumns = ["credit"],
        onUpdate = ForeignKey.CASCADE,
        onDelete = ForeignKey.CASCADE
    )
    var credit: Int? = null
)