package eu.morningbird.storytellingengine.model.util

import android.content.Intent
import android.os.Bundle

data class NavigationDirections(
    val destinationIntent: Intent?,
    val destination: Class<*>?,
    val flags: List<Int>?,
    val bundle: Bundle?,
    val shouldFinishCurrentActivity: Boolean
)