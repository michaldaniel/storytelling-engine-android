package eu.morningbird.storytellingengine.util

open class Event<out T>(private val content: T) {

    private var hasBeenHandled = false

    fun get(): T? {
        return if (hasBeenHandled) {
            null
        } else {
            hasBeenHandled = true
            content
        }
    }

    fun peek(): T = content
}