package com.magic.smarttravel.screens.chat

import android.view.View
import android.view.ViewGroup
import com.magic.smarttravel.R
import com.magic.smarttravel.screens.general.CommonViewHolder

/**
 * Created by Marta Turchyniak on 12/1/20.
 */
open class ChatViewHolder(view: View) : CommonViewHolder<ChatViewHolder.ChatViewPayload>(view) {

    override fun bind(data: ChatViewPayload) {
        super.bind(data)

        val items = data.allItems
        val message = data.message

        val topMargin = if (this.adapterPosition == 0) {
            0f
        } else {
            val previousMessageType = items[adapterPosition - 1].type
            if (previousMessageType == message.type) {
                itemView.context.resources.getDimension(R.dimen.s4)
            } else {
                itemView.context.resources.getDimension(R.dimen.s12)
            }
        }

        val params = itemView.layoutParams
                as ViewGroup.MarginLayoutParams
        params.topMargin = topMargin.toInt()
        itemView.layoutParams = params
    }

    data class ChatViewPayload(
        val message: ChatMessage, val allItems: List<ChatMessage>
    )
}