package eu.morningbird.storytellingengine.adapter

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.Rect
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textview.MaterialTextView
import eu.morningbird.storytellingengine.R
import eu.morningbird.storytellingengine.model.*
import eu.morningbird.storytellingengine.view.StoryboardActivity
import eu.morningbird.storytellingengine.view.TypeWriterTextView
import kotlinx.android.synthetic.main.activity_storyboard.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlin.math.roundToInt


class MessagesAdapter(private val context: Context) :
    RecyclerView.Adapter<MessagesAdapter.ViewHolder>() {

    private var messages: MutableList<Pair<Message, Character>> = ArrayList()
    var scene: Int? = null

    private suspend fun updateScene(scene: Int) {
        messages = ArrayList()
        Timeline.chatMessages?.let {
            for (message in it) {
                val character = message.fetchCharacter()
                if (character != null) messages.add(Pair(message, character))
            }
        }
        this@MessagesAdapter.scene = scene
        notifyDataSetChanged()
    }

    private suspend fun updateMessages() {
        Timeline.chatMessages?.let {
            for (position in messages.size until it.size) {
                val character = it[position].fetchCharacter()
                if (character != null) messages.add(Pair(it[position], character))
                if (position > 0) notifyItemChanged(position - 1, View.INVISIBLE)
                notifyItemInserted(position)
            }
        }
    }

    private fun scrollToBottom() {
        if (context is StoryboardActivity) context.chatRecyclerView.scrollToPosition(messages.size - 1)
    }

    fun update(scene: Int) {
        GlobalScope.launch(Dispatchers.Main) {
            if (scene != this@MessagesAdapter.scene) {
                updateScene(scene)
            } else {
                updateMessages()
            }
            scrollToBottom()
        }
    }


    fun getItem(position: Int): Pair<Message, Character>? {
        return try {
            messages[position]
        } catch (e: IndexOutOfBoundsException) {
            null
        }
    }

    override fun getItemCount(): Int {
        return messages.size
    }

    override fun getItemId(position: Int): Long {
        val id = getItem(position)?.first?.id?.toLong()
        if (id != null) return id
        return 0
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val viewHolder: ViewHolder
        val view = when (viewType) {
            Position.RIGHT.code -> {
                LayoutInflater.from(context).inflate(R.layout.item_message_right, parent, false)
            }
            Position.LEFT.code -> {
                LayoutInflater.from(context).inflate(R.layout.item_message_left, parent, false)
            }
            else -> {
                LayoutInflater.from(context).inflate(R.layout.item_message_none, parent, false)
            }
        }
        viewHolder = ViewHolder(view)
        view.tag = viewHolder

        return viewHolder
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindView(position)
        scrollToBottom()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int, payload: List<Any?>) {
        if (payload.isEmpty()) holder.bindView(position)
        else {
            for (load in payload) {
                if (load is Int) holder.setBubbleTriangleViewVisibility(load)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        val item = getItem(position)
        if (item != null) {
            Timeline.characters?.let { characters ->
                val character = characters.find { it.first.name == item.second.name }
                if (character != null) return character.second.code
            }
        }
        return Position.NONE.code
    }

    class OverlapDecoration(private val verticalOverlap: Int) : RecyclerView.ItemDecoration() {


        override fun getItemOffsets(
            outRect: Rect,
            view: View,
            parent: RecyclerView,
            state: RecyclerView.State
        ) {
            val itemPosition = parent.getChildAdapterPosition(view)
            if (itemPosition == 0) {
                return; }
            outRect.set(0, -verticalOverlap, 0, 0)
        }
    }

    inner class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        private var messageTextView: TypeWriterTextView = view.findViewById(R.id.messageTextView)
        private var messageSpacingTextView: MaterialTextView =
            view.findViewById(R.id.messageSpacingTextView)
        private var characterNameTextView: MaterialTextView =
            view.findViewById(R.id.characterNameTextView)
        private var bubbleTriangleView: View = view.findViewById(R.id.bubbleTriangleView)
        private var characterNameConstraintLayout: ConstraintLayout =
            view.findViewById(R.id.characterNameConstraintLayout)
        private var messageBubbleShadowConstraintLayout: ConstraintLayout =
            view.findViewById(R.id.messageBubbleShadowConstraintLayout)

        fun setBubbleTriangleViewVisibility(visibility: Int) {
            bubbleTriangleView.visibility = visibility
        }

        fun bindView(position: Int) {
            val item = getItem(position)
            val previousItem = getItem(position - 1)
            val nextItem = getItem(position + 1)
            if (item != null) {
                messageSpacingTextView.text = item.first.text
                if (nextItem == null) {
                    messageTextView.chunk = (3 * Settings.textAnimationSpeed).roundToInt()
                    messageTextView.animateText = item.first.text
                } else {
                    messageTextView.text = item.first.text
                }
                messageTextView.setTextSize(
                    TypedValue.COMPLEX_UNIT_PX,
                    view.context.resources.getDimension(R.dimen.message_text_base_size) * Settings.fontSize
                )
                messageSpacingTextView.setTextSize(
                    TypedValue.COMPLEX_UNIT_PX,
                    view.context.resources.getDimension(R.dimen.message_text_base_size) * Settings.fontSize
                )
                val character: Character = item.second
                characterNameTextView.text = character.tag
                characterNameTextView.setTextSize(
                    TypedValue.COMPLEX_UNIT_PX,
                    view.context.resources.getDimension(R.dimen.character_badge_text_base_size) * Settings.fontSize
                )
                bubbleTriangleView.backgroundTintList =
                    ColorStateList.valueOf(Color.parseColor(character.color))
                characterNameConstraintLayout.backgroundTintList =
                    ColorStateList.valueOf(Color.parseColor(character.color))
                messageBubbleShadowConstraintLayout.backgroundTintList =
                    ColorStateList.valueOf(Color.parseColor(character.color))

                val previousCharacter: Character? = previousItem?.second
                if (previousCharacter != character) {
                    characterNameConstraintLayout.visibility = View.VISIBLE
                } else {
                    characterNameConstraintLayout.visibility = View.GONE
                }
                if (nextItem == null) {
                    bubbleTriangleView.visibility = View.VISIBLE
                } else {
                    bubbleTriangleView.visibility = View.INVISIBLE
                }
            }
        }
    }
}