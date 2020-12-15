package com.magic.smarttravel.screens.chat

import android.annotation.SuppressLint
import android.view.View
import android.widget.TextView
import com.magic.smarttravel.R

/**
 * Created by Marta Turchyniak on 12/1/20.
 */
open class TextMessageViewHolder(view: View) : ChatViewHolder(view) {

    private val tvMessage: TextView = view.findViewById(R.id.tvMessage)
    private val tvMessageSender: TextView = view.findViewById(R.id.tvMessageSender)

    @SuppressLint("SetTextI18n")
    override fun bind(data: ChatViewPayload) {
        super.bind(data)
        tvMessage.text = data.message.text
        tvMessageSender.text = "${data.message.sender.firstName} ${data.message.sender.lastName}"
    }
}

class ReceivedMessageViewHolder(view: View) : TextMessageViewHolder(view) {

    private val tvSenderShortName: TextView = view.findViewById(R.id.tvSenderShortName)

    override fun bind(data: ChatViewPayload) {
        super.bind(data)
        tvSenderShortName.text =
            "${data.message.sender.firstName.first()}${data.message.sender.lastName.first()}"
    }
}

class SentMessageViewHolder(view: View) : TextMessageViewHolder(view)