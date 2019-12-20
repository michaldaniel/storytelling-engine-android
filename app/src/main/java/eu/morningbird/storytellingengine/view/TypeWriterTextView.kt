package eu.morningbird.storytellingengine.view

import android.annotation.SuppressLint
import android.content.Context
import android.os.Handler
import android.util.AttributeSet
import androidx.lifecycle.MutableLiveData
import com.google.android.material.textview.MaterialTextView
import eu.morningbird.storytellingengine.util.Event

class TypeWriterTextView(context: Context, attrs: AttributeSet?) :
    MaterialTextView(context, attrs) {

    companion object {
        val typingEvent: MutableLiveData<Event<Boolean>> = MutableLiveData()
    }

    var animateText: CharSequence? = null
        set(value) {
            field = value
            index = 0
            this.text = ""
            typingEvent.postValue(Event(true))
            additionHandler.removeCallbacks(characterAdder)
            additionHandler.postDelayed(characterAdder, delay)
        }

    var delay: Long = 1L
        set(value) {
            field = value
            if (value < 1L) field = 1L
        }

    var chunk: Int = 3
        set(value) {
            field = value
            if (value < 1) field = 1
        }

    private var index: Int = 0

    private var additionHandler: Handler = Handler()
    private var characterAdder: Runnable = AdderRunnable()

    inner class AdderRunnable : Runnable {
        @SuppressLint("SetTextI18n")
        override fun run() {
            animateText?.let {
                index = if (index + chunk < it.length) {
                    index + chunk
                } else {
                    it.length
                }
                text = it.subSequence(0, index)
                if (index < it.length) {
                    additionHandler.postDelayed(characterAdder, delay)
                } else {
                    typingEvent.postValue(Event(false))
                }
            }
        }
    }
}