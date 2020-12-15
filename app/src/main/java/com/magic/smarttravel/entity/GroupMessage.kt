package com.magic.smarttravel.entity

import java.util.*

data class GroupMessage(
    var message: String = "",
    var timestamp: Date = Date(),
    var sender: Sender = Sender()
) {

    data class Sender(
        var userId: String = "",
        var firstName: String = "",
        var lastName: String = "",
        val avatar: String? = null
    )
}