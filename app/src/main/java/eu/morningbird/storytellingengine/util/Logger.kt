package eu.morningbird.storytellingengine.util

import android.util.Log
import eu.morningbird.storytellingengine.BuildConfig

object Logger {
    enum class Level { DEBUG, ERROR, INFO }

    private const val tag: String = "Logger"

    const val discrepancy_debug = "Discrepancy between assets and json definitions!"

    fun log(str: String, level: Level = Level.DEBUG) {
        if (!BuildConfig.DEBUG) return
        when (level) {
            Level.DEBUG -> Log.d(tag, str)
            Level.ERROR -> Log.e(tag, str)
            Level.INFO -> Log.i(tag, str)
        }
    }

}