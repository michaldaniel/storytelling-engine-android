package eu.morningbird.storytellingengine.model.json

import com.fasterxml.jackson.annotation.JsonProperty

data class Present(
    @JsonProperty("left")
    var left: List<String>,
    @JsonProperty("right")
    var right: List<String>
)