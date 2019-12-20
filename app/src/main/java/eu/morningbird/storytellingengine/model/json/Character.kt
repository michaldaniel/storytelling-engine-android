package eu.morningbird.storytellingengine.model.json

import com.fasterxml.jackson.annotation.JsonProperty

data class Character(
    @JsonProperty("name")
    var name: String?,
    @JsonProperty("sprite")
    var sprite: String?
)