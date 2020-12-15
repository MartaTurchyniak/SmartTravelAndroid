package com.magic.smarttravel.entity

/**
 * Created by Marta Turchyniak on 5/23/20.
 */
data class TravelUser(
    var id: String = "",
    var phone: String = "",
    var firstName: String? = null,
    var lastName: String? = null,
    var avatar: String? = null,
    var groupIds: MutableList<String> = mutableListOf()
) {

    fun registrationCompleted(): Boolean {
        return firstName != null
    }
}