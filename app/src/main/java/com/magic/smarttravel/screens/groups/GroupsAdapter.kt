package com.magic.smarttravel.screens.groups

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.magic.smarttravel.R
import com.magic.smarttravel.entity.Group
import com.magic.smarttravel.screens.general.ShortNameFormatter
import kotlinx.android.synthetic.main.item_group.view.*

/**
 * Created by Marta Turchyniak on 5/22/20.
 */
class GroupsAdapter(val listener: InteractionListener?) :
    RecyclerView.Adapter<GroupsAdapter.GroupViewHolder>() {

    var list: List<Group>? = null
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroupViewHolder {
        return GroupViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_group,
                parent,
                false
            )
        )

    }

    override fun getItemCount(): Int {
        return list?.size ?: 0
    }

    override fun onBindViewHolder(holder: GroupViewHolder, position: Int) {
        holder.bindView()
    }

    inner class GroupViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        init {
            itemView.setOnClickListener {
                list?.let {
                    listener?.onClick(it[adapterPosition])
                    notifyItemChanged(adapterPosition)
                }
            }
        }

        fun bindView() {
            list?.let { list ->
                val model = list[adapterPosition]
                itemView.ivInitial.background =
                    if (adapterPosition % 2 == 0) ContextCompat.getDrawable(
                        itemView.context,
                        R.drawable.buble_pink
                    ) else ContextCompat.getDrawable(
                        itemView.context, R.drawable.turqios_buble
                    )
                itemView.ivInitial.text = model.name.let { ShortNameFormatter.format(it) }
                itemView.groupName.text = model.name
            }
        }
    }

    interface InteractionListener {

        fun onClick(group: Group)
    }
}