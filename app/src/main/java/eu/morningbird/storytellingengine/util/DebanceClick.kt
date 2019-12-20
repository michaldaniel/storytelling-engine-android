package eu.morningbird.storytellingengine.util

import android.os.SystemClock
import android.view.View

const val DEBOUNCE_TIME = 200L

fun View.clickWithDebounce(debounceTime: Long = DEBOUNCE_TIME, action: () -> Unit) {
    this.setOnClickListener(object : View.OnClickListener {
        private var lastClickTime: Long = 0

        override fun onClick(v: View) {
            if (SystemClock.elapsedRealtime() - lastClickTime < debounceTime) return
            else action()

            lastClickTime = SystemClock.elapsedRealtime()
        }
    })
}