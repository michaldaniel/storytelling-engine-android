package eu.morningbird.storytellingengine.model

import androidx.annotation.Nullable
import androidx.room.*
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import eu.morningbird.storytellingengine.model.repository.CharacterRepository
import eu.morningbird.storytellingengine.model.repository.SpriteRepository

@JsonIgnoreProperties(ignoreUnknown = true)
@Entity(
    tableName = "message",
    indices = [Index(value = ["id"], unique = true), Index(
        value = ["scene", "order"],
        unique = true
    )]
)
data class Message(
    @JsonIgnore
    @ColumnInfo(name = "id")
    @PrimaryKey(autoGenerate = true)
    var id: Int? = null,
    @JsonIgnore
    @ColumnInfo(name = "scene")
    @ForeignKey(
        entity = Scene::class,
        parentColumns = ["order"],
        childColumns = ["scene"],
        onUpdate = ForeignKey.CASCADE,
        onDelete = ForeignKey.CASCADE
    )
    var scene: Int? = null,
    @JsonIgnore
    @ColumnInfo(name = "character_name")
    @ForeignKey(
        entity = Character::class,
        parentColumns = ["name"],
        childColumns = ["character_name"],
        onUpdate = ForeignKey.CASCADE,
        onDelete = ForeignKey.CASCADE
    )
    var characterName: String? = null,
    @JsonIgnore
    @ColumnInfo(name = "sprite")
    @ForeignKey(
        entity = Sprite::class,
        parentColumns = ["name"],
        childColumns = ["sprite_name"],
        onUpdate = ForeignKey.CASCADE,
        onDelete = ForeignKey.CASCADE
    )
    var spriteName: String? = null,
    @JsonIgnore
    @ColumnInfo(name = "order")
    var order: Int? = null,
    @JsonProperty("sound")
    @ColumnInfo(name = "sound")
    @Nullable
    var sound: String? = null,
    @JsonProperty("fullscreen")
    @ColumnInfo(name = "fullscreen")
    var fullscreen: Boolean = false,
    @JsonProperty("text")
    @ColumnInfo(name = "text")
    var text: String
) {
    @JsonProperty("character")
    @Ignore
    var characterSpriteRelation: eu.morningbird.storytellingengine.model.json.Character? = null


    suspend fun fetchCharacter(): Character? {
        return characterName?.let { CharacterRepository.get(it) }
    }

    suspend fun fetchSprite(): Sprite? {
        return characterName?.let { characterName ->
            spriteName?.let { spriteName ->
                SpriteRepository.get(characterName, spriteName)
            }

        }
    }
}