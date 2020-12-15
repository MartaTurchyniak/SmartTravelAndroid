package com.magic.smarttravel.screens.chat

import com.magic.smarttravel.entity.GroupMessage
import java.util.UUID.randomUUID

/**
 * Created by Marta Turchyniak on 12/1/20.
 */
data class ChatMessage(
    val type: MessageType,
    val text: String,
    val sender: GroupMessage.Sender,
    val id: String = randomUUID().toString()
) {

    enum class MessageType {
        INCOMING,
        OUTGOING
    }
}