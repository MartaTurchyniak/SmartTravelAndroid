package com.magic.smarttravel.screens.new_group

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.magic.smarttravel.R
import com.magic.smarttravel.screens.general.ShortNameFormatter
import kotlinx.android.synthetic.main.contact_item.view.*

/**
 * Created by Marta Turchyniak on 5/22/20.
 */

class ContactsAdapter(
    private val list: MutableList<ContactModel>,
    private val showSelected: Boolean, val listener: InteractionListener?
) :
    RecyclerView.Adapter<ContactsAdapter.ContactsViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactsViewHolder {
        return ContactsViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.contact_item,
                parent,
                false
            )
        )

    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ContactsViewHolder, position: Int) {
        holder.bindView()
    }

    inner class ContactsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        init {
            itemView.setOnClickListener {
                listener?.onClick(list[adapterPosition])
                notifyItemChanged(adapterPosition)
            }
        }

        fun bindView() {
            val model = list[adapterPosition]
            itemView.ivPhoto.background =
                if (adapterPosition % 2 == 0) ContextCompat.getDrawable(
                    itemView.context,
                    R.drawable.buble_pink
                ) else ContextCompat.getDrawable(
                    itemView.context, R.drawable.turqios_buble
                )
            itemView.ivPhoto.text = model.name?.let { ShortNameFormatter.format(it) }
            itemView.name.text = model.name
            itemView.mobileNumber.text = model.mobileNumber
            initSelectedView(model)
        }

        private fun initSelectedView(model: ContactModel) {
            if (showSelected) {
                itemView.rootView.setBackgroundColor(
                    ContextCompat.getColor(
                        itemView.context,
                        R.color.grey_100
                    )
                )
                itemView.selected.visibility = View.VISIBLE
                if (model.isSelected!!) itemView.selected.setImageDrawable(
                    itemView.context.getDrawable(
                        R.drawable.ic_check_circle
                    )
                )
                else {
                    itemView.selected.setImageDrawable(itemView.context.getDrawable(R.drawable.ic_radio_button_unchecked))
                    itemView.rootView.setBackgroundColor(
                        ContextCompat.getColor(
                            itemView.context,
                            android.R.color.white
                        )
                    )
                }
            } else {
                itemView.selected.visibility = View.GONE
            }
        }
    }

    interface InteractionListener {

        fun onClick(contact: ContactModel)
    }
}