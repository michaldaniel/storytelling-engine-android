package eu.morningbird.storytellingengine.adapter

import android.content.Context
import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import eu.morningbird.storytellingengine.R
import eu.morningbird.storytellingengine.model.Scene
import eu.morningbird.storytellingengine.model.converter.asRomanNumeral
import kotlinx.android.synthetic.main.item_chapter.view.*

class ChaptersAdapter(private val context: Context, private val listener: OnItemCliclListener) :
    RecyclerView.Adapter<ChaptersAdapter.ViewHolder>() {

    interface OnItemCliclListener {
        fun onClick(view: View, scene: Scene)
    }

    var data: List<Scene> = ArrayList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    fun getItem(position: Int): Scene {
        return data[position]
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_chapter, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindView(position)
    }

    inner class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        fun bindView(position: Int) {
            val item = getItem(position)
            view.chapterItemNumberTextView.text = view.context.getString(
                R.string.chapter_with_numeral,
                (position + 1).asRomanNumeral()
            )
            view.chapterItemNameTextView.text =
                item.name?.let { it } ?: run { view.context.getString(R.string.unnamed_chapter) }
            view.chapterItemIconImageView.setImageResource(
                if (item.unlocked) {
                    R.drawable.ic_play
                } else {
                    R.drawable.ic_lock
                }
            )
            view.chapterItemIconImageView.imageTintList = ColorStateList.valueOf(
                view.context.getColor(
                    if (item.unlocked) {
                        R.color.colorAccent
                    } else {
                        R.color.colorTextOnWhiteSecondary
                    }
                )
            )

            if (item.unlocked) {
                view.setOnClickListener { view -> listener.onClick(view, item) }
            } else {
                view.setOnClickListener(null)
            }
        }
    }
}