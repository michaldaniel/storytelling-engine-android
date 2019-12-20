package eu.morningbird.storytellingengine.model

import android.view.View

data class AboutItem(
    val text: String? = null,
    val stringId: Int? = null,
    val icon: Int? = null,
    val url: String? = null,
    val onClickListener: View.OnClickListener? = null
) {
    constructor(
        text: String,
        icon: Int? = null
    ) : this(text, null, icon, null, null)

    constructor(
        stringId: Int,
        icon: Int? = null
    ) : this(null, stringId, icon, null, null)

    constructor(
        text: String,
        icon: Int? = null,
        onClickListener: View.OnClickListener? = null
    ) : this(text, null, icon, null, onClickListener)

    constructor(
        stringId: Int,
        icon: Int? = null,
        onClickListener: View.OnClickListener? = null
    ) : this(null, stringId, icon, null, onClickListener)

    constructor(
        text: String,
        icon: Int? = null,
        url: String? = null
    ) : this(text, null, icon, url, null)

    constructor(
        stringId: Int,
        icon: Int? = null,
        url: String? = null
    ) : this(null, stringId, icon, url, null)
}