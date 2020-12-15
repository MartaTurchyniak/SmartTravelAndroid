package com.magic.smarttravel.screens.chat

import android.view.LayoutInflater
import android.view.ViewGroup
import com.magic.smarttravel.R
import com.magic.smarttravel.screens.general.DefaultAdapter

/**
 * Created by Marta Turchyniak on 12/1/20.
 */
class ChatAdapter : DefaultAdapter<ChatMessage, ChatViewHolder>() {

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        holder.bind(ChatViewHolder.ChatViewPayload(items[position], items))
    }

    override fun onBindViewHolder(
        holder: ChatViewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {
        holder.bind(ChatViewHolder.ChatViewPayload(items[position], items))
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder =
        when (viewType) {
            TYPE_INCOMING -> ReceivedMessageViewHolder(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.item_message_received,
                    parent,
                    false
                )
            )
            TYPE_OUTGOING -> SentMessageViewHolder(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.item_message_sent,
                    parent,
                    false
                )
            )

            else -> throw IllegalStateException()
        }

    override fun getItemViewType(position: Int): Int {
        val item = items[position]

        return if (item.type == ChatMessage.MessageType.INCOMING) {
            TYPE_INCOMING
        } else {
            TYPE_OUTGOING
        }
    }

    fun insert(message: ChatMessage) {
        this.items.add(message)
        this.notifyItemInserted(this.itemCount - 1)
    }

    companion object {
        private const val TYPE_INCOMING = 0
        private const val TYPE_OUTGOING = 1
    }
}