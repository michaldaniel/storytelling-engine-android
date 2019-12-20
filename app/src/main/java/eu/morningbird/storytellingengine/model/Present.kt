package eu.morningbird.storytellingengine.model

import androidx.room.*
import eu.morningbird.storytellingengine.model.converter.PositionConverter

@Entity(
    tableName = "present",
    primaryKeys = ["scene", "character"],
    indices = [Index(value = ["scene", "character"], unique = true)]
)
@TypeConverters(PositionConverter::class)
data class Present(
    @ColumnInfo(name = "scene")
    @ForeignKey(
        entity = Scene::class,
        parentColumns = ["order"],
        childColumns = ["scene"],
        onUpdate = ForeignKey.CASCADE,
        onDelete = ForeignKey.CASCADE
    )
    var scene: Int,
    @ColumnInfo(name = "character")
    @ForeignKey(
        entity = Character::class,
        parentColumns = ["name"],
        childColumns = ["character"],
        onUpdate = ForeignKey.CASCADE,
        onDelete = ForeignKey.CASCADE
    )
    var character: String,
    @ColumnInfo(name = "position")
    var position: Position
)

