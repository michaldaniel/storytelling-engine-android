package eu.morningbird.storytellingengine.util

import android.content.Context
import androidx.recyclerview.widget.LinearLayoutManager

class ScrollControlLinearLayout(
    context: Context,
    orientation: Int,
    reversed: Boolean,
    private var scrollEnabled: Boolean = true
) : LinearLayoutManager(context, orientation, reversed) {
    override fun canScrollHorizontally(): Boolean {
        return scrollEnabled
    }

    override fun canScrollVertically(): Boolean {
        return scrollEnabled
    }

}