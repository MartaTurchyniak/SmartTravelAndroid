package com.magic.smarttravel.screens.groups.group_details

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.magic.smarttravel.R
import com.magic.smarttravel.entity.TravelUser
import com.magic.smarttravel.screens.general.ShortNameFormatter
import com.magic.smarttravel.screens.new_group.ContactModel
import kotlinx.android.synthetic.main.contact_item.view.*

/**
 * Created by Marta Turchyniak on 12/10/20.
 */
class GroupDetailsAdapter :
    RecyclerView.Adapter<GroupDetailsAdapter.GroupDetailsViewHolder>() {

    var list: MutableList<TravelUser>? = null
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroupDetailsViewHolder {
        return GroupDetailsViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.contact_item,
                parent,
                false
            )
        )

    }

    override fun getItemCount(): Int {
        return list?.size ?: 0
    }

    override fun onBindViewHolder(holder: GroupDetailsViewHolder, position: Int) {
        holder.bindView()
    }

    inner class GroupDetailsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bindView() {
            list?.let {
                val model = it[adapterPosition]
                itemView.ivPhoto.background =
                    if (adapterPosition % 2 == 0) ContextCompat.getDrawable(
                        itemView.context,
                        R.drawable.buble_pink
                    ) else ContextCompat.getDrawable(
                        itemView.context, R.drawable.turqios_buble
                    )
                val name = if (model.lastName.isNullOrEmpty().not()) {
                    model.firstName + " " + model.lastName
                } else {
                    model.firstName!!
                }
                itemView.ivPhoto.text = ShortNameFormatter.format(name)
                itemView.name.text = name
                itemView.mobileNumber.text = model.phone
                itemView.selected.visibility = View.GONE
            }
        }
    }
}