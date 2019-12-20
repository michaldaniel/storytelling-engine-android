package eu.morningbird.storytellingengine.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textview.MaterialTextView
import eu.morningbird.storytellingengine.R
import eu.morningbird.storytellingengine.model.Credit
import eu.morningbird.storytellingengine.model.Member

class CreditsAdapter(private val context: Context) :
    RecyclerView.Adapter<CreditsAdapter.ViewHolder>() {

    var data: List<Any> = ArrayList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    fun getItem(position: Int): Any {
        return data[position]
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = when (viewType) {
            0 -> LayoutInflater.from(context).inflate(R.layout.item_credit_header, parent, false)
            else -> LayoutInflater.from(context).inflate(R.layout.item_credit_member, parent, false)
        }
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindView(position)
    }


    override fun getItemViewType(position: Int): Int {
        val item = getItem(position)
        when (item) {
            is Credit -> return 0
            is Member -> return 1
            else -> return -1
        }
    }

    inner class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        fun bindView(position: Int) {
            val item = getItem(position)
            val textView: MaterialTextView = view.findViewById(R.id.creditsTextView)
            when (item) {
                is Credit -> textView.text = item.name
                is Member -> textView.text = item.name
            }
        }
    }
}