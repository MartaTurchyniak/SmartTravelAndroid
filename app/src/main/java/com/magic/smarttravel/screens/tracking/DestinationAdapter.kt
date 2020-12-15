package com.magic.smarttravel.screens.tracking

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.android.libraries.places.api.model.AutocompletePrediction
import com.magic.smarttravel.R
import com.magic.smarttravel.screens.new_group.ContactsAdapter
import kotlinx.android.synthetic.main.destination_item.view.*

/**
 * Created by Marta Turchyniak on 12/2/20.
 */
class DestinationAdapter(val listener: InteractionListener) : RecyclerView.Adapter<DestinationAdapter.ViewHolder>() {

    var items: List<AutocompletePrediction>? = null
        set(value) {
            field = value
            notifyDataSetChanged()
        }
    var isStart: Boolean? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.destination_item,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        items?.let {
            holder.bind(it[position])
        }
    }

    override fun getItemCount(): Int {
        return items?.size ?: 0
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        init {
            itemView.setOnClickListener {
                items?.let {
                    isStart?.let { start -> listener.onClick(it[adapterPosition], start) }
                }
                items = null
                notifyItemChanged(adapterPosition)
            }
        }

        fun bind(prediction: AutocompletePrediction) {
            itemView.suggestions.text = prediction.getFullText(null).toString()
        }
    }

    interface InteractionListener{
        fun onClick(prediction: AutocompletePrediction, isStart: Boolean)
    }

}