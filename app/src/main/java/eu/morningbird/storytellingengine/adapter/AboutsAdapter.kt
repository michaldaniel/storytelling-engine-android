package eu.morningbird.storytellingengine.adapter

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import eu.morningbird.storytellingengine.R
import eu.morningbird.storytellingengine.model.AboutItem
import kotlinx.android.synthetic.main.item_about.view.*


class AboutsAdapter(private val context: Context) :
    RecyclerView.Adapter<AboutsAdapter.ViewHolder>() {

    var data: List<AboutItem> = ArrayList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    fun getItem(position: Int): AboutItem {
        return data[position]
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_about, parent, false)
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

            when {
                item.text != null -> {
                    view.aboutItemTextView.text = item.text
                    view.aboutItemTextView.visibility = View.VISIBLE
                }
                item.stringId != null -> {
                    view.aboutItemTextView.text = view.context.getString(item.stringId)
                    view.aboutItemTextView.visibility = View.VISIBLE
                }
                else -> {
                    view.aboutItemTextView.visibility = View.GONE
                }
            }

            when {
                item.onClickListener != null -> {
                    view.aboutItemConstraintLayout.setOnClickListener(item.onClickListener)
                    view.aboutItemUrlImageView.visibility = View.VISIBLE
                }
                item.url != null -> {
                    view.aboutItemConstraintLayout.setOnClickListener {
                        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(item.url))
                        view.context.startActivity(browserIntent)
                    }
                    view.aboutItemUrlImageView.visibility = View.VISIBLE
                }
                else -> {
                    view.aboutItemUrlImageView.visibility = View.GONE
                }
            }

            if(item.icon != null){
                view.aboutItemImageView.setImageDrawable(view.context.getDrawable(item.icon))
                view.visibility = View.VISIBLE
            } else {
                view.visibility = View.GONE
            }



        }
    }
}